package com.vaadin;

import com.vaadin.data.Item;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.*;

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

        deviceUID = new TextField("Наименование устройства :");
        deviceUID.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        deviceUID.setIcon(VaadinIcons.KEY);


        SaveButton = new Button("Сохранить");

        SaveButton.setData(this);
        SaveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        SaveButton.setIcon(FontAwesome.SAVE);
        SaveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                String sErrorMessage = "";
                String sFieldValue = EditTextField.getValue();
                String sUID = deviceUID.getValue();



                if (sFieldValue == null){
                    sErrorMessage = "Наименование устройства не задано\n";
                }

                if (sFieldValue.equals("")){
                    sErrorMessage = sErrorMessage + "Наименование устройства не задано\n";
                }

                if (sUID == null){
                    sErrorMessage = "UID устройства не задан\n";
                }

                if (sUID.equals("")){
                    sErrorMessage = sErrorMessage + "UID устройства не задан\n";
                }

                if (sFieldValue.length() > 30){
                    sErrorMessage = sErrorMessage + "Длина наименования превышает 30 символов\n";
                }

                if (tUsefulFuctions.fIsLeafNameBusy(iTreeContentLayout.iUserLog,sFieldValue) > 0){
                    sErrorMessage = sErrorMessage + "Указанное наименование уже используется. Введите другое.\n";
                }

                if (!tUsefulFuctions.isSubscriberExists()) {
                    sErrorMessage = sErrorMessage + "Сервер подписки недоступен\n";
                }

                if (!sErrorMessage.equals("")){
                    Notification.show("Ошибка сохранения:",
                            sErrorMessage,
                            Notification.Type.TRAY_NOTIFICATION);
                } else {

                    //String sFullDeviceTopicName = RootTopicName.getValue()+sChildTopicName;


                       addUserDevice(
                                iLeafId//int qParentLeafId
                                , sFieldValue//String qDeviceName
                                , iTreeContentLayout.iUserLog//String qUserLog
                                , "UNKNOWN" //String qActionTypeName
                                , sUID
                        );

                        String addSubsribeRes = "";
                        String addTaskRes = "";



                        int NewTaskId;

                        NewTaskId = addUserDeviceTask(
                                iNewUserDeviceId
                        , "SYNCTIME"
                        , 1
                        , "DAYS"
                        );

                        addTaskRes = tUsefulFuctions.sendMessAgeToSubcribeServer(
                                NewTaskId
                                , iTreeContentLayout.iUserLog
                                , "add"
                                , "task"
                        );



                        Item newItem = iTreeContentLayout.itTree.TreeContainer.addItem(iNewLeafId);
                        newItem.getItemProperty(1).setValue(iNewTreeId);
                        newItem.getItemProperty(2).setValue(iNewLeafId);
                        newItem.getItemProperty(3).setValue(iLeafId);
                        newItem.getItemProperty(4).setValue(sFieldValue);
                        newItem.getItemProperty(5).setValue(iNewIconCode);
                        newItem.getItemProperty(6).setValue(iNewUserDeviceId);
                        newItem.getItemProperty(7).setValue("UNKNOWN");

                        //iTreeContentLayout.itTree.TreeContainer.setParent(iNewLeafId, iLeafId);
                        iTreeContentLayout.itTree.TreeContainer.setChildrenAllowed(iNewLeafId,false);
                        iTreeContentLayout.itTree.TreeContainer.setChildrenAllowed(iLeafId,true);

                        iTreeContentLayout.itTree.TreeContainer.setParent(iNewLeafId, iLeafId);

                    if (iNewIconCode.equals("FOLDER")) {
                            iTreeContentLayout.itTree.setItemIcon(iNewLeafId, VaadinIcons.FOLDER);
                        }
                        if (iNewIconCode.equals("TACHOMETER")) {
                            iTreeContentLayout.itTree.setItemIcon(iNewLeafId, FontAwesome.TACHOMETER);
                        }
                        if (iNewIconCode.equals("AUTOMATION")) {
                            iTreeContentLayout.itTree.setItemIcon(iNewLeafId, VaadinIcons.AUTOMATION);
                        }

                        iTreeContentLayout.tTreeContentLayoutRefresh(iLeafId, 0);
                        iTreeContentLayout.itTree.expandItem(iLeafId);




                    if (!addSubsribeRes.equals("")) {
                            Notification.show("Устройство добавлено c ошибкой",
                                    addSubsribeRes,
                                    Notification.Type.TRAY_NOTIFICATION);
                            UI.getCurrent().removeWindow((tAddDeviceWindow) clickEvent.getButton().getData());
                        } else if (!addTaskRes.equals("")) {

                            Notification.show("Устройство добавлено c ошибкой",
                                    addTaskRes,
                                    Notification.Type.TRAY_NOTIFICATION);
                            UI.getCurrent().removeWindow((tAddDeviceWindow) clickEvent.getButton().getData());
                        } else {
                            Notification.show("Устройство добавлено!",
                                    null,
                                    Notification.Type.TRAY_NOTIFICATION);
                            UI.getCurrent().removeWindow((tAddDeviceWindow) clickEvent.getButton().getData());
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


    public void addUserDevice(
        int qParentLeafId
        , String qDeviceName
        , String qUserLog
        , String qActionTypeName
        , String qInTopicName
    ){
        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement addDeviceStmt = Con.prepareCall("{call p_add_user_device(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            addDeviceStmt.setInt(1, qParentLeafId);
            addDeviceStmt.setString(2, qDeviceName);
            addDeviceStmt.setString(3, qUserLog);
            addDeviceStmt.setString(4, qActionTypeName);
            addDeviceStmt.registerOutParameter(5, Types.INTEGER);
            addDeviceStmt.registerOutParameter(6, Types.INTEGER);
            addDeviceStmt.registerOutParameter(7, Types.VARCHAR);
            addDeviceStmt.registerOutParameter(8, Types.INTEGER);
            addDeviceStmt.setString(9, qInTopicName);

            addDeviceStmt.execute();

            iNewTreeId = addDeviceStmt.getInt(5);
            iNewLeafId = addDeviceStmt.getInt(6);
            iNewIconCode = addDeviceStmt.getString(7);
            iNewUserDeviceId = addDeviceStmt.getInt(8);

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

    public Integer addUserDeviceTask(
            int qUserDeviceId
            , String eTaskTypeName
            , int eTaskInterval
            , String eIntervalType
    ){
        Integer iTaskId = 0;
        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement addDeviceTaskStmt = Con.prepareCall("{call p_add_task(?, ?, ?, ?, ?, ?)}");
            addDeviceTaskStmt.setInt(1, qUserDeviceId);
            addDeviceTaskStmt.setString(2, eTaskTypeName);
            addDeviceTaskStmt.setInt(3, eTaskInterval);
            addDeviceTaskStmt.setString(4, eIntervalType);
            addDeviceTaskStmt.setNull(5,Types.VARCHAR);
            addDeviceTaskStmt.registerOutParameter(6, Types.INTEGER);

            addDeviceTaskStmt.execute();

            iTaskId = addDeviceTaskStmt.getInt(6);

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
        return iTaskId;

    }
}
