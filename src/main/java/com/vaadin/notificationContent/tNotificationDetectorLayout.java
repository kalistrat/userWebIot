package com.vaadin.notificationContent;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tTreeContentLayout;
import com.vaadin.tUsefulFuctions;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalistrat on 01.11.2017.
 */
public class tNotificationDetectorLayout extends VerticalLayout {
    Button AddButton;
    Button DeleteButton;
    Button SaveButton;

    Table iNotificationTable;
    IndexedContainer iNotificationContainer;
    int iUserDeviceId;
    tTreeContentLayout iParentContentLayout;

    class stateIdMap{
        int itemId;
        int stateId;
        public stateIdMap(int itId,int stId){
            itemId = itId;
            stateId = stId;
        }
    }

    List<stateIdMap> statesList;

    public tNotificationDetectorLayout(
            int eUserDeviceId
            ,tTreeContentLayout eParentContentLayout
    ){

        iUserDeviceId = eUserDeviceId;
        iParentContentLayout = eParentContentLayout;
        statesList = new ArrayList<>();

        Label Header = new Label();
        Header.setContentMode(ContentMode.HTML);
        Header.setValue(VaadinIcons.CALENDAR_ENVELOPE.getHtml() + "  " + "Перечень оповещений");
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

                String conditionName = (String) ((tNotificationConditionSelect) ThatItem.getItemProperty(2).getValue()).getValue();
                String valueFrom = ((tNotificationCriteriaField) ThatItem.getItemProperty(3).getValue()).valueFromField.getValue().replace(",",".");
                String valueTill = ((tNotificationCriteriaField) ThatItem.getItemProperty(3).getValue()).valueTillField.getValue().replace(",",".");
                boolean conditionType = ((tNotificationCriteriaField) ThatItem.getItemProperty(3).getValue()).isInterval;
                String valueTimeInt = ((TextField) ThatItem.getItemProperty(4).getValue()).getValue();
                String valueNotifyList = ((tNotificationListLayout) ThatItem.getItemProperty(5).getValue()).getValuesStr();

                String sErrorMessage = "";

                if (conditionName == null){
                    sErrorMessage = "Условие оповещения не задано\n";
                }

                if (conditionName.equals("")){
                    sErrorMessage = sErrorMessage + "Условие оповещения не задано\n";
                }

                if (!valueFrom.equals("")) {
                    if (tUsefulFuctions.ParseDouble(valueFrom) == null) {
                        sErrorMessage = sErrorMessage + "Значение критерия не является численным\n";
                    }
                } else {
                    sErrorMessage = sErrorMessage + "Значение критерия не задано\n";
                }

                if (conditionType){
                    if (tUsefulFuctions.ParseDouble(valueTill) == null){
                        sErrorMessage = sErrorMessage + "Крайнее значение интервала не число или не задано\n";
                    }
                }

                if (conditionType){
                    if (tUsefulFuctions.ParseDouble(valueTill) != null && tUsefulFuctions.ParseDouble(valueFrom) != null){
                        if (tUsefulFuctions.ParseDouble(valueTill)<tUsefulFuctions.ParseDouble(valueFrom)) {
                            sErrorMessage = sErrorMessage + "Начальное значение интервала превышает его конечное значение\n";
                        }
                        if (isNotificationExists(conditionName,valueFrom,valueTill,conditionType)){
                            sErrorMessage = sErrorMessage + "Добавляемое оповещение уже существует\n";
                        }
                    }
                } else {
                    if (tUsefulFuctions.ParseDouble(valueFrom) != null){
                        if (isNotificationExists(conditionName,valueFrom,valueTill,conditionType)){
                            sErrorMessage = sErrorMessage + "Добавляемое оповещение уже существует\n";
                        }
                    }
                }


                if (tUsefulFuctions.StrToIntValue(valueTimeInt) == null) {
                    sErrorMessage = sErrorMessage + "Значение длительности выполнения критерия не задано или не является числом\n";
                } else {
                    if (tUsefulFuctions.StrToIntValue(valueTimeInt) < 5) {
                        sErrorMessage = sErrorMessage + "Значение длительности выполнения критерия не может быть менее 5 секунд\n";
                    }
                }

                if (valueNotifyList.equals("")){
                    sErrorMessage = sErrorMessage + "Не отмечено ни одного источника оповещения (MAIL,WHATSUP)\n";
                }

                if (!sErrorMessage.equals("")){
                    Notification.show("Ошибка сохранения:",
                            sErrorMessage,
                            Notification.Type.TRAY_NOTIFICATION);
                } else {

                    ((tNotificationConditionSelect) ThatItem.getItemProperty(2).getValue()).setEnabled(false);
                    ((tNotificationCriteriaField) ThatItem.getItemProperty(3).getValue()).valueFromField.setEnabled(false);
                    ((tNotificationCriteriaField) ThatItem.getItemProperty(3).getValue()).valueTillField.setEnabled(false);
                    ((TextField) ThatItem.getItemProperty(4).getValue()).setEnabled(false);
                    ((tNotificationListLayout) ThatItem.getItemProperty(5).getValue()).setEnabledFalse();

                    int iNewStateId = addNewNotification(
                    Integer.parseInt(valueTimeInt)
                    ,valueFrom
                    ,valueTill
                    ,iUserDeviceId
                    ,conditionName
                    ,valueNotifyList
                    );

                    tUsefulFuctions.sendMessAgeToSubcribeServer(
                            iNewStateId
                            , iParentContentLayout.iUserLog
                            , "add"
                            , "state"
                    );

                    NotificationContainerRefresh();
                    DeleteButton.setEnabled(true);
                    AddButton.setEnabled(true);
                    SaveButton.setEnabled(false);
                    Notification.show("Оповещение добавлено!",
                            null,
                            Notification.Type.TRAY_NOTIFICATION);

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

                int NewItemNum = iNotificationContainer.size()+1;

                Item AddedItem = iNotificationContainer.addItem(NewItemNum);

                AddedItem.getItemProperty(1).setValue(NewItemNum);
                tNotificationCriteriaField creField = new tNotificationCriteriaField();
                tNotificationConditionSelect condSel = new tNotificationConditionSelect(creField);

                condSel.select("Измеряемая величина > критического значения");
                AddedItem.getItemProperty(2).setValue(condSel);

                AddedItem.getItemProperty(3).setValue(creField);
                TextField timeInt = new TextField();
                timeInt.setWidth("50px");
                timeInt.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
                timeInt.setValue("");
                timeInt.setInputPrompt("0");

                AddedItem.getItemProperty(4).setValue(timeInt);
                tNotificationListLayout noteListLay = new tNotificationListLayout();
                AddedItem.getItemProperty(5).setValue(noteListLay);
                iNotificationTable.setPageLength(iNotificationContainer.size());
                SaveButton.setData(AddedItem);
                SaveButton.setEnabled(true);
                DeleteButton.setEnabled(false);

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
                for (int i=0; i<iNotificationContainer.size();i++){
                    if (iNotificationTable.isSelected(i+1)) {
                        SelectedItemId = i+1;
                    }
                }

                if (SelectedItemId>0) {
                    int removeStateId = getDbIdByItemId(SelectedItemId);
                    removeStateFromDb(removeStateId);
                    tUsefulFuctions.sendMessAgeToSubcribeServer(
                            removeStateId
                            , iParentContentLayout.iUserLog
                            , "delete"
                            , "state"
                    );
                    NotificationContainerRefresh();
                    Notification.show("Оповещение удалёно!",
                            null,
                            Notification.Type.TRAY_NOTIFICATION);

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

        iNotificationTable = new Table();
        iNotificationTable.setWidth("100%");

        iNotificationTable.setColumnHeader(1, "№");
        iNotificationTable.setColumnHeader(2, "Условие<br/>оповещения");
        iNotificationTable.setColumnHeader(3, "Критерий<br/>оповещения");
        iNotificationTable.setColumnHeader(4, "Δt, с");
        iNotificationTable.setColumnHeader(5, "Системы<br/>оповещения");

        iNotificationContainer = new IndexedContainer();
        iNotificationContainer.addContainerProperty(1, Integer.class, null);
        iNotificationContainer.addContainerProperty(2, tNotificationConditionSelect.class, null);
        iNotificationContainer.addContainerProperty(3, tNotificationCriteriaField.class, null);
        iNotificationContainer.addContainerProperty(4, TextField.class, null);
        iNotificationContainer.addContainerProperty(5, tNotificationListLayout.class, null);

        setNotificationContainer();

        iNotificationTable.setContainerDataSource(iNotificationContainer);


        iNotificationTable.setPageLength(iNotificationContainer.size());



        iNotificationTable.addStyleName(ValoTheme.TABLE_COMPACT);
        iNotificationTable.addStyleName(ValoTheme.TABLE_SMALL);
        iNotificationTable.addStyleName("TableRow");


        iNotificationTable.setSelectable(true);

        VerticalLayout NotificationTableLayout = new VerticalLayout(
                iNotificationTable
        );
        NotificationTableLayout.setWidth("100%");
        NotificationTableLayout.setHeightUndefined();
        NotificationTableLayout.setComponentAlignment(iNotificationTable,Alignment.MIDDLE_CENTER);

        VerticalLayout ContentLayout = new VerticalLayout(
                HeaderLayout
                ,NotificationTableLayout
        );
        ContentLayout.setSpacing(true);
        ContentLayout.setWidth("100%");
        ContentLayout.setHeightUndefined();

        this.addComponent(ContentLayout);

    }

    public void NotificationContainerRefresh(){
        iNotificationContainer.removeAllItems();
        setNotificationContainer();
        iNotificationTable.setPageLength(iNotificationContainer.size());

    }

    public void setNotificationContainer(){
        try {
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );
            statesList.clear();

            String DataSql = "select @num1:=@num1+1 notification_condition_num\n" +
                    ",uas.actuator_state_name notification_condition_name\n" +
                    ",case when uas.actuator_message_code='INTERVAL' then\n" +
                    "concat(\n" +
                    "(\n" +
                    "select uasc.right_part_expression\n" +
                    "from user_actuator_state_condition uasc\n" +
                    "where uasc.user_actuator_state_id=uas.user_actuator_state_id\n" +
                    "and uasc.condition_num=1\n" +
                    ")\n" +
                    ",'|',(\n" +
                    "select uasc.right_part_expression\n" +
                    "from user_actuator_state_condition uasc\n" +
                    "where uasc.user_actuator_state_id=uas.user_actuator_state_id\n" +
                    "and uasc.condition_num=2\n" +
                    "),'|'\n" +
                    ")\n" +
                    "else (\n" +
                    "select uasc.right_part_expression\n" +
                    "from user_actuator_state_condition uasc\n" +
                    "where uasc.user_actuator_state_id=uas.user_actuator_state_id\n" +
                    "and uasc.condition_num=1\n" +
                    ") end criteria_value\n" +
                    ",uas.transition_time\n" +
                    ",(\n" +
                    "select concat(group_concat(nt.notification_code separator '|'),'|')\n" +
                    "from user_device_state_notification uno\n" +
                    "join notification_type nt on nt.notification_type_id=uno.notification_type_id\n" +
                    "where uno.user_actuator_state_id=uas.user_actuator_state_id\n" +
                    ") notification_codes\n" +
                    ",uas.user_actuator_state_id\n" +
                    "from user_actuator_state uas\n" +
                    "join (select @num1:=0) t\n" +
                    "where uas.user_device_id = ?";

            PreparedStatement DataStmt = Con.prepareStatement(DataSql);
            DataStmt.setInt(1,iUserDeviceId);

            ResultSet DataRs = DataStmt.executeQuery();

            while (DataRs.next()) {

                Item newItem = iNotificationContainer.addItem(DataRs.getInt(1));
                newItem.getItemProperty(1).setValue(DataRs.getInt(1));
                tNotificationCriteriaField creField = new tNotificationCriteriaField();
                creField.setFieldsValues(DataRs.getString(3));
                tNotificationConditionSelect condSel = new tNotificationConditionSelect(creField);
                condSel.select(DataRs.getString(2));
                condSel.setEnabled(false);
                newItem.getItemProperty(2).setValue(condSel);
                creField.valueFromField.setEnabled(false);
                creField.valueTillField.setEnabled(false);
                newItem.getItemProperty(3).setValue(creField);
                TextField timeInt = new TextField();
                timeInt.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
                timeInt.setValue(String.valueOf(DataRs.getInt(4)));
                timeInt.setEnabled(false);
                timeInt.setWidth("50px");
                newItem.getItemProperty(4).setValue(timeInt);
                tNotificationListLayout noteListLay = new tNotificationListLayout();
                noteListLay.setEnabledFalse();
                for (String iNoteType : tUsefulFuctions.GetListFromString(DataRs.getString(5),"|")) {
                    noteListLay.markNotification(iNoteType);
                }
                newItem.getItemProperty(5).setValue(noteListLay);

                statesList.add(new stateIdMap(DataRs.getInt(1),DataRs.getInt(6)));

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

    private int addNewNotification(
        int eTransTime
        ,String eValueFrom
        ,String eValueTill
        ,int eUserDeviceId
        ,String eStateName
        ,String eNotifyListStr
    ){
        int iNewNotify = 0;
        try{
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement procStmt = Con.prepareCall("call p_add_notification(?,?,?,?,?,?,?)");
            procStmt.registerOutParameter(1,Types.INTEGER);
            procStmt.setInt(2,eTransTime);
            procStmt.setString(3,String.valueOf(tUsefulFuctions.ParseDouble(eValueFrom)));
            if (eStateName.equals("Измеряемая величина > критического значения")) {
                procStmt.setString(4, String.valueOf(tUsefulFuctions.ParseDouble(eValueTill)));
            } else {
                procStmt.setString(4, eValueTill);
            }
            procStmt.setInt(5,eUserDeviceId);
            procStmt.setString(6,eStateName);
            procStmt.setString(7,eNotifyListStr);
            procStmt.execute();
            iNewNotify = procStmt.getInt(1);
            Con.close();

        } catch (SQLException|ClassNotFoundException e){
            e.printStackTrace();
        }
        return iNewNotify;
    }

    private boolean isNotificationExists(String addName
    ,String addValFrom
    ,String addValTill
                                         ,boolean addIsInterval
    ){
        boolean isE = false;


        for (int i=0; i<iNotificationContainer.size()-1; i++){
            tNotificationConditionSelect conSel = (tNotificationConditionSelect) iNotificationContainer.getItem(i+1).getItemProperty(2).getValue();
            String selectedName = (String) conSel.getValue();
            tNotificationCriteriaField creFi = (tNotificationCriteriaField) iNotificationContainer.getItem(i+1).getItemProperty(3).getValue();
            double valFrom = tUsefulFuctions.ParseDouble(creFi.valueFromField.getValue());

//            System.out.println("isNotificationExists : selectedName+valFrom : " + selectedName + " " + addValFrom);
//            System.out.println("isNotificationExists : item id : " + i);

            if (creFi.isInterval && addIsInterval){
                double valTill = tUsefulFuctions.ParseDouble(creFi.valueTillField.getValue());

                if (addName.equals(selectedName)
                        && valFrom == tUsefulFuctions.ParseDouble(addValFrom)
                        && valTill == tUsefulFuctions.ParseDouble(addValTill)){
                    isE = true;
//                    System.out.println("isNotificationExists : addValFrom : " + addValFrom);
//                    System.out.println("isNotificationExists : addValTill : " + addValTill);
//                    System.out.println("isNotificationExists : valFrom : " + valFrom);
//                    System.out.println("isNotificationExists : valTill : " + valTill);

                    break;
                }
            } else {
                if (addName.equals(selectedName) && valFrom == tUsefulFuctions.ParseDouble(addValFrom)){
                    isE = true;
//                    System.out.println("isNotificationExists : addValFrom : " + addValFrom);
//                    System.out.println("isNotificationExists : valFrom : " + valFrom);
                    break;
                }
            }
        }

        return  isE;
    }

    private Integer getDbIdByItemId(int checkItemId){
        Integer dbId = null;
        for (stateIdMap iSt : statesList){
            if (iSt.itemId == checkItemId) {
                dbId = iSt.stateId;
            }
        }
        return  dbId;
    }

    private void removeStateFromDb(int remStateId){
        try{
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement procStmt = Con.prepareCall("call p_delete_state_by_id(?)");
            procStmt.setInt(1,remStateId);
            procStmt.execute();
            Con.close();

        } catch (SQLException|ClassNotFoundException e){
            e.printStackTrace();
        }
    }

}
