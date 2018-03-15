package com.vaadin;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kalistrat on 10.11.2017.
 */
public class tUserDataFormLayout extends VerticalLayout {

    TextField LoginField;
    TextField PhoneTextField;
    TextField MailTextField;
    TextField PostCodeField;
    NativeSelect SubjectTypeSelect;

    //For physical persons
    TextField FirstNameTextField;
    TextField SecondNameTextField;
    TextField MiddleNameTextField;
    DateField BirthDateField;

    //For juridical persons
    TextField SubjectNameTextField;
    TextField SubjectAddressTextField;
    TextField SubjectInnTextField;
    TextField SubjectKppField;

    FormLayout PersonalForm;
    String UserLog;


    public tUserDataFormLayout(String userLog){

        UserLog = userLog;

        Label Header = new Label();
        Header.setContentMode(ContentMode.HTML);
        Header.setValue(VaadinIcons.USER_CARD.getHtml() + "  " + "Регистрационные данные пользователя");
        Header.addStyleName(ValoTheme.LABEL_COLORED);
        Header.addStyleName(ValoTheme.LABEL_SMALL);

        SubjectTypeSelect = new NativeSelect("Тип субъекта :");
        SubjectTypeSelect.addItem("физическое лицо");
        SubjectTypeSelect.addItem("юридическое лицо");
        SubjectTypeSelect.setNullSelectionAllowed(false);


//        HorizontalLayout FormHeaderLayout = new HorizontalLayout(
//                Header
//        );
//
//        FormHeaderLayout.setWidth("100%");
//        FormHeaderLayout.setHeightUndefined();
//        FormHeaderLayout.setComponentAlignment(Header, Alignment.MIDDLE_LEFT);

        LoginField = new TextField("Логин :");
        LoginField.setIcon(VaadinIcons.USER);

        PhoneTextField = new TextField("Номер телефона :");
        PhoneTextField.setIcon(VaadinIcons.PHONE);

        MailTextField = new TextField("Адрес электронной почты :");
        MailTextField.setIcon(VaadinIcons.ENVELOPE);

        PostCodeField = new TextField("Почтовый индекс :");

        FirstNameTextField = new TextField("Имя :");

        SecondNameTextField = new TextField("Фамилия :");

        MiddleNameTextField = new TextField("Отчество :");

        BirthDateField = new DateField("Дата рождения: ");
        BirthDateField.setResolution(Resolution.DAY);
        BirthDateField.setImmediate(true);
        BirthDateField.setDateFormat("dd.MM.yyyy");

        LoginField.setEnabled(false);
        PhoneTextField.setEnabled(false);
        MailTextField.setEnabled(false);
        PostCodeField.setEnabled(false);
        SubjectTypeSelect.setEnabled(false);

        //For physical persons
        FirstNameTextField.setEnabled(false);
        SecondNameTextField.setEnabled(false);
        MiddleNameTextField.setEnabled(false);
        BirthDateField.setEnabled(false);

        //For juridical persons
        SubjectNameTextField = new TextField("Наименование организации :");
        SubjectAddressTextField = new TextField("Адрес организации :");
        SubjectInnTextField = new TextField("Инн организации :");
        SubjectKppField = new TextField("Кпп организации :");

        SubjectNameTextField.setEnabled(false);
        SubjectAddressTextField.setEnabled(false);
        SubjectInnTextField.setEnabled(false);
        SubjectKppField.setEnabled(false);

        PersonalForm = new FormLayout();
        setPersonalForm();


        PersonalForm.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        PersonalForm.addStyleName("FormFont");
        PersonalForm.setMargin(false);
        PersonalForm.setWidth("100%");
        PersonalForm.setHeightUndefined();


        VerticalLayout FormLayout = new VerticalLayout(
                PersonalForm
        );
        FormLayout.addStyleName(ValoTheme.LAYOUT_CARD);
        FormLayout.setWidth("900px");
        FormLayout.setHeightUndefined();
        FormLayout.setComponentAlignment(PersonalForm,Alignment.MIDDLE_CENTER);
        FormLayout.setSpacing(true);
        FormLayout.setMargin(new MarginInfo(false,false,true,false));

        VerticalLayout ContentLayout = new VerticalLayout(
                //FormHeaderLayout
                FormLayout
        );
        ContentLayout.setSpacing(true);
        ContentLayout.setSizeUndefined();

        this.addComponent(ContentLayout);
    }

    private void setPersonalForm(){
        //DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        try {
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            String DataSql = "select u.user_log\n" +
                    ",u.user_mail\n" +
                    ",u.user_phone\n" +
                    ",u.first_name\n" +
                    ",u.second_name\n" +
                    ",u.middle_name\n" +
                    ",u.birth_date\n" +
                    ",u.subject_type\n" +
                    ",u.subject_name\n" +
                    ",u.subject_address\n" +
                    ",u.subject_inn\n" +
                    ",u.subject_kpp\n" +
                    ",u.post_index\n" +
                    "from users u\n" +
                    "where u.user_log = ?";

            PreparedStatement DataStmt = Con.prepareStatement(DataSql);
            DataStmt.setString(1,UserLog);

            ResultSet DataRs = DataStmt.executeQuery();

            while (DataRs.next()) {

                String subjectType = DataRs.getString(8);

                if (subjectType == null) {
                    subjectType = "физическое лицо";
                }

                if (subjectType.equals("физическое лицо")) {

                    LoginField.setValue(DataRs.getString(1));
                    MailTextField.setValue(DataRs.getString(2));
                    PhoneTextField.setValue(DataRs.getString(3));
                    SubjectTypeSelect.select(DataRs.getString(8));
                    FirstNameTextField.setValue(DataRs.getString(4));
                    SecondNameTextField.setValue(DataRs.getString(5));
                    MiddleNameTextField.setValue(DataRs.getString(6));
                    BirthDateField.setValue(DataRs.getDate(7));
                    PostCodeField.setValue(DataRs.getString(13));

                    PersonalForm.addComponent(LoginField);
                    PersonalForm.addComponent(MailTextField);
                    PersonalForm.addComponent(PhoneTextField);
                    PersonalForm.addComponent(SubjectTypeSelect);
                    PersonalForm.addComponent(FirstNameTextField);
                    PersonalForm.addComponent(SecondNameTextField);
                    PersonalForm.addComponent(MiddleNameTextField);
                    PersonalForm.addComponent(BirthDateField);
                    PersonalForm.addComponent(PostCodeField);

                } else {

                    LoginField.setValue(DataRs.getString(1));
                    MailTextField.setValue(DataRs.getString(2));
                    PhoneTextField.setValue(DataRs.getString(3));
                    SubjectTypeSelect.select(DataRs.getString(8));
                    SubjectNameTextField.setValue(DataRs.getString(9));
                    SubjectAddressTextField.setValue(DataRs.getString(10));
                    SubjectInnTextField.setValue(DataRs.getString(11));
                    SubjectKppField.setValue(DataRs.getString(12));
                    PostCodeField.setValue(DataRs.getString(13));

                    PersonalForm.addComponent(LoginField);
                    PersonalForm.addComponent(MailTextField);
                    PersonalForm.addComponent(PhoneTextField);
                    PersonalForm.addComponent(SubjectTypeSelect);
                    PersonalForm.addComponent(SubjectNameTextField);
                    PersonalForm.addComponent(SubjectAddressTextField);
                    PersonalForm.addComponent(SubjectInnTextField);
                    PersonalForm.addComponent(SubjectKppField);
                    PersonalForm.addComponent(PostCodeField);

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
}
