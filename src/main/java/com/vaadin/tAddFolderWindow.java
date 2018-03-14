package com.vaadin;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by kalistrat on 15.05.2017.
 */
public class tAddFolderWindow extends Window {

    Button SaveButton;
    Button CancelButton;
    tTreeContentLayout iTreeContentLayout;
    int iLeafId;
    int iNewTreeId;
    int iNewLeafId;

    TextField NameTextField;
    TextField MqttServerTextField;
    TextField DeviceLoginTextField;
    TextField DevicePassWordTextField;
    HorizontalLayout OutTopicNameField;
    NativeSelect TimeZoneSelect;
    TextField TimeSyncInterval;
    CheckBox SLLCheck;
    Label RootTopicName;
    String mqttRegularUrl;
    String mqttSslUrl;


    public tAddFolderWindow(int eLeafId
            ,tTreeContentLayout eParentContentLayout
    ){
        iLeafId = eLeafId;
        iTreeContentLayout = eParentContentLayout;

        iNewTreeId = 0;
        iNewLeafId = 0;

        mqttRegularUrl = setMqttRegularLink("regular",iTreeContentLayout.iUserLog);
        mqttSslUrl = setMqttRegularLink("ssl",iTreeContentLayout.iUserLog);

        this.setIcon(VaadinIcons.FOLDER_ADD);
        this.setCaption(" Добавление контроллера");

        SaveButton = new Button("Сохранить");

        SaveButton.setData(this);
        SaveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        SaveButton.setIcon(FontAwesome.SAVE);

        SaveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                String sErrorMessage = "";
                String sFieldValue = NameTextField.getValue();
                String sLogValue = DeviceLoginTextField.getValue();
                String sPassValue = DevicePassWordTextField.getValue();
                String sTimeSync = TimeSyncInterval.getValue();

                if (sFieldValue.equals("")){
                    sErrorMessage = "Наименование контроллера не задано\n";
                }

                if (sFieldValue.length() > 30){
                    sErrorMessage = sErrorMessage + "Длина наименования превышает 30 символов\n";
                }

                if (tUsefulFuctions.fIsLeafNameBusy(iTreeContentLayout.iUserLog,sFieldValue) > 0){
                    sErrorMessage = sErrorMessage + "Указанное наименование уже используется. Введите другое.\n";
                }

                if (sLogValue.equals("")){
                    sErrorMessage = sErrorMessage + "Логин контроллера не задан\n";
                } else {
                    if (sLogValue.length() > 50){
                        sErrorMessage = sErrorMessage + "Длина логина превышает 50 символов\n";
                    }
                    if (sLogValue.length() < 5){
                        sErrorMessage = sErrorMessage + "Длина логина меньше 5 символов\n";
                    }
                    if (tUsefulFuctions.isExistsContLogIn(sLogValue).intValue() == 1){
                        sErrorMessage = sErrorMessage + "Указанный логин занят. Введите другой\n";
                    }
                    if (!tUsefulFuctions.IsLatinAndDigits(sLogValue)){
                        sErrorMessage = sErrorMessage + "Логин должен состоять из латиницы и цифр\n";
                    }
                }

                if (sPassValue.equals("")){
                    sErrorMessage = sErrorMessage + "Пароль контроллера не задан\n";
                } else {
                    if (sPassValue.length() > 50){
                        sErrorMessage = sErrorMessage + "Длина пароля превышает 50 символов\n";
                    }
                    if (sPassValue.length() < 5){
                        sErrorMessage = sErrorMessage + "Длина пароля меньше 5 символов\n";
                    }
                    if (!tUsefulFuctions.IsLatinAndDigits(sPassValue)){
                        sErrorMessage = sErrorMessage + "Пароль должен состоять из латиницы и цифр\n";
                    }
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

                }

                if (!sErrorMessage.equals("")){
                    Notification.show("Ошибка сохранения:",
                            sErrorMessage,
                            Notification.Type.TRAY_NOTIFICATION);
                } else {


                    addSubFolder(
                     iLeafId
                    ,sFieldValue
                    ,iTreeContentLayout.iUserLog
                    ,sLogValue
                    ,sPassValue
                    ,tUsefulFuctions.sha256(sPassValue)
                    ,MqttServerTextField.getValue()
                    ,(String) TimeZoneSelect.getValue()
                    ,timeSyncInt
                    ,RootTopicName.getValue()
                    );

                    if (iNewTreeId != 0) {

                        Item newItem = iTreeContentLayout.itTree.TreeContainer.addItem(iNewLeafId);
                        newItem.getItemProperty(1).setValue(iNewTreeId);
                        newItem.getItemProperty(2).setValue(iNewLeafId);
                        newItem.getItemProperty(3).setValue(iLeafId);
                        newItem.getItemProperty(4).setValue(sFieldValue);
                        newItem.getItemProperty(5).setValue("FOLDER");
                        newItem.getItemProperty(6).setValue(0);
                        newItem.getItemProperty(7).setValue(null);


                        iTreeContentLayout.itTree.TreeContainer.setChildrenAllowed(iNewLeafId,false);
                        iTreeContentLayout.itTree.TreeContainer.setParent(iNewLeafId, iLeafId);

                        iTreeContentLayout.itTree.setItemIcon(iNewLeafId, VaadinIcons.FOLDER);
                        iTreeContentLayout.tTreeContentLayoutRefresh(iLeafId,0);
                        iTreeContentLayout.itTree.expandItem(iLeafId);

                        tUsefulFuctions.sendMessAgeToSubcribeServer(
                                iNewLeafId
                                , iTreeContentLayout.iUserLog
                                , "change"
                                , "server"
                        );
                    }

                    Notification.show("Контроллер добавлен!",
                            null,
                            Notification.Type.TRAY_NOTIFICATION);
                    UI.getCurrent().removeWindow((tAddFolderWindow) clickEvent.getButton().getData());

                }


            }
        });

        CancelButton = new Button("Отменить");

        CancelButton.setData(this);
        CancelButton.addStyleName(ValoTheme.BUTTON_SMALL);
        CancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                UI.getCurrent().removeWindow((tAddFolderWindow) clickEvent.getButton().getData());
            }
        });

        HorizontalLayout ButtonsLayout = new HorizontalLayout(
                SaveButton
                ,CancelButton
        );

        ButtonsLayout.setSizeUndefined();
        ButtonsLayout.setSpacing(true);

        NameTextField = new TextField("Наименование контроллера :");
        NameTextField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        NameTextField.setValue("");

        MqttServerTextField = new TextField("mqtt-сервер :");
        MqttServerTextField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        MqttServerTextField.setEnabled(false);
        MqttServerTextField.setValue(mqttRegularUrl);

        DeviceLoginTextField = new TextField("Логин контроллера :");
        DeviceLoginTextField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        DeviceLoginTextField.setValue("");

        DeviceLoginTextField.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                RootTopicName.setValue("/" + DeviceLoginTextField.getValue() + "/synctime");
            }
        });

        DevicePassWordTextField = new TextField("Пароль контроллера :");
        DevicePassWordTextField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        DevicePassWordTextField.setValue("");

