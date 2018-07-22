package com.vaadin.folderContent;

import com.vaadin.data.Item;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.tTreeContentLayout;
import com.vaadin.tUsefulFuctions;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang3.RandomStringUtils;

import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by kalistrat on 17.05.2017.
 */
public class tAddDeviceWindow extends Window {

    Button SaveButton;
    Button CancelButton;
    TextField EditTextField;
    TextField deviceUID;
    tTreeContentLayout iTreeContentLayout;
    int iLeafId;

    int iNewTreeId;
    int iNewLeafId;
    int iNewUserDeviceId;
    String iNewIconCode;

    NativeSelect TimeZoneSelect;
    TextField TimeSyncInterval;


    public tAddDeviceWindow(int eLeafId
            ,tTreeContentLayout eParentContentLayout
    ){
        iLeafId = eLeafId;
        iTreeContentLayout = eParentContentLayout;

        iNewTreeId = 0;
        iNewLeafId = 0;


        this.setIcon(VaadinIcons.PLUG);
        this.setCaption(" Добавление нового устройства");

        EditTextField = new TextField("Наименование устройства :");
        EditTextField.addStyleName(ValoTheme.TEXTFIELD_SMALL);

        deviceUID = new TextField("UID устройства :");
        deviceUID.addStyleName(ValoTheme.TEXTFIELD_SMALL);

        TimeZoneSelect = new NativeSelect("Часовой пояс устройства :");
        TimeZoneSelect.setNullSelectionAllowed(false);
        tUsefulFuctions.setTimeZoneList(TimeZoneSelect);
        TimeZoneSelect.select("UTC+3");
        TimeSyncInterval = new TextField("Интервал синхронизации времени (в сутках) :");
        TimeSyncInterval.setValue("1");
        TimeSyncInterval.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        StringToIntegerConverter plainIntegerConverter = new StringToIntegerConverter() {
            protected java.text.NumberFormat getFormat(Locale locale) {
                NumberFormat format = super.getFormat(locale);
                format.setGroupingUsed(false);
                return format;
            };
        };
        TimeSyncInterval.setConverter(plainIntegerConverter);
        TimeSyncInterval.addValidator(new IntegerRangeValidator("Значение может изменяться от 1 до 365", 1, 365));
        TimeSyncInterval.setConversionError("Введённое значение не является целочисленным");
        TimeSyncInterval.setNullRepresentation("");
        TimeSyncInterval.setValue("");


        SaveButton = new Button("Сохранить");

        SaveButton.setData(this);
        SaveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        SaveButton.setIcon(FontAwesome.SAVE);
        SaveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                String sErrorMessage = "";
                String sName = EditTextField.getValue();
                String sUID = deviceUID.getValue();
                String sTimeSync = TimeSyncInterval.getValue();



                if (sName == null){
                    sErrorMessage = "Наименование устройства не задано\n";
                }

                if (sName.equals("")){
                    sErrorMessage = sErrorMessage + "Наименование устройства не задано\n";
                }

                if (sUID == null){
                    sErrorMessage = "UID устройства не задан\n";
                }

                if (sUID.equals("")){
                    sErrorMessage = sErrorMessage + "UID устройства не задан\n";
                }

                if (sName.length() > 30){
                    sErrorMessage = sErrorMessage + "Длина наименования превышает 30 символов\n";
                }

                if (tUsefulFuctions.fIsLeafNameBusy(iTreeContentLayout.iUserLog,sName) > 0){
                    sErrorMessage = sErrorMessage + "Указанное наименование уже используется. Введите другое.\n";
                }

                if (!tUsefulFuctions.isSubscriberExists()) {
                    sErrorMessage = sErrorMessage + "Сервер подписки недоступен\n";
                }

                if (!tUsefulFuctions.isDataBaseExists()) {
                    sErrorMessage = sErrorMessage + "Сервер базы данных недоступен\n";
                }

                if (fIsUIDExists(sUID)) {
                    sErrorMessage = sErrorMessage + "Указанный UID уже используется\n";
                }

                int timeSyncInt = 0;

                if (sTimeSync != null){

                    if (tUsefulFuctions.StrToIntValue(sTimeSync)!= null) {

                        timeSyncInt = Integer.parseInt(sTimeSync);

                        if (timeSyncInt < 1) {
                            sErrorMessage = sErrorMessage + "Интервал синхронизации не может быть меньше суток\n";
                        }
                        if (timeSyncInt > 365) {
                            sErrorMessage = sErrorMessage + "Интервал синхронизации превышает 365 суток\n";
                        }

                    } else {
                        sErrorMessage = sErrorMessage + "Интервал синхронизации некорректный\n";
                    }

                } else {
                    sErrorMessage = sErrorMessage + "Не задан интервал синхронизации\n";
                }

                String oWsResponse = tUsefulFuctions.overAllWsCheckUserDevice(sUID,iTreeContentLayout.iUserLog);

                if (oWsResponse != null) {
                    if (oWsResponse.equals("DEVICE_NOT_FOUND")) {
                        sErrorMessage = sErrorMessage + "Устройство с UID " + sUID + " не выпускалось\n";
                    } else if (oWsResponse.equals("WRONG_LOGIN_PASSWORD")) {
                        sErrorMessage = sErrorMessage + "Для пользователя " + iTreeContentLayout.iUserLog + " не синхронизирован пароль в системе\n";
                    } else if (oWsResponse.equals("EXECUTION_ERROR")) {
                        sErrorMessage = sErrorMessage + "Произошла ошибка выполнения\n";
                    } else if (oWsResponse.equals("DEVICE_EXISTS_AND_WAITING")) {
                        sErrorMessage = sErrorMessage + "Устройство  с UID " + sUID + " уже находится в ожидании подключения\n";
                    } else if (oWsResponse.equals("DEVICE_EXISTS_AND_CONNECTED")) {
                        sErrorMessage = sErrorMessage + "Устройство  с UID " + sUID + " уже подключено\n";
                    }
                } else {
                    sErrorMessage = sErrorMessage + "Проверка устройства не может быть произведена\n";
                }

                if (!sErrorMessage.equals("")){
                    Notification.show("Ошибка сохранения:",
                            sErrorMessage,
                            Notification.Type.TRAY_NOTIFICATION);
                } else {

                    String sDeviceLog = iTreeContentLayout.iUserLog + RandomStringUtils.random(5, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
                    String sDevicePass = RandomStringUtils.random(7, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
                    String sDevicePassSha = tUsefulFuctions.sha256(sDevicePass);

                    String oWsSetDeviceRes = tUsefulFuctions.overAllWsSetUserDevice(
                            sUID
                            ,iTreeContentLayout.iUserLog
                            ,"AWAINTING"
                    );

                    if (oWsSetDeviceRes != null) {

                        if (oWsSetDeviceRes.equals("DEVICE_TRANSFERRED_TO_AWAITING_STATUS")) {

                            pAddUserLeafUID(
                                    sName
                                    , sUID
                                    , iTreeContentLayout.iUserLog
                                    , (String) TimeZoneSelect.getValue()
                                    , timeSyncInt
                                    , sDeviceLog
                                    , sDevicePass
                                    , sDevicePassSha
                            );


                            Item newItem = iTreeContentLayout.itTree.TreeContainer.addItem(iNewLeafId);
                            newItem.getItemProperty(1).setValue(iNewTreeId);
                            newItem.getItemProperty(2).setValue(iNewLeafId);
                            newItem.getItemProperty(3).setValue(1);
                            newItem.getItemProperty(4).setValue(sName);
                            newItem.getItemProperty(5).setValue(iNewIconCode);
                            newItem.getItemProperty(6).setValue(0);
                            newItem.getItemProperty(7).setValue(null);
                            newItem.getItemProperty(8).setValue("LEAF");
                            newItem.getItemProperty(9).setValue(sUID);

                            iTreeContentLayout.itTree.TreeContainer.setChildrenAllowed(iNewLeafId, false);
                            iTreeContentLayout.itTree.TreeContainer.setChildrenAllowed(1, true);

                            iTreeContentLayout.itTree.TreeContainer.setParent(iNewLeafId, 1);

                            if (iNewIconCode.equals("FOLDER")) {
                                iTreeContentLayout.itTree.setItemIcon(iNewLeafId, VaadinIcons.FOLDER);
                            }
                            if (iNewIconCode.equals("TACHOMETER")) {
                                iTreeContentLayout.itTree.setItemIcon(iNewLeafId, FontAwesome.TACHOMETER);
                            }
                            if (iNewIconCode.equals("AUTOMATION")) {
                                iTreeContentLayout.itTree.setItemIcon(iNewLeafId, VaadinIcons.AUTOMATION);
                            }
                            if (iNewIconCode.equals("QUESTION")) {
                                iTreeContentLayout.itTree.setItemIcon(iNewLeafId, VaadinIcons.QUESTION_CIRCLE_O);
                            }

                            if (iNewIconCode.equals("CLOSE_CIRCLE")) {
                                iTreeContentLayout.itTree.setItemIcon(iNewLeafId, VaadinIcons.CLOSE_CIRCLE);
                            }


                            iTreeContentLayout.tTreeContentLayoutRefresh(1, 0);
                            iTreeContentLayout.itTree.expandItem(1);


                            Notification.show("Устройство добавлено!",
                                    null,
                                    Notification.Type.TRAY_NOTIFICATION);
                            UI.getCurrent().removeWindow((tAddDeviceWindow) clickEvent.getButton().getData());

                        } else {
                            Notification.show("Ошибка добавления!",
                                    "устройство имеет неверный статус или небыло найдено",
                                    Notification.Type.TRAY_NOTIFICATION);
                        }

                    } else {
                        Notification.show("Ошибка добавления!",
                                "общий веб-сервис недоступен",
                                Notification.Type.TRAY_NOTIFICATION);
                    }

                    }


            }
        });

