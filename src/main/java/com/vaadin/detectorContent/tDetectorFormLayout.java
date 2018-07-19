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
 * Created by kalistrat on 13.05.2017.
 */
public class tDetectorFormLayout extends VerticalLayout {

    Button SaveButton;
    Button EditButton;
    public TextField NameTextField;
    //TextField UnitsTextField;
    public NativeSelect PeriodMeasureSelect;
    public TextField DetectorAddDate;
    public TextField InTopicNameField;
    public NativeSelect ArrivedDataTypeSelect;

    int iUserDeviceId;

    public tDetectorFormLayout(int eUserDeviceId,String eUserLogIn){

        iUserDeviceId = eUserDeviceId;

        SaveButton = new Button();
        SaveButton.setIcon(FontAwesome.SAVE);
        SaveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        SaveButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        SaveButton.setEnabled(false);

        SaveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                String sErrorMessage = "";

                if (!sErrorMessage.equals("")){
                    Notification.show("Ошибка сохранения:",
                            sErrorMessage,
                            Notification.Type.TRAY_NOTIFICATION);
                } else {

                    updateDetectorFormData();
                    SaveButton.setEnabled(false);
                    EditButton.setEnabled(true);
                    PeriodMeasureSelect.setEnabled(false);
                    ArrivedDataTypeSelect.setEnabled(false);
                }

            }
        });

        EditButton = new Button();
        EditButton.setIcon(VaadinIcons.EDIT);
        EditButton.addStyleName(ValoTheme.BUTTON_SMALL);
        EditButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);

        EditButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                SaveButton.setEnabled(true);
                EditButton.setEnabled(false);
                PeriodMeasureSelect.setEnabled(true);
                ArrivedDataTypeSelect.setEnabled(true);
            }
        });

        NameTextField = new TextField("Наименование устройства :");
        NameTextField.setEnabled(false);
        //UnitsTextField = new TextField("Единицы измерения :");
        //UnitsTextField.setEnabled(false);
        PeriodMeasureSelect = new NativeSelect("Период измерений :");
        PeriodMeasureSelect.setEnabled(false);
        PeriodMeasureSelect.addItem("ежесекундно");
        PeriodMeasureSelect.addItem("ежеминутно");
        PeriodMeasureSelect.addItem("ежечасно");
        PeriodMeasureSelect.addItem("ежедневно");
        PeriodMeasureSelect.addItem("еженедельно");
        PeriodMeasureSelect.addItem("ежемесячно");
        PeriodMeasureSelect.addItem("ежегодно");
        PeriodMeasureSelect.addItem("не задано");
        PeriodMeasureSelect.setNullSelectionAllowed(false);
        PeriodMeasureSelect.select("не задано");

        ArrivedDataTypeSelect = new NativeSelect("Тип измеряемых данных :");
        ArrivedDataTypeSelect.setEnabled(false);
        ArrivedDataTypeSelect.addItem("текст");
        ArrivedDataTypeSelect.addItem("число");
        ArrivedDataTypeSelect.addItem("дата");
        ArrivedDataTypeSelect.setNullSelectionAllowed(false);
        ArrivedDataTypeSelect.select("текст");


        DetectorAddDate = new TextField("Дата добавления устройства :");
        DetectorAddDate.setEnabled(false);
        InTopicNameField = new TextField("mqtt-топик для записи :");
        InTopicNameField.setEnabled(false);


        FormLayout dform = new FormLayout(
                NameTextField
                ,PeriodMeasureSelect
                ,DetectorAddDate
                ,InTopicNameField
                ,ArrivedDataTypeSelect
        );
        dform.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        dform.addStyleName("FormFont");

        dform.setMargin(false);


        VerticalLayout dForm = new VerticalLayout(
                dform
        );
        dForm.addStyleName(ValoTheme.LAYOUT_CARD);
        dForm.setWidth("100%");
        dForm.setHeightUndefined();



        Label DetectorFormHeader = new Label();
        DetectorFormHeader.setContentMode(ContentMode.HTML);
        DetectorFormHeader.setValue(VaadinIcons.FORM.getHtml() + "  " + "Параметры измерительного устройства");
        DetectorFormHeader.addStyleName(ValoTheme.LABEL_COLORED);
        DetectorFormHeader.addStyleName(ValoTheme.LABEL_SMALL);

        HorizontalLayout FormHeaderButtons = new HorizontalLayout(
                EditButton
                ,SaveButton
        );
        FormHeaderButtons.setSpacing(true);
        FormHeaderButtons.setSizeUndefined();

        HorizontalLayout FormHeaderLayout = new HorizontalLayout(
                DetectorFormHeader
                ,FormHeaderButtons
        );
        FormHeaderLayout.setWidth("100%");
        FormHeaderLayout.setHeightUndefined();
        FormHeaderLayout.setComponentAlignment(DetectorFormHeader,Alignment.MIDDLE_LEFT);
        FormHeaderLayout.setComponentAlignment(FormHeaderButtons,Alignment.MIDDLE_RIGHT);



        VerticalLayout ContentLayout = new VerticalLayout(
                FormHeaderLayout
                ,dForm
        );
        ContentLayout.setSpacing(true);
        ContentLayout.setWidth("100%");
        ContentLayout.setHeightUndefined();

        this.addComponent(ContentLayout);
        //this.addStyleName(ValoTheme.LAYOUT_WELL);


    }



    public void updateDetectorFormData(){
        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement Stmt = Con.prepareCall("{call p_updateDetectorFormData(?, ? ,?)}");
            Stmt.setInt(1, iUserDeviceId);
            Stmt.setString(2, (String) PeriodMeasureSelect.getValue());
            Stmt.setString(3, (String) ArrivedDataTypeSelect.getValue());

            Stmt.execute();

            Con.close();

        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }

    }


}
