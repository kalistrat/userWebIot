package com.vaadin.actuatorContent;

import com.vaadin.*;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.notificationContent.tNotificationListLayout;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.sql.*;

/**
 * Created by kalistrat on 26.05.2017.
 */
public class tActuatorStatesLayout extends VerticalLayout
implements addDeleteListenable {
    Button AddButton;
    Button DeleteButton;
    Button SaveButton;

    Table StatesTable;
    IndexedContainer StatesContainer;
    int iUserDeviceId;
    addDeleteListener Listener;
    tTreeContentLayout iParentContentLayout;
    Integer iCurrentLeafId;


    public tActuatorStatesLayout(int eUserDeviceId
            ,tTreeContentLayout eParentContentLayout
            ,Integer eCurrentLeafId
    ){

        iUserDeviceId = eUserDeviceId;
        iParentContentLayout = eParentContentLayout;
        iCurrentLeafId = eCurrentLeafId;

        Label Header = new Label();
        Header.setContentMode(ContentMode.HTML);
        Header.setValue(VaadinIcons.TABLE.getHtml() + "  " + "Перечень возможных состояний устройства");
        Header.addStyleName(ValoTheme.LABEL_COLORED);
        Header.addStyleName(ValoTheme.LABEL_SMALL);

        SaveButton = new Button();
        SaveButton.setIcon(FontAwesome.SAVE);
        SaveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        SaveButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        SaveButton.setEnabled(false);

        SaveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Item ThatItem = (Item) clickEvent.getButton().getData();

                String InputName = ((TextField) ThatItem.getItemProperty(2).getValue()).getValue();
                String InputCode = ((TextField) ThatItem.getItemProperty(3).getValue()).getValue();

                String valueTimeInt = ((TextField) ThatItem.getItemProperty(5).getValue()).getValue();
                String valueNotifyList = ((tNotificationListLayout) ThatItem.getItemProperty(6).getValue()).getValuesStr();


                //System.out.println("InputName :" + InputName);
                //System.out.println("InputCode :" + InputCode);

                String sErrorMessage = "";

                if (InputName == null){
                    sErrorMessage = "Наименование состояния не задано\n";
                }

                if (InputName.equals("")){
                    sErrorMessage = sErrorMessage + "Наименование состояния не задано\n";
                }

                if (InputName.length() > 25){
                    sErrorMessage = sErrorMessage + "Длина наименования превышает 25 символов\n";
                }

                if (isStatesContainerContainsName(InputName)){
                    sErrorMessage = sErrorMessage + "Указанное наименование уже используется. Введите другое.\n";
                }

                if (InputCode == null){
                    sErrorMessage = sErrorMessage + "Код сообщения не задан\n";
                }

                if (InputCode.equals("")){
                    sErrorMessage = sErrorMessage + "Код сообщения не задан\n";
                }

                if (InputCode.length() > 20){
                    sErrorMessage = sErrorMessage + "Длина кода сообщения превышает 20 символов\n";
                }

                if (isStatesContainerContainsCode(InputCode)){
                    sErrorMessage = sErrorMessage + "Указанный код уже используется. Введите другой.\n";
                }

                if (!tUsefulFuctions.IsLatinAndDigits(InputCode)){
                    sErrorMessage = sErrorMessage + "Указанный код недопустим. Он должен состоять из букв латиницы и цифр\n";
                }

                if (tUsefulFuctions.StrToIntValue(valueTimeInt) == null) {
                    sErrorMessage = sErrorMessage + "Значение длительности изменения состояния не задано или не является числом\n";
                } else {
                    if (tUsefulFuctions.StrToIntValue(valueTimeInt) < 5) {
                        sErrorMessage = sErrorMessage + "Значение длительности изменения состояния не может быть менее 5 секунд\n";
                    }
                }

                if (!sErrorMessage.equals("")){
                    Notification.show("Ошибка сохранения:",
                            sErrorMessage,
                            Notification.Type.TRAY_NOTIFICATION);
                } else {
                    ((TextField) ThatItem.getItemProperty(2).getValue()).setEnabled(false);
                    ((TextField) ThatItem.getItemProperty(3).getValue()).setEnabled(false);
                    ((TextField) ThatItem.getItemProperty(5).getValue()).setEnabled(false);
                    ((tNotificationListLayout) ThatItem.getItemProperty(6).getValue()).setEnabledFalse();

                    if (valueNotifyList.equals("")){
                        valueNotifyList = "NONOT";
                    }

                    int newStateId = newActuatorStateInsert(iUserDeviceId
                            ,InputName
                            ,InputCode
                            ,tUsefulFuctions.StrToIntValue(valueTimeInt)
                            ,valueNotifyList
                    );

                    tUsefulFuctions.sendMessAgeToSubcribeServer(
                            newStateId
                            , iParentContentLayout.iUserLog
                            , "add"
                            , "state"
                    );
                    StatesContainerRefresh();
                    DeleteButton.setEnabled(true);
                    AddButton.setEnabled(true);
                    SaveButton.setEnabled(false);
                    Listener.afterAdd(InputName);

                }

            }
        });

        AddButton = new Button();
        AddButton.setIcon(VaadinIcons.PLUS);
        AddButton.addStyleName(ValoTheme.BUTTON_SMALL);
        AddButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);

        AddButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                DeleteButton.setEnabled(false);
                AddButton.setEnabled(false);

                int NewItemNum = StatesContainer.size()+1;

                Item AddedItem = StatesContainer.addItem(NewItemNum);
                AddedItem.getItemProperty(1).setValue(NewItemNum);

                TextField AddedNameTF = new TextField();
                AddedNameTF.setValue("");
                AddedNameTF.addStyleName(ValoTheme.TEXTFIELD_TINY);
                AddedNameTF.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
                AddedNameTF.setWidth("150px");
                AddedItem.getItemProperty(2).setValue(AddedNameTF);

                TextField AddedCodeTF = new TextField();
                AddedCodeTF.setValue("");
                AddedCodeTF.addStyleName(ValoTheme.TEXTFIELD_TINY);
                AddedCodeTF.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
                AddedCodeTF.setWidth("100px");

                AddedItem.getItemProperty(3).setValue(AddedCodeTF);

                Button mqttCommitButton = new Button();
                mqttCommitButton.addStyleName(ValoTheme.BUTTON_SMALL);
                mqttCommitButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
                mqttCommitButton.setIcon(VaadinIcons.PLAY_CIRCLE);

                AddedItem.getItemProperty(4).setValue(mqttCommitButton);

                TextField timeInt = new TextField();
                timeInt.setWidth("50px");
                timeInt.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
                timeInt.setValue("");
                timeInt.setInputPrompt("0");

                AddedItem.getItemProperty(5).setValue(timeInt);

                tNotificationListLayout noteListLay = new tNotificationListLayout();

                AddedItem.getItemProperty(6).setValue(noteListLay);

                SaveButton.setData(AddedItem);
                SaveButton.setEnabled(true);
                DeleteButton.setEnabled(false);
                StatesTable.setPageLength(StatesContainer.size());

            }
        });

        DeleteButton = new Button();
        DeleteButton.setIcon(VaadinIcons.CLOSE_CIRCLE);
        DeleteButton.addStyleName(ValoTheme.BUTTON_SMALL);
        DeleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        DeleteButton.setData(this);

        DeleteButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                int SelectedItemId = 0;
                for (int i=0; i<StatesContainer.size();i++){
                    if (StatesTable.isSelected(i+1)) {
                        SelectedItemId = i+1;
                    }
                }

                if (SelectedItemId>0) {

                    String ItemName = (String) ((TextField) StatesContainer
                            .getItem(SelectedItemId)
                            .getItemProperty(2)
                            .getValue()).getValue();

                    UI.getCurrent().addWindow(new tActuatorStateDeleteWindow(
                          ItemName
                            , (tActuatorStatesLayout) clickEvent.getButton().getData()
                    ));

                } else {
                    Notification.show("Удаление невозможно:",
                            "Не выбрано ни одной строки",
                            Notification.Type.TRAY_NOTIFICATION);
                }



            }
        });


        HorizontalLayout HeaderButtons = new HorizontalLayout(
                DeleteButton
                ,AddButton
                ,SaveButton
        );
        HeaderButtons.setSpacing(true);
        HeaderButtons.setSizeUndefined();

        HorizontalLayout HeaderLayout = new HorizontalLayout(
                Header
                ,HeaderButtons
        );
        HeaderLayout.setWidth("100%");
        HeaderLayout.setHeightUndefined();
        HeaderLayout.setComponentAlignment(Header, Alignment.MIDDLE_LEFT);
        HeaderLayout.setComponentAlignment(HeaderButtons,Alignment.MIDDLE_RIGHT);

        StatesTable = new Table();
        StatesTable.setWidth("100%");

        StatesTable.setColumnHeader(1, "№");
        StatesTable.setColumnHeader(2, "Наименование<br/>состояния");
        StatesTable.setColumnHeader(3, "Код<br/>сообщения");
        StatesTable.setColumnHeader(4, "");
        StatesTable.setColumnHeader(5, "Δt, с");
        StatesTable.setColumnHeader(6, "Системы<br/>оповещения");

        StatesContainer = new IndexedContainer();
        StatesContainer.addContainerProperty(1, Integer.class, null);
        StatesContainer.addContainerProperty(2, TextField.class, null);
        StatesContainer.addContainerProperty(3, TextField.class, null);
        StatesContainer.addContainerProperty(4, Button.class, null);
        StatesContainer.addContainerProperty(5, TextField.class, null);
        StatesContainer.addContainerProperty(6, tNotificationListLayout.class, null);

        setStatesContainer();

        StatesTable.setContainerDataSource(StatesContainer);


        StatesTable.setPageLength(StatesContainer.size());



        StatesTable.addStyleName(ValoTheme.TABLE_COMPACT);
        StatesTable.addStyleName(ValoTheme.TABLE_SMALL);
        StatesTable.addStyleName("TableRow");


        StatesTable.setSelectable(true);


