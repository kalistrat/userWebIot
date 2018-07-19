package com.vaadin.detectorContent;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tUsefulFuctions;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by kalistrat on 13.07.2017.
 */
public class tDetectorMeasuresJournalLayout extends VerticalLayout {

    Table MeasuresTable;
    IndexedContainer MeasuresContainer;
    int iUserDeviceId;
    Button RefreshButton;


    public tDetectorMeasuresJournalLayout(int eUserDeviceId,String actionType) {

        iUserDeviceId = eUserDeviceId;

        String headerTxt;
        String MeasureDateName;

        if (actionType.equals("ACTUATOR")) {
            headerTxt = "Журнал состояний устройства";
            MeasureDateName = "Дата<br/>состояния";
        } else {
            headerTxt = "Журнал показаний устройства";
            MeasureDateName = "Дата<br/>показания";
        }

        Label Header = new Label();
        Header.setContentMode(ContentMode.HTML);
        Header.setValue(VaadinIcons.TABLE.getHtml() + "  " + headerTxt);
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
                MeasuresJournalRefresh();
            }
        });

        HorizontalLayout HeaderButtons = new HorizontalLayout(
                RefreshButton
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
        HeaderLayout.setComponentAlignment(HeaderButtons, Alignment.MIDDLE_RIGHT);

        MeasuresTable = new Table();
        MeasuresTable.setWidth("100%");

        MeasuresTable.setColumnHeader(1, "№");
        MeasuresTable.setColumnHeader(2, MeasureDateName);
        MeasuresTable.setColumnHeader(3, "Текстовое<br/>значение");
        MeasuresTable.setColumnHeader(4, "Числовое<br/>значение");
        MeasuresTable.setColumnHeader(5, "Временное<br/>значение");

        MeasuresContainer = new IndexedContainer();
        MeasuresContainer.addContainerProperty(1, Integer.class, null);
        MeasuresContainer.addContainerProperty(2, String.class, null);
        MeasuresContainer.addContainerProperty(3, String.class, null);
        MeasuresContainer.addContainerProperty(4, String.class, null);
        MeasuresContainer.addContainerProperty(5, String.class, null);
        setMeasuresContainer();
        MeasuresTable.setContainerDataSource(MeasuresContainer);

        if (MeasuresContainer.size()<5) {
            MeasuresTable.setPageLength(MeasuresContainer.size());
        } else {
            MeasuresTable.setPageLength(5);
        }

        MeasuresTable.addStyleName(ValoTheme.TABLE_COMPACT);
        MeasuresTable.addStyleName(ValoTheme.TABLE_SMALL);
        MeasuresTable.addStyleName("TableRow");

        VerticalLayout StatesTableLayout = new VerticalLayout(
                MeasuresTable
        );
        StatesTableLayout.setWidth("100%");
        StatesTableLayout.setHeightUndefined();
        StatesTableLayout.setComponentAlignment(MeasuresTable,Alignment.MIDDLE_CENTER);

        VerticalLayout ContentLayout = new VerticalLayout(
                HeaderLayout
                ,StatesTableLayout
        );
        ContentLayout.setSpacing(true);
        ContentLayout.setWidth("100%");
        ContentLayout.setHeightUndefined();

        this.addComponent(ContentLayout);

    }

    public void setMeasuresContainer(){

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        try {
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            String DataSql = "select @num1:=@num1+1 num\n" +
                    ",udm.measure_date\n" +
                    ",udm.measure_mess\n" +
                    ",udm.measure_value\n" +
                    ",udm.measure_date_value\n" +
                    "from user_device_measures udm\n" +
                    "join (select @num1:=0) t1\n" +
                    "where udm.user_device_id = ?\n" +
                    "order by udm.measure_date desc";

            PreparedStatement DataStmt = Con.prepareStatement(DataSql);
            DataStmt.setInt(1,iUserDeviceId);

            ResultSet DataRs = DataStmt.executeQuery();

            while (DataRs.next()) {

                Item newItem = MeasuresContainer.addItem(DataRs.getInt(1));
                newItem.getItemProperty(1).setValue(DataRs.getInt(1));
                newItem.getItemProperty(2).setValue(df.format(new Date(DataRs.getTimestamp(2).getTime())));
                newItem.getItemProperty(3).setValue(DataRs.getString(3));
                newItem.getItemProperty(4).setValue(DataRs.getString(4));
                if (DataRs.getTimestamp(5) != null) {
                    newItem.getItemProperty(5).setValue(df.format(new Date(DataRs.getTimestamp(5).getTime())));
                } else {
                    newItem.getItemProperty(5).setValue("");
                }
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

    public void MeasuresJournalRefresh(){
        MeasuresContainer.removeAllItems();
        setMeasuresContainer();
        if (MeasuresContainer.size()<5) {
            MeasuresTable.setPageLength(MeasuresContainer.size());
        } else {
            MeasuresTable.setPageLength(5);
        }
    }
}
