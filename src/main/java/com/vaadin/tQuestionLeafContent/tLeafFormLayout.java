package com.vaadin.tQuestionLeafContent;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tTreeContentLayout;
import com.vaadin.tUsefulFuctions;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.*;

/**
 * Created by kalistrat on 15.03.2018.
 */
public class tLeafFormLayout extends VerticalLayout {

    Button SaveButton;
    Button EditButton;

    TextField NameTextField;
    TextField UIDTextField;
    TextField TimeTopicNameField;
    TextField MqttServerTextField;
    TextField DeviceLoginTextField;
    TextField DevicePassWordTextField;
    NativeSelect TimeZoneSelect;
    TextField TimeSyncInterval;

    public  tLeafFormLayout(int leafId, tTreeContentLayout treeContentLayout){

        Label Header = new Label();
        Header.setContentMode(ContentMode.HTML);
        Header.setValue(VaadinIcons.FORM.getHtml() + "  " + "Параметры устройства");
        Header.addStyleName(ValoTheme.LABEL_COLORED);
        Header.addStyleName(ValoTheme.LABEL_SMALL);

        SaveButton = new Button();
        SaveButton.setIcon(FontAwesome.SAVE);
        SaveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        SaveButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        SaveButton.setEnabled(false);

        EditButton = new Button();
        EditButton.setIcon(VaadinIcons.EDIT);
        EditButton.addStyleName(ValoTheme.BUTTON_SMALL);
        EditButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);

        HorizontalLayout FormHeaderButtons = new HorizontalLayout(
                EditButton
                ,SaveButton
        );
        FormHeaderButtons.setSpacing(true);
        FormHeaderButtons.setSizeUndefined();

        HorizontalLayout FormHeaderLayout = new HorizontalLayout(
                Header
                , FormHeaderButtons
        );
        FormHeaderLayout.setWidth("100%");
        FormHeaderLayout.setHeightUndefined();
        FormHeaderLayout.setComponentAlignment(Header, Alignment.MIDDLE_LEFT);
        FormHeaderLayout.setComponentAlignment(FormHeaderButtons, Alignment.MIDDLE_RIGHT);

        NameTextField = new TextField("Наименование устройства :");
        NameTextField.setEnabled(false);

        UIDTextField = new TextField("UID устройства :");
        UIDTextField.setEnabled(false);

        TimeTopicNameField = new TextField("mqtt-топик для синхронизации времени :");
        TimeTopicNameField.setEnabled(false);

        DeviceLoginTextField = new TextField("Логин устройства :");
        DevicePassWordTextField = new TextField("Пароль устройства :");
        MqttServerTextField = new TextField("mqtt-сервер :");

        TimeZoneSelect = new NativeSelect("Часовой пояс контроллера :");
        TimeZoneSelect.setNullSelectionAllowed(false);
        tUsefulFuctions.setTimeZoneList(TimeZoneSelect);

        TimeSyncInterval = new TextField("Интервал синхронизации времени (в сутках) :");

        DeviceLoginTextField.setEnabled(false);
        DevicePassWordTextField.setEnabled(false);
        MqttServerTextField.setEnabled(false);
        TimeZoneSelect.setEnabled(false);
        TimeSyncInterval.setEnabled(false);

        setLeafFormData(treeContentLayout.getLeafTreeId(leafId));

        FormLayout ControlerForm = new FormLayout(
                NameTextField
                ,UIDTextField
                , MqttServerTextField
                , DeviceLoginTextField
                , DevicePassWordTextField
                , TimeTopicNameField
                , TimeZoneSelect
                , TimeSyncInterval
        );

        ControlerForm.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        ControlerForm.addStyleName("FormFont");
        ControlerForm.setMargin(false);

        VerticalLayout ControlerFormLayout = new VerticalLayout(
                ControlerForm
        );
        ControlerFormLayout.addStyleName(ValoTheme.LAYOUT_CARD);
        ControlerFormLayout.setWidth("100%");
        ControlerFormLayout.setHeightUndefined();

        VerticalLayout ContentLayout = new VerticalLayout(
                FormHeaderLayout
                , ControlerFormLayout
        );
        ContentLayout.setSpacing(true);
        ContentLayout.setWidth("100%");
        ContentLayout.setHeightUndefined();

        this.addComponent(ContentLayout);
    }

    private void setLeafFormData(int treeId){
        try {
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            String TreeSql = "select udt.leaf_name\n" +
                    ",udt.uid\n" +
                    ",udt.time_topic\n" +
                    ",tz.timezone_value\n" +
                    ",ms.vserver_ip\n" +
                    ",udt.sync_interval\n" +
                    ",udt.control_log\n" +
                    ",udt.control_pass\n" +
                    "from user_devices_tree udt\n" +
                    "join timezones tz on tz.timezone_id=udt.timezone_id\n" +
                    "join mqtt_servers ms on ms.server_id=udt.mqtt_server_id\n" +
                    "where udt.user_devices_tree_id = ?";

            PreparedStatement Stmt = Con.prepareStatement(TreeSql);
            Stmt.setInt(1,treeId);

            ResultSet SqlRs = Stmt.executeQuery();

            while (SqlRs.next()) {

                NameTextField.setValue(SqlRs.getString(1));
                UIDTextField.setValue(SqlRs.getString(2));
                MqttServerTextField.setValue(SqlRs.getString(5));
                DeviceLoginTextField.setValue(SqlRs.getString(7));
                DevicePassWordTextField.setValue(SqlRs.getString(8));
                TimeTopicNameField.setValue(SqlRs.getString(3));
                TimeZoneSelect.select(SqlRs.getString(4));
                TimeSyncInterval.setValue(String.valueOf(SqlRs.getInt(6)));

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
}