//        OutTopicNameField = new TextField("mqtt-топик для синхронизации времени :");
//        OutTopicNameField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        TextField topicfield = new TextField();
        //topicfield.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        topicfield.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);

        RootTopicName = new Label();
        RootTopicName.addStyleName("FormTextLabel");
        //RootTopicName.addStyleName(ValoTheme.LABEL_SMALL);

        OutTopicNameField = new HorizontalLayout(
                RootTopicName
                ,topicfield
        );
        OutTopicNameField.setCaption("mqtt-топик для синхронизации времени :");
        OutTopicNameField.setEnabled(false);

        TimeZoneSelect = new NativeSelect("Часовой пояс контроллера :");
        TimeZoneSelect.setNullSelectionAllowed(false);
        tUsefulFuctions.setTimeZoneList(TimeZoneSelect);
        TimeZoneSelect.select("UTC+3");
        TimeSyncInterval = new TextField("Интервал синхронизации времени (в сутках) :");
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

        SLLCheck = new CheckBox("шифрование трафика (SSL)");
        SLLCheck.addStyleName(ValoTheme.CHECKBOX_SMALL);
        SLLCheck.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if ((Boolean) valueChangeEvent.getProperty().getValue()) {
                    MqttServerTextField.setValue(mqttSslUrl);
                } else {
                    MqttServerTextField.setValue(mqttRegularUrl);
                }
            }
        });


        FormLayout ContParamLayout = new FormLayout(
                NameTextField
                , MqttServerTextField
                , DeviceLoginTextField
                , DevicePassWordTextField
                , OutTopicNameField
                , TimeZoneSelect
                , TimeSyncInterval
                , SLLCheck
        );
        ContParamLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        ContParamLayout.setSizeUndefined();
        ContParamLayout.addStyleName("FormFont");
        ContParamLayout.setMargin(false);

        VerticalLayout MessageLayout = new VerticalLayout(
                ContParamLayout
        );

        MessageLayout.setSpacing(true);
        MessageLayout.setSizeUndefined();
        MessageLayout.setMargin(true);
        MessageLayout.setComponentAlignment(ContParamLayout, Alignment.MIDDLE_CENTER);
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

    public void addSubFolder(
            int qParentLeafId
            ,String qSubFolderName
            ,String qUserLog
            ,String qDeviceLog
            ,String qDevicePass
            ,String qDevicePassSha
            ,String qMqttServerLink
            ,String qTimeZoneVal
            ,int qTimeSyncInt
            ,String qTimeSyncTopic
    ){
        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement addFolderStmt = Con.prepareCall("{call p_add_subfolder(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            addFolderStmt.setInt(1, qParentLeafId);
            addFolderStmt.setString(2, qSubFolderName);
            addFolderStmt.setString(3, qUserLog);
            addFolderStmt.registerOutParameter(4, Types.INTEGER);
            addFolderStmt.registerOutParameter(5, Types.INTEGER);
            addFolderStmt.setString(6, qDeviceLog);
            addFolderStmt.setString(7, qDevicePass);
            addFolderStmt.setString(8, qDevicePassSha);
            addFolderStmt.setString(9, qMqttServerLink);
            addFolderStmt.setInt(10, qTimeSyncInt);
            addFolderStmt.setString(11, qTimeSyncTopic);
            addFolderStmt.setString(12, qTimeZoneVal);
            addFolderStmt.execute();

            iNewTreeId = addFolderStmt.getInt(4);
            iNewLeafId = addFolderStmt.getInt(5);

            Con.close();

        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }

    }

    public String setMqttRegularLink(String qServType, String qUserLog){
        String servLink = null;
        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement callStmt = Con.prepareCall("{? = call f_get_server_link(?,?)}");
            callStmt.registerOutParameter(1, Types.VARCHAR);
            callStmt.setString(2, qServType);
            callStmt.setString(3, qUserLog);

            callStmt.execute();

            servLink =  callStmt.getString(1);

            Con.close();

        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }
        return  servLink;
    }

}
