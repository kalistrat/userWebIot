package com.vaadin.actuatorContent;

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
 * Created by kalistrat on 24.11.2017.
 */
public class tActuatorTaskLayout extends VerticalLayout {

    tActuatorStatesLayout statesLayout;
    Table taskTable;
    IndexedContainer taskTableContainer;
    int iUserDeviceId;
    tTreeContentLayout iParentContentLayout;

    Button AddButton;
    Button DeleteButton;
    Button SaveButton;

    class taskIdMap{
        int itemId;
        int taskId;
        public taskIdMap(int itId,int taId){
            itemId = itId;
            taskId = taId;
        }
    }
    List<taskIdMap> tasksList;

    public tActuatorTaskLayout(tActuatorStatesLayout iStatesLayout){
        statesLayout = iStatesLayout;
        iUserDeviceId = iStatesLayout.iUserDeviceId;
        tasksList = new ArrayList<>();
        iParentContentLayout = iStatesLayout.iParentContentLayout;

        Label Header = new Label();
        Header.setContentMode(ContentMode.HTML);
        Header.setValue(VaadinIcons.CALENDAR_CLOCK.getHtml() + "  " + "Назначенные задания");
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

                String conditionName = (String) ((NativeSelect) ThatItem.getItemProperty(2).getValue()).getValue();
                String TimeIntType = (String) ((NativeSelect) ThatItem.getItemProperty(3).getValue()).getValue();
                String valueTimeInt = ((TextField) ThatItem.getItemProperty(4).getValue()).getValue();

                String sErrorMessage = "";

                if (conditionName == null){
                    sErrorMessage = "Состояние не задано\n";
                }

                if (conditionName.equals("")){
                    sErrorMessage = sErrorMessage + "Состояние не задано\n";
                }

                if (TimeIntType == null){
                    sErrorMessage = "Тип временного интервала не задан\n";
                }

                if (TimeIntType.equals("")){
                    sErrorMessage = sErrorMessage + "Тип временного интервала не задан\n";
                }


                if (tUsefulFuctions.StrToIntValue(valueTimeInt) == null) {
                    sErrorMessage = sErrorMessage + "Значение интервала выполнения задания не задано или не является целочисленным\n";
                } else {
                    if (tUsefulFuctions.StrToIntValue(valueTimeInt) < 1) {
                        sErrorMessage = sErrorMessage + "Значение интервала выполенения задания не может быть менее 1 временной единицы\n";
                    }
                }

                if (!sErrorMessage.equals("")){
                    Notification.show("Ошибка сохранения:",
                            sErrorMessage,
                            Notification.Type.TRAY_NOTIFICATION);
                } else {

                    ((NativeSelect) ThatItem.getItemProperty(2).getValue()).setEnabled(false);
                    ((NativeSelect) ThatItem.getItemProperty(3).getValue()).setEnabled(false);
                    ((TextField) ThatItem.getItemProperty(4).getValue()).setEnabled(false);


                    int iNewTaskId = addUserActuatorTask(
                             iUserDeviceId
                            , Integer.parseInt(valueTimeInt)
                            , TimeIntType
                            , conditionName
                    );

                    //System.out.println("iNewTaskId : " + iNewTaskId);

                    tUsefulFuctions.sendMessAgeToSubcribeServer(
                            iNewTaskId
                            , iParentContentLayout.iUserLog
                            , "add"
                            , "task"
                    );

                    taskTableContainerRefresh();
                    DeleteButton.setEnabled(true);
                    AddButton.setEnabled(true);
                    SaveButton.setEnabled(false);
                    Notification.show("Задание добавлено!",
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

                List<String> nList = getConditionNameSelect();

                if (nList.size()>0) {

                    DeleteButton.setEnabled(false);
                    AddButton.setEnabled(false);

                    int NewItemNum = taskTableContainer.size() + 1;

                    Item AddedItem = taskTableContainer.addItem(NewItemNum);

                    AddedItem.getItemProperty(1).setValue(NewItemNum);

                    NativeSelect conditionNameSelect = new NativeSelect();
                    conditionNameSelect.setNullSelectionAllowed(false);
                    for (String is : nList) {
                        conditionNameSelect.addItem(is);
                    }

                    conditionNameSelect.select(nList.get(0));

                    AddedItem.getItemProperty(2).setValue(conditionNameSelect);

                    NativeSelect timIntSelect = new NativeSelect();
                    timIntSelect.addItem("секунда");
                    timIntSelect.addItem("минута");
                    timIntSelect.addItem("час");
                    timIntSelect.addItem("сутки");

                    timIntSelect.select("минута");
                    timIntSelect.setNullSelectionAllowed(false);

                    AddedItem.getItemProperty(3).setValue(timIntSelect);
                    TextField timeInt = new TextField();
                    timeInt.setWidth("50px");
                    timeInt.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
                    timeInt.setValue("");
                    timeInt.setInputPrompt("0");

                    AddedItem.getItemProperty(4).setValue(timeInt);

                    taskTable.setPageLength(taskTableContainer.size());
                    SaveButton.setData(AddedItem);
                    SaveButton.setEnabled(true);
                    DeleteButton.setEnabled(false);

                } else {
                    Notification.show("Добавление невозможно:",
                            "Не задано ни одного возможного состояния устройства",
                            Notification.Type.TRAY_NOTIFICATION);
                }
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
                for (int i=0; i<taskTableContainer.size();i++){
                    if (taskTable.isSelected(i+1)) {
                        SelectedItemId = i+1;
                    }
                }

                if (SelectedItemId>0) {
                    int removeTaskId = getDbIdByItemId(SelectedItemId);
                    removeTaskFromDb(removeTaskId);
                    tUsefulFuctions.sendMessAgeToSubcribeServer(
                            removeTaskId
                            , iParentContentLayout.iUserLog
                            , "delete"
                            , "task"
                    );
                    taskTableContainerRefresh();
                    Notification.show("Задание удалёно!",
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

        taskTable = new Table();
        taskTable.setWidth("100%");

        taskTable.setColumnHeader(1, "№");
        taskTable.setColumnHeader(2, "Наименование<br/>условия");
        taskTable.setColumnHeader(3, "Тип<br/>интервала");
        taskTable.setColumnHeader(4, "Значение<br/>интервала");

        taskTableContainer = new IndexedContainer();
        taskTableContainer.addContainerProperty(1, Integer.class, null);
        taskTableContainer.addContainerProperty(2, NativeSelect.class, null);
        taskTableContainer.addContainerProperty(3, NativeSelect.class, null);
        taskTableContainer.addContainerProperty(4, TextField.class, null);


        setTaskTableContainer();

        taskTable.setContainerDataSource(taskTableContainer);


        taskTable.setPageLength(taskTableContainer.size());



        taskTable.addStyleName(ValoTheme.TABLE_COMPACT);
        taskTable.addStyleName(ValoTheme.TABLE_SMALL);
        taskTable.addStyleName("TableRow");


        taskTable.setSelectable(true);

        VerticalLayout taskTableLayout = new VerticalLayout(
                taskTable
        );
        taskTableLayout.setWidth("100%");
        taskTableLayout.setHeightUndefined();
        taskTableLayout.setComponentAlignment(taskTable,Alignment.MIDDLE_CENTER);

        VerticalLayout ContentLayout = new VerticalLayout(
                HeaderLayout
                ,taskTableLayout
        );
        ContentLayout.setSpacing(true);
        ContentLayout.setWidth("100%");
        ContentLayout.setHeightUndefined();

        this.addComponent(ContentLayout);
    }

    public void setTaskTableContainer(){
        try {
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );
            tasksList.clear();

            String DataSql = "select @num1:=@num1+1 num\n" +
                    ",uas.actuator_state_name\n" +
                    ",udta.interval_type\n" +
                    ",udta.task_interval\n" +
                    ",udta.user_device_task_id\n" +
                    "from user_device_task udta\n" +
                    "left join user_actuator_state uas on uas.user_actuator_state_id=udta.user_actuator_state_id\n" +
                    "join (select @num1:=0) t\n" +
                    "where udta.user_device_id=?";

            PreparedStatement DataStmt = Con.prepareStatement(DataSql);
            DataStmt.setInt(1,iUserDeviceId);

            ResultSet DataRs = DataStmt.executeQuery();

            while (DataRs.next()) {

                Item newItem = taskTableContainer.addItem(DataRs.getInt(1));

                newItem.getItemProperty(1).setValue(DataRs.getInt(1));

                NativeSelect conditionNameSelect = new NativeSelect();
                conditionNameSelect.setNullSelectionAllowed(false);
                conditionNameSelect.addItem(DataRs.getString(2));
                conditionNameSelect.select(DataRs.getString(2));

                newItem.getItemProperty(2).setValue(conditionNameSelect);

                NativeSelect timIntSelect = new NativeSelect();
                timIntSelect.addItem("секунда");
                timIntSelect.addItem("минута");
                timIntSelect.addItem("час");
                timIntSelect.addItem("сутки");
                timIntSelect.setNullSelectionAllowed(false);

                //System.out.println("setTaskTableContainer :DataRs.getString(2) : " + DataRs.getString(2));
                //System.out.println("setTaskTableContainer : iUserDeviceId : " + iUserDeviceId);

                if (DataRs.getString(3).equals("MINUTES")) {
                    timIntSelect.select("минута");
                } else if (DataRs.getString(3).equals("SECONDS")){
                    timIntSelect.select("секунда");
                } else if (DataRs.getString(3).equals("HOURS")){
                    timIntSelect.select("час");
                } else {
                    timIntSelect.select("сутки");
                }

                newItem.getItemProperty(3).setValue(timIntSelect);


                TextField timeInt = new TextField();
                timeInt.setWidth("50px");
                timeInt.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
                timeInt.setValue(String.valueOf(DataRs.getInt(4)));
                timeInt.setInputPrompt("0");

                newItem.getItemProperty(4).setValue(timeInt);

                tasksList.add(new taskIdMap(DataRs.getInt(1),DataRs.getInt(5)));

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


    private Integer getDbIdByItemId(int checkItemId){
        Integer dbId = null;
        for (taskIdMap iSt : tasksList){
            if (iSt.itemId == checkItemId) {
                dbId = iSt.taskId;
            }
        }
        return  dbId;
    }

    private void removeTaskFromDb(int remTaskId){
        try{
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement procStmt = Con.prepareCall("call p_delete_task_by_id(?)");
            procStmt.setInt(1,remTaskId);
            procStmt.execute();
            Con.close();

        } catch (SQLException|ClassNotFoundException e){
            e.printStackTrace();
        }
    }


    public void taskTableContainerRefresh(){
        taskTableContainer.removeAllItems();
        setTaskTableContainer();
        taskTable.setPageLength(taskTableContainer.size());

    }

    private List<String> getConditionNameSelect(){
        List<String> nameList = new ArrayList<>();
        try {
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            String DataSql = "select uas.actuator_state_name\n" +
                    "from user_actuator_state uas\n" +
                    "where uas.user_device_id=?";

            PreparedStatement DataStmt = Con.prepareStatement(DataSql);
            DataStmt.setInt(1,iUserDeviceId);

            ResultSet DataRs = DataStmt.executeQuery();

            while (DataRs.next()) {
                nameList.add(DataRs.getString(1));
            }


            Con.close();

        } catch (SQLException se3) {
            //Handle errors for JDBC
            se3.printStackTrace();
        } catch (Exception e13) {
            //Handle errors for Class.forName
            e13.printStackTrace();
        }
        return nameList;
    }

    private Integer addUserActuatorTask(
            int qUserDeviceId
            , int qTaskInterval
            , String qIntervalType
            ,String qConditionName
    ){
        Integer iTaskId = 0;
        String eIntervalType;

        if (qIntervalType.equals("минута")) {
            eIntervalType = "MINUTES";
        } else if (qIntervalType.equals("секунда")){
            eIntervalType = "SECONDS";
        } else if (qIntervalType.equals("час")){
            eIntervalType = "HOURS";
        } else {
            eIntervalType = "DAYS";
        }

        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement addDeviceTaskStmt = Con.prepareCall("{call p_add_task(?, ?, ?, ?, ?, ?)}");
            addDeviceTaskStmt.setInt(1, qUserDeviceId);
            addDeviceTaskStmt.setString(2, "STATE");
            addDeviceTaskStmt.setInt(3, qTaskInterval);
            addDeviceTaskStmt.setString(4, eIntervalType);
            addDeviceTaskStmt.setString(5, qConditionName);
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
