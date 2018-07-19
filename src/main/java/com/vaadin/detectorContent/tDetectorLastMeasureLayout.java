package com.vaadin.detectorContent;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tUsefulFuctions;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by kalistrat on 19.05.2017.
 */
public class tDetectorLastMeasureLayout extends VerticalLayout {

    Button RefreshButton;
    TextField MeasureValueTextField;
    TextField MeasureDateTextField;
    int iUserDeviceId;

    public tDetectorLastMeasureLayout(int eUserDeviceId,String actionType) {

        iUserDeviceId = eUserDeviceId;
        String headerTxt;
        String lastMeasureValName;
        String lastMeasureDateName;

        if (actionType.equals("ACTUATOR")) {
            headerTxt = "Последнее состояние устройства";
            lastMeasureValName = "Код состояния :";
            lastMeasureDateName = "Дата состояния :";
        } else {
            headerTxt = "Последнее измерение устройства";
            lastMeasureValName = "Величина измерения :";
            lastMeasureDateName = "Дата измерения :";
        }

        Label Header = new Label();
        Header.setContentMode(ContentMode.HTML);
        Header.setValue(VaadinIcons.SPARK_LINE.getHtml() + "  " + headerTxt);
        Header.addStyleName(ValoTheme.LABEL_COLORED);
        Header.addStyleName(ValoTheme.LABEL_SMALL);

        RefreshButton = new Button();
        RefreshButton.setIcon(VaadinIcons.REFRESH);
        RefreshButton.addStyleName(ValoTheme.BUTTON_SMALL);
        RefreshButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        RefreshButton.setEnabled(true);

        RefreshButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                getDetectorLastMeasure();
            }
        });


        HorizontalLayout FormHeaderButtons = new HorizontalLayout(
                RefreshButton
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


        MeasureValueTextField = new TextField(lastMeasureValName);
        MeasureValueTextField.setEnabled(false);

        MeasureDateTextField = new TextField(lastMeasureDateName);
        MeasureDateTextField.setEnabled(false);

        getDetectorLastMeasure();

        FormLayout MeasureForm = new FormLayout(
                MeasureValueTextField
                , MeasureDateTextField
        );

        MeasureForm.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        MeasureForm.addStyleName("FormFont");
        MeasureForm.setMargin(false);

        VerticalLayout MeasureFormLayout = new VerticalLayout(
                MeasureForm
        );
        MeasureFormLayout.addStyleName(ValoTheme.LAYOUT_CARD);
        MeasureFormLayout.setWidth("100%");
        MeasureFormLayout.setHeightUndefined();

        VerticalLayout ContentLayout = new VerticalLayout(
                FormHeaderLayout
                , MeasureFormLayout
        );
        ContentLayout.setSpacing(true);
        ContentLayout.setWidth("100%");
        ContentLayout.setHeightUndefined();

        this.addComponent(ContentLayout);
    }

    public void getDetectorLastMeasure()
    {

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");


        try {
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            String DataSql = "select udme.measure_mess\n" +
                    ",udme.measure_date\n" +
                    "from user_device_measures udme\n" +
                    "where udme.user_device_measure_id in (\n" +
                    "select max(udm.user_device_measure_id)\n" +
                    "from user_device_measures udm\n" +
                    "where udm.user_device_id = ?\n" +
                    ")\n";

            PreparedStatement DataStmt = Con.prepareStatement(DataSql);
            DataStmt.setInt(1,iUserDeviceId);

            ResultSet DataRs = DataStmt.executeQuery();

            while (DataRs.next()) {
                MeasureValueTextField.setValue(String.valueOf(DataRs.getString(1)));
                MeasureDateTextField.setValue(df.format(new Date(DataRs.getTimestamp(2).getTime())));
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