//        StatesTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {
//            @Override
//            public void itemClick(ItemClickEvent itemClickEvent) {
//
//                //String SelectedItemName = ((TextField) itemClickEvent.getItem().getItemProperty(2).getValue()).getValue();
//                //System.out.println("SelectedItemName :" + SelectedItemName);
//
//                SelectedItemId = (Integer) itemClickEvent.getItem().getItemProperty(1).getValue();
//                //System.out.println("SelectedItemId :" + SelectedItemId);
//
//            }
//        });

        VerticalLayout StatesTableLayout = new VerticalLayout(
                StatesTable
        );
        StatesTableLayout.setWidth("100%");
        StatesTableLayout.setHeightUndefined();
        StatesTableLayout.setComponentAlignment(StatesTable,Alignment.MIDDLE_CENTER);

        VerticalLayout ContentLayout = new VerticalLayout(
                HeaderLayout
                ,StatesTableLayout
        );
        ContentLayout.setSpacing(true);
        ContentLayout.setWidth("100%");
        ContentLayout.setHeightUndefined();

        this.addComponent(ContentLayout);

    }

    public void setStatesContainer(){

        try {
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            String DataSql = "select @num1:=@num1+1 num\n" +
                    ",uas.actuator_state_name\n" +
                    ",uas.actuator_message_code\n" +
                    ",uas.user_actuator_state_id\n" +
                    ",(\n" +
                    "select concat(group_concat(nt.notification_code separator '|'),'|')\n" +
                    "from user_device_state_notification uno\n" +
                    "join notification_type nt on nt.notification_type_id=uno.notification_type_id\n" +
                    "where uno.user_actuator_state_id=uas.user_actuator_state_id\n" +
                    ") notification_codes\n" +
                    ",uas.transition_time\n" +
                    "from user_actuator_state uas\n" +
                    "join user_device ud on ud.user_device_id=uas.user_device_id\n" +
                    "join mqtt_servers ser on ser.server_id=ud.mqqt_server_id\n" +
                    "join (select @num1:=0) t1\n" +
                    "where uas.user_device_id = ?";

            PreparedStatement DataStmt = Con.prepareStatement(DataSql);
            DataStmt.setInt(1,iUserDeviceId);

            ResultSet DataRs = DataStmt.executeQuery();

            while (DataRs.next()) {

                Item newItem = StatesContainer.addItem(DataRs.getInt(1));

                TextField NameTF = new TextField();
                NameTF.setValue(DataRs.getString(2));
                NameTF.setEnabled(false);
                NameTF.addStyleName(ValoTheme.TEXTFIELD_TINY);
                NameTF.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
                NameTF.setWidth("150px");

                TextField CodeTF = new TextField();
                CodeTF.setValue(DataRs.getString(3));
                CodeTF.setEnabled(false);
                CodeTF.addStyleName(ValoTheme.TEXTFIELD_TINY);
                CodeTF.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
                CodeTF.setWidth("100px");

                Button mqttCommitButton = new Button();
                mqttCommitButton.addStyleName(ValoTheme.BUTTON_SMALL);
                mqttCommitButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
                mqttCommitButton.setIcon(VaadinIcons.PLAY_CIRCLE);
                mqttCommitButton.setData(DataRs.getInt(4));
                mqttCommitButton.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        int iStateId = (int) clickEvent.getButton().getData();
                        tUsefulFuctions.sendMessAgeToSubcribeServer(
                                iStateId
                                , iParentContentLayout.iUserLog
                                , "add"
                                , "state_message"
                        );
                    }
                });

                TextField timeInt = new TextField();
                timeInt.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
                timeInt.setValue(String.valueOf(DataRs.getInt(6)));
                timeInt.setEnabled(false);
                timeInt.setWidth("50px");

                tNotificationListLayout noteListLay = new tNotificationListLayout();
                noteListLay.setEnabledFalse();

                if (DataRs.getString(5) != null) {
                    for (String iNoteType : tUsefulFuctions.GetListFromString(DataRs.getString(5), "|")) {
                        noteListLay.markNotification(iNoteType);
                    }
                }

                newItem.getItemProperty(1).setValue(DataRs.getInt(1));
                newItem.getItemProperty(2).setValue(NameTF);
                newItem.getItemProperty(3).setValue(CodeTF);
                newItem.getItemProperty(4).setValue(mqttCommitButton);
                newItem.getItemProperty(5).setValue(timeInt);
                newItem.getItemProperty(6).setValue(noteListLay);


            }


            Con.close();

        } catch (SQLException se3) {
            //Handle errors for JDBC
            se3.printStackTrace();
        } catch (Exception e13) {
            //Handle errors for Class.forName
            e13.printStackTrace();
        }

    }

    public void StatesContainerRefresh(){
        StatesContainer.removeAllItems();
        setStatesContainer();
        StatesTable.setPageLength(StatesContainer.size());

    }

    public boolean isStatesContainerContainsName(String NewItemName){
        int k = 0;

        for (int i=0; i<StatesContainer.size()-1; i++){
            String ItemName = (String) ((TextField) StatesContainer.getItem(i+1).getItemProperty(2).getValue()).getValue();
            if (ItemName.equals(NewItemName)){
                k = k + 1;
            }
        }

        if (k>0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isStatesContainerContainsCode(String NewCode){
        int k = 0;

        for (int i=0; i<StatesContainer.size()-1; i++){
            String ItemCode = (String) ((TextField) StatesContainer.getItem(i+1).getItemProperty(3).getValue()).getValue();
            if (ItemCode.equals(NewCode)){
                k = k + 1;
            }
        }

        if (k>0) {
            return true;
        } else {
            return false;
        }
    }

    public Integer newActuatorStateInsert(
            int qUserDeviceId
            ,String qStateName
            ,String qStateCode
            ,int qTrTime
            ,String qNoteList
    ){
        Integer newStateId = null;

        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement Stmt = Con.prepareCall("{call p_insert_actuator_state(?, ?, ?, ?, ?, ?)}");
            Stmt.setInt(1, qUserDeviceId);
            Stmt.setString(2, qStateName);
            Stmt.setString(3, qStateCode);
            Stmt.setInt(4, qTrTime);
            Stmt.setString(5, qNoteList);
            Stmt.registerOutParameter(6,Types.INTEGER);
            Stmt.execute();

            newStateId = Stmt.getInt(6);

            Con.close();

        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }
        return newStateId;
    }


    public void setListener(addDeleteListener listener){
        this.Listener = listener;
    }

    public void commitActuatorMessageCode(String MessCode
            ,String topicName
            ,String mqttServerHost
    ){
        try {
            MqttClient client = new MqttClient(mqttServerHost, topicName, null);
            MqttMessage message = new MqttMessage(MessCode.getBytes());
            client.connect();
            client.publish(topicName, message);
            client.disconnect();
        } catch(MqttException me) {
            Notification.show("Ошибка подключения:",
                    "Не удается подключиться к серверу " + mqttServerHost,
                    Notification.Type.TRAY_NOTIFICATION);
            //me.printStackTrace();
        }
    }

}

