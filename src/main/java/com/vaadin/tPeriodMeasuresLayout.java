package com.vaadin;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.teemu.VaadinIcons;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.vaadin.tUsefulFuctions.GetMarksFromString;

/**
 * Created by kalistrat on 25.01.2017.
 */
public class tPeriodMeasuresLayout extends VerticalLayout {


    NativeSelect tPeriodCB;
    Integer XD;
    Integer YD;
    List<tMark> MarkXList;
    List<tMark> MarkYList;
    List<tMark> XYList;
    Integer tUserDeviceId;
    tGraphLayout GraphDraw;
    VerticalLayout ContentLayout;

    public tPeriodMeasuresLayout(int eUserDeviceId){


        this.tUserDeviceId = eUserDeviceId;
        tPeriodCB = new NativeSelect();
        XYList = new ArrayList<tMark>();

        setComboBoxData();

        setListXYMarks(eUserDeviceId,"год",10,"x");
        setListXYMarks(eUserDeviceId,"год",5,"y");
        GetGraphData(eUserDeviceId,"год");


        tPeriodCB.setCaption("Выберите период");
        tPeriodCB.setNullSelectionAllowed(false);


        tPeriodCB.setValue("год");

        tPeriodCB.setImmediate(true);

        tPeriodCB.addValueChangeListener(new Property.ValueChangeListener() {
             @Override
             public void valueChange(Property.ValueChangeEvent e) {
//                 Notification.show("Value changed:",
//                         String.valueOf(e.getProperty().getValue()),
//                         Notification.Type.TRAY_NOTIFICATION);

                 String SelectedPeriod = (String) e.getProperty().getValue();
                 MarkXList.removeAll(MarkXList);
                 MarkYList.removeAll(MarkYList);
                 XYList.removeAll(XYList);

                 setListXYMarks(eUserDeviceId,SelectedPeriod,10,"x");
                 setListXYMarks(eUserDeviceId,SelectedPeriod,5,"y");
                 GetGraphData(eUserDeviceId,SelectedPeriod);
                 tGraphLayout GraphDrawNew  = new tGraphLayout(
                         MarkXList
                         ,MarkYList
                         ,XYList
                         ,GetMeasureUnits(tUserDeviceId)
                 );
                 GraphDrawNew.setMargin(false);
                 GraphDrawNew.setSizeUndefined();

                 ContentLayout.replaceComponent(GraphDraw,GraphDrawNew);
                 ContentLayout.setComponentAlignment(GraphDrawNew,Alignment.MIDDLE_CENTER);
                 GraphDraw = GraphDrawNew;

                 if (XYList.size() < 2){
                         Notification.show("За выбранный период данные отсутствуют",
                         Notification.Type.TRAY_NOTIFICATION);
                 }

             }
         }
        );

        GraphDraw = new tGraphLayout(
        this.MarkXList
        ,this.MarkYList
        ,this.XYList
        ,GetMeasureUnits(this.tUserDeviceId)
        );
        GraphDraw.setMargin(false);
        GraphDraw.setSizeUndefined();

//        this.setCaption("Показания за ближайший период");
//        this.setIcon(VaadinIcons.CHART);


        Label GraphHeader = new Label();
        GraphHeader.setContentMode(ContentMode.HTML);
        GraphHeader.setValue(com.vaadin.icons.VaadinIcons.CHART.getHtml() + "  " + "Показания за ближайший период");
        GraphHeader.addStyleName(ValoTheme.LABEL_COLORED);
        GraphHeader.addStyleName(ValoTheme.LABEL_SMALL);

        FormLayout SelectForm = new FormLayout(
                tPeriodCB
        );
        SelectForm.setSizeUndefined();
        SelectForm.setMargin(false);

        HorizontalLayout GraphSelect = new HorizontalLayout(
                SelectForm
        );
        GraphSelect.setSpacing(true);
        GraphSelect.setSizeUndefined();
        GraphSelect.addStyleName("SelectFont");

        HorizontalLayout GraphHeaderLayout = new HorizontalLayout(
                GraphHeader
                ,GraphSelect
        );
        GraphHeaderLayout.setHeightUndefined();
        GraphHeaderLayout.setWidth("100%");
        GraphHeaderLayout.setMargin(false);
        GraphHeaderLayout.setComponentAlignment(GraphHeader,Alignment.MIDDLE_LEFT);
        GraphHeaderLayout.setComponentAlignment(GraphSelect,Alignment.MIDDLE_RIGHT);


        ContentLayout = new VerticalLayout(
                GraphHeaderLayout
                ,GraphDraw
        );
        ContentLayout.setSpacing(true);
        ContentLayout.setMargin(false);
        ContentLayout.setComponentAlignment(GraphDraw,Alignment.MIDDLE_CENTER);
        ContentLayout.setWidth("100%");
        ContentLayout.setHeightUndefined();

        this.addComponent(ContentLayout);
        //this.addStyleName(ValoTheme.LAYOUT_WELL);

    }