        CancelButton = new Button("Отменить");

        CancelButton.setData(this);
        CancelButton.addStyleName(ValoTheme.BUTTON_SMALL);
        CancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                UI.getCurrent().removeWindow((tAddDeviceWindow) clickEvent.getButton().getData());
            }
        });

        HorizontalLayout ButtonsLayout = new HorizontalLayout(
                SaveButton
                ,CancelButton
        );

        ButtonsLayout.setSizeUndefined();
        ButtonsLayout.setSpacing(true);

        FormLayout IniDevParamLayout = new FormLayout(
                EditTextField
                ,deviceUID
                ,TimeZoneSelect
                ,TimeSyncInterval
        );

        IniDevParamLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        IniDevParamLayout.setSizeUndefined();
        IniDevParamLayout.addStyleName("FormFont");
        IniDevParamLayout.setMargin(false);

        VerticalLayout MessageLayout = new VerticalLayout(
                IniDevParamLayout
        );
        MessageLayout.setSpacing(true);
        MessageLayout.setWidth("520px");
        MessageLayout.setHeightUndefined();
        MessageLayout.setMargin(new MarginInfo(true,false,true,false));
        MessageLayout.setComponentAlignment(IniDevParamLayout, Alignment.MIDDLE_CENTER);
        MessageLayout.addStyleName(ValoTheme.LAYOUT_CARD);

        VerticalLayout WindowContentLayout = new VerticalLayout(
                MessageLayout
                ,ButtonsLayout
        );
        WindowContentLayout.setSizeUndefined();
        WindowContentLayout.setSpacing(true);
        WindowContentLayout.setMargin(true);
        WindowContentLayout.setComponentAlignment(ButtonsLayout, Alignment.BOTTOM_CENTER);

        this.setContent(WindowContentLayout);
        this.setSizeUndefined();
        this.setModal(true);

    }


    public void pAddUserLeafUID(
        String eName
        ,String eUID
        ,String eUserLog
        ,String eTimeZone
        ,int eSyncTime
        ,String eDeviceLog
        ,String eDevicePass
        ,String eDevicePassSha
    ){
        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement Stmt = Con.prepareCall("{call pAddUserLeafUID(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            Stmt.setString(1, eName);
            Stmt.setString(2, eUID);
            Stmt.setString(3, eUserLog);
            Stmt.setString(4, eTimeZone);
            Stmt.setInt(5, eSyncTime);
            Stmt.setString(6, eDeviceLog);
            Stmt.setString(7, eDevicePass);
            Stmt.setString(8, eDevicePassSha);

            Stmt.registerOutParameter(9, Types.INTEGER);
            Stmt.registerOutParameter(10, Types.INTEGER);

            Stmt.execute();

            iNewTreeId = Stmt.getInt(9);
            iNewLeafId = Stmt.getInt(10);
            iNewIconCode = "QUESTION";

            Con.close();


        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
            //return "Ошибка JDBC";
        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
            //return "Ошибка Class.forName";
        }

    }


    private boolean fIsUIDExists(String eUID){
        boolean IsUIDExists = false;

        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement Stmt = Con.prepareCall("{? = call fIsUIDExists(?)}");
            Stmt.registerOutParameter (1, Types.INTEGER);
            Stmt.setString(2,eUID);
            Stmt.execute();
            if (Stmt.getInt(1) == 1) {
                IsUIDExists = true;
            }
            Con.close();


        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }

        return IsUIDExists;
    }

}
