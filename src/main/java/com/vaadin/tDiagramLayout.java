package com.vaadin;

import com.google.gson.Gson;
import com.vaadin.data.Item;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import javax.xml.crypto.Data;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalistrat on 13.11.2017.
 */
public class tDiagramLayout extends VerticalLayout {

    Button RefreshButton;
    int iUserDeviceId;
    Diagram diagram;
    List<tDetectorDiagramData> dList;
    String mesDataType;
    VerticalLayout ContentLayout;

    public tDiagramLayout(int userDeviceId, String actType, String messageDataType){

        iUserDeviceId = userDeviceId;
        String actionType = actType;
        String headerTxt;
        mesDataType = messageDataType;

        if (actionType.equals("ACTUATOR")) {
            headerTxt = "Состояния за ближайший период";
        } else {
            headerTxt = "Показания за ближайший период";
        }

        Label Header = new Label();
        Header.setContentMode(ContentMode.HTML);
        Header.setValue(VaadinIcons.CHART.getHtml() + "  " + headerTxt);
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
                ContentLayout.removeComponent(diagram);
                diagram = null;
                diagram = new Diagram();
                dList = new ArrayList<>();
                setDiagramData();
                diagram.setCoords((new Gson()).toJson(dList));
                diagram.addStyleName("diagram");
                ContentLayout.addComponent(diagram);
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


        diagram = new Diagram();
        dList = new ArrayList<>();
        setDiagramData();

        diagram.setCoords((new Gson()).toJson(dList));

        //System.out.println("diagram.getState().getGraphData() : " + diagram.getState().getCoords());

        diagram.addStyleName("diagram");

        ContentLayout = new VerticalLayout(
                FormHeaderLayout
                ,diagram
        );
        ContentLayout.setSpacing(true);
        ContentLayout.setWidth("100%");
        ContentLayout.setHeightUndefined();

        this.addComponent(ContentLayout);
    }

    private void setDiagramData(){
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        try {
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            String DataSql = "\n" +
                    "select a.*\n" +
                    ",b.*\n" +
                    "from (\n" +
                    "select udm.measure_date\n" +
                    ",case when ud.measure_data_type = 'число' then CAST(udm.measure_value AS CHAR(30))\n" +
                    "when ud.measure_data_type='дата' then DATE_FORMAT(udm.measure_date_value, '%d.%m.%Y %H:%i:%s')\n" +
                    "else udm.measure_mess end value\n" +
                    "from user_device_measures udm\n" +
                    "join user_device ud on ud.user_device_id=udm.user_device_id\n" +
                    "where udm.user_device_id=?\n" +
                    ") a\n" +
                    "join (\n" +
                    "select @num:=@num+1 rn\n" +
                    ",t.tvalue\n" +
                    "from (\n" +
                    "select case when ud.measure_data_type = 'число' then CAST(udm.measure_value AS CHAR(30))\n" +
                    "when ud.measure_data_type='дата' then DATE_FORMAT(udm.measure_date_value, '%d.%m.%Y %H:%i:%s')\n" +
                    "else udm.measure_mess end tvalue\n" +
                    "from user_device_measures udm\n" +
                    "join user_device ud on ud.user_device_id=udm.user_device_id\n" +
                    "where udm.user_device_id=?\n" +
                    "group by case when ud.measure_data_type = 'число' then CAST(udm.measure_value AS CHAR(30))\n" +
                    "when ud.measure_data_type='дата' then DATE_FORMAT(udm.measure_date_value, '%d.%m.%Y %H:%i:%s')\n" +
                    "else udm.measure_mess end\n" +
                    ") t\n" +
                    "join (select @num:=0) nt\n" +
                    ") b on a.value=b.tvalue\n" +
                    "order by a.measure_date";

            PreparedStatement DataStmt = Con.prepareStatement(DataSql);
            DataStmt.setInt(1,iUserDeviceId);
            DataStmt.setInt(2,iUserDeviceId);

            ResultSet DataRs = DataStmt.executeQuery();

            if (mesDataType.equals("число")) {
                while (DataRs.next()) {
                    if (tUsefulFuctions.ParseDouble(DataRs.getString(2)) != null) {
                        dList.add(new tDetectorDiagramData(
                                df.format(new Date(DataRs.getTimestamp(1).getTime()))
                                , tUsefulFuctions.ParseDouble(DataRs.getString(2))
                                , ""
                        ));
                    }
                }
                //System.out.println("mesDataType.equals(\"число\") : iUserDeviceId : " + iUserDeviceId);
            } else {
                while (DataRs.next()) {
                        dList.add(new tDetectorDiagramData(
                                df.format(new Date(DataRs.getTimestamp(1).getTime()))
                                , tUsefulFuctions.ParseDouble(DataRs.getString(3))
                                , DataRs.getString(4)
                        ));
                }
                //System.out.println("mesDataType.equals(\"текст\") : iUserDeviceId : " + iUserDeviceId);
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