    public void reDrawGraphByPeriod(String SelectedPeriod){

        MarkXList.removeAll(MarkXList);
        MarkYList.removeAll(MarkYList);
        XYList.removeAll(XYList);

        setListXYMarks(tUserDeviceId,SelectedPeriod,10,"x");
        setListXYMarks(tUserDeviceId,SelectedPeriod,5,"y");
        GetGraphData(tUserDeviceId,SelectedPeriod);
        tGraphLayout GraphDrawNew  = new tGraphLayout(
                MarkXList
                ,MarkYList
                ,XYList
                ,GetMeasureUnits(tUserDeviceId)
        );
        GraphDrawNew.setMargin(false);
        GraphDrawNew.setSizeUndefined();

        ContentLayout.replaceComponent(GraphDraw,GraphDrawNew);
        ContentLayout.setComponentAlignment(GraphDrawNew,Alignment.MIDDLE_CENTER);
        GraphDraw = GraphDrawNew;

        if (XYList.size() < 2){
            Notification.show("За выбранный период данные отсутствуют",
                    Notification.Type.TRAY_NOTIFICATION);
        }
    }

    public void setComboBoxData(){

        try {
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            String PeriodSql = "select g.period_id\n" +
                    ",g.period_code\n" +
                    "from graph_period g";

            PreparedStatement PeriodSqlStmt = Con.prepareStatement(PeriodSql);

            ResultSet PeriodSqlRs = PeriodSqlStmt.executeQuery();

            while (PeriodSqlRs.next()) {
                Integer iPeriodId = PeriodSqlRs.getInt(1);
                String iPeriodCode = PeriodSqlRs.getString(2);
                this.tPeriodCB.addItem(iPeriodCode);
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

    public void setListXYMarks(int iUserDeviceId, String iPeriodCode, int iCountMarks, String iAxeName){
        String iMarksStringResult = "";
        Integer iMarksInterval = 0;
        String SqlContent = "";

        if (iAxeName.equals("x")){
            SqlContent = "{call p_make_date_marks1(?,?,?,?,?)}";
        } else {
            SqlContent = "{call p_make_double_marks1(?,?,?,?,?)}";
        }

        try {
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection conn = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    ,tUsefulFuctions.USER
                    ,tUsefulFuctions.PASS
            );

            CallableStatement XMarksListStmt = conn.prepareCall(SqlContent);
            XMarksListStmt.setInt(1, iUserDeviceId);
            XMarksListStmt.setString(2, iPeriodCode);
            XMarksListStmt.setInt(3, iCountMarks);
            XMarksListStmt.registerOutParameter (4, Types.VARCHAR);
            XMarksListStmt.registerOutParameter (5, Types.INTEGER);
            XMarksListStmt.execute();
            iMarksStringResult = XMarksListStmt.getString(4);
            iMarksInterval = XMarksListStmt.getInt(5);

            conn.close();
        } catch(SQLException SQLe){
            //Handle errors for JDBC
            SQLe.printStackTrace();
        }catch(Exception e1){
            //Handle errors for Class.forName
            e1.printStackTrace();
        }

        if (iAxeName.equals("x")){
            this.MarkXList = GetMarksFromString(iMarksStringResult,"x");
            this.XD = iMarksInterval;
        } else {
            this.MarkYList = GetMarksFromString(iMarksStringResult,"y");
            this.YD = iMarksInterval;
        }

    }

    public void GetGraphData(int iUserDeviceId, String iPeriodCode){

        try {
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            String GraphSql = "select TIMESTAMPDIFF(second,f_get_min_period_date1(?),udm.measure_date) x\n" +
                    ",round(udm.measure_value) y\n" +
                    "from user_device_measures udm\n" +
                    "where udm.measure_date <= (\n" +
                    "select now()\n" +
                    ")\n" +
                    "and udm.measure_date >= f_get_min_period_date1(?)\n" +
                    "and udm.user_device_id=?\n" +
                    "and udm.measure_value is not null\n" +
                    "order by udm.measure_date";

            PreparedStatement GraphSqlStmt = Con.prepareStatement(GraphSql);

            GraphSqlStmt.setString(1, iPeriodCode);
            GraphSqlStmt.setString(2, iPeriodCode);
            GraphSqlStmt.setInt(3, iUserDeviceId);

            ResultSet GraphSqlRs = GraphSqlStmt.executeQuery();

            while (GraphSqlRs.next()) {
                this.XYList.add(new tMark(GraphSqlRs.getInt(1),GraphSqlRs.getInt(2),""));
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

    public String GetMeasureUnits(int iUserDeviceId){
        String UnitSymbol = "";

        try {
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection conn = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    ,tUsefulFuctions.USER
                    ,tUsefulFuctions.PASS
            );

            CallableStatement UnitSymStmt =  conn.prepareCall("{? = call f_get_unit_sym(?)}");
            UnitSymStmt.registerOutParameter (1, Types.INTEGER);
            UnitSymStmt.setInt(2,iUserDeviceId);
            UnitSymStmt.execute();
            UnitSymbol = UnitSymStmt.getString(1);

            conn.close();
        } catch(SQLException SQLe){
            //Handle errors for JDBC
            SQLe.printStackTrace();
        }catch(Exception e1){
            //Handle errors for Class.forName
            e1.printStackTrace();
        }


        return UnitSymbol;
    }

}
