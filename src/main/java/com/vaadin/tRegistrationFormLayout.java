package com.vaadin;

import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.io.InputStream;
import java.util.Date;

/**
 * Created by kalistrat on 24.05.2017.
 */
public class tRegistrationFormLayout extends VerticalLayout {

    TextField LoginField;
    //TextField NameTextField;
    PasswordField PassWordField;
    PasswordField ConfirmPassWordField;
    TextField PhoneTextField;
    TextField MailTextField;
    TextField PostCodeField;
    Button SendMailButton;
    Button ClearFormButton;
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

    tCaptchaLayout captchaLayout;
    //tCaptchaImage sendedCaptchaImage;


    public tRegistrationFormLayout() {



        Label Header = new Label();
        Header.setContentMode(ContentMode.HTML);
        Header.setValue(VaadinIcons.USER_CARD.getHtml() + "  " + "Регистрационные данные пользователя");
        Header.addStyleName(ValoTheme.LABEL_COLORED);
        Header.addStyleName(ValoTheme.LABEL_SMALL);

        SendMailButton = new Button("Отправить заявку на доступ");
        SendMailButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        SendMailButton.addStyleName(ValoTheme.BUTTON_SMALL);
        SendMailButton.setIcon(VaadinIcons.PAPERPLANE);

        SendMailButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                String sErrorMessage = "";

                String sLog = LoginField.getValue();

                String sPswd = PassWordField.getValue();
                String scPswd = ConfirmPassWordField.getValue();
                String sPhone = PhoneTextField.getValue();
                String sMail = MailTextField.getValue();
                String sPost = PostCodeField.getValue();
                String sSubjType = (String) SubjectTypeSelect.getValue();


                String sSubjName = "";
                String sSubjAddr = "";
                String sSubjInn = "";
                String sSubjKpp = "";
                String sFirName = "";
                String sSecName = "";
                String sMidName = "";
                Date dBirthdate = null;


                //For juridical persons
                if (!sSubjType.equals("физическое лицо")) {
                    //For juridical persons
                    sSubjName = SubjectNameTextField.getValue();
                    sSubjAddr = SubjectAddressTextField.getValue();
                    sSubjInn = SubjectInnTextField.getValue();
                    sSubjKpp = SubjectKppField.getValue();
                } else {
                    //For physical persons
                    sFirName = FirstNameTextField.getValue();
                    sSecName = SecondNameTextField.getValue();
                    sMidName = MiddleNameTextField.getValue();
                    dBirthdate = BirthDateField.getValue();
                }

                if (sLog.equals("")){
                    sErrorMessage = "Логин не задан\n";
                } else {

                    if (sLog.length() > 50) {
                        sErrorMessage = sErrorMessage + "Длина логина превышает 50 символов\n";
                    }

                    if (sLog.length() < 8) {
                        sErrorMessage = sErrorMessage + "Длина логина менее 8 символов\n";
                        System.out.println("Здесь должна быть проверка логина на длину");
                    }

                    if (!tUsefulFuctions.IsLatinAndDigits(sLog)) {
                        sErrorMessage = sErrorMessage + "Логин должен состоять из латиницы и цифр\n";
                    }

                    if (tUsefulFuctions.isExistsUserLogin(sLog).intValue() == 1) {
                        sErrorMessage = sErrorMessage + "Указанный логин уже используется\n";
                    }

                }

                if (sPswd.equals("")){
                    sErrorMessage = sErrorMessage + "Пароль не задан\n";
                } else {

                        if (sPswd.length() > 150) {
                            sErrorMessage = sErrorMessage + "Длина пароля превышает 150 символов\n";
                        }

                        if (sPswd.length() < 8) {
                            sErrorMessage = sErrorMessage + "Длина пароля менее 8 символов\n";
                        }

                        if (!tUsefulFuctions.IsLatinAndDigits(sPswd)) {
                            sErrorMessage = sErrorMessage + "Пароль должен состоять из латиницы и цифр\n";
                        }

                        if (!sPswd.equals(scPswd)) {
                            sErrorMessage = sErrorMessage + "Пароль и его подтверждение не совпадают\n";
                        }

                }

                if (sPhone.equals("")) {
                    sErrorMessage = sErrorMessage + "Номер телефона не задан\n";
                } else {

                    if (sPhone.length() != 11) {
                        sErrorMessage = sErrorMessage + "Длина номера телефона должны быть 11 символов\n";
                    }

                    if (!tUsefulFuctions.IsDigits(sPhone)) {
                        sErrorMessage = sErrorMessage + "Номер телефона должен состоять из цифр\n";
                    }
                }


                if (sMail.equals("")) {
                    sErrorMessage = sErrorMessage + "Адрес электронной почты не задан\n";
                } else {
                    if (!tUsefulFuctions.IsEmailName(sMail)) {
                        sErrorMessage = sErrorMessage + "Адрес электронной почты не соответствует указанному формату\n";
                    }

                    if (sMail.length() > 150) {
                        sErrorMessage = sErrorMessage + "Длина адреса электронной почты превышает 150 символов\n";
                    }

                    if (tUsefulFuctions.isExistsUserMail(sMail).intValue() == 1) {
                        sErrorMessage = sErrorMessage + "Указанная электронная почта уже используется\n";
                    }
                }

                if (sPost.equals("")) {
                    sErrorMessage = sErrorMessage + "Почтовый индекс не задан\n";
                } else {
                    if (sPost.length() != 6) {
                        sErrorMessage = sErrorMessage + "Длина почтового индекса должна быть 6 символов\n";
                    }
                    if (!tUsefulFuctions.IsDigits(sPost)){
                        sErrorMessage = sErrorMessage + "Почтовый индекс должен состоять из цифр\n";
                    }
                }

                if (!sSubjType.equals("физическое лицо")) {
                    //For juridical persons

                    if (sSubjName.equals("")){
                        sErrorMessage = sErrorMessage + "Наименование организации не задано\n";
                    } else {

                        if (sSubjName.length() > 150) {
                            sErrorMessage = sErrorMessage + "Длина наименования организации превышает 150 символов\n";
                        }
                    }


                    if (sSubjAddr.equals("")){
                        sErrorMessage = sErrorMessage + "Адрес организации не задан\n";
                    } else {

                        if (sSubjAddr.length() > 150) {
                            sErrorMessage = sErrorMessage + "Адрес организации превышает 150 символов\n";
                        }
                    }

                    if (sSubjInn.equals("")){
                        sErrorMessage = sErrorMessage + "ИНН организации не задан\n";
                    } else {

                        if (sSubjInn.length() != 10) {
                            sErrorMessage = sErrorMessage + "Длина ИНН не соответствует 10 символам\n";
                        }

                        if (!tUsefulFuctions.IsDigits(sSubjInn)) {
                            sErrorMessage = sErrorMessage + "ИНН должен содержать только цифры\n";
                        }
                    }

                    if (sSubjKpp.equals("")){
                        sErrorMessage = sErrorMessage + "КПП организации не задан\n";
                    } else {

                        if (sSubjKpp.length() != 9) {
                            sErrorMessage = sErrorMessage + "Длина КПП не соответствует 9 символам\n";
                        }

                        if (!tUsefulFuctions.IsDigits(sSubjKpp)) {
                            sErrorMessage = sErrorMessage + "КПП должен содержать только цифры\n";
                        }
                    }

                } else {
                    //For physical persons

                    if (sFirName.equals("")){
                        sErrorMessage = sErrorMessage + "Имя физического лица не задано\n";
                    } else {

                        if (sFirName.length() > 50) {
                            sErrorMessage = sErrorMessage + "Длина имени физического лица превышает 50 символов\n";
                        }
                    }

                    if (sSecName.equals("")){
                        sErrorMessage = sErrorMessage + "Фамилия физического лица не задана\n";
                    } else {

                        if (sSecName.length() > 50) {
                            sErrorMessage = sErrorMessage + "Длина фамилии физического лица превышает 50 символов\n";
                        }
                    }

                    if (sMidName.equals("")){
                        sErrorMessage = sErrorMessage + "Отчество физического лица не задано\n";
                    } else {

                        if (sMidName.length() > 50) {
                            sErrorMessage = sErrorMessage + "Длина отчества физического лица превышает 50 символов\n";
                        }
                    }

                    if (dBirthdate == null){
                        sErrorMessage = sErrorMessage + "Дата рождения физического лица не задана\n";
                    } else {

                        if (tUsefulFuctions.calculateAge(new java.sql.Date(dBirthdate.getTime())) < 18) {
                            sErrorMessage = sErrorMessage + "Число полных лет не превышает 18\n";
                        }
                    }


                }

                Integer InptValue = tUsefulFuctions.StrToIntValue(captchaLayout.ResultTextField.getValue());

                if (InptValue == null) {
                    sErrorMessage = sErrorMessage + "Введён неверный результат проверочного выражения\n";
                } else {
                    if (InptValue.intValue()!=captchaLayout.captchaRes) {
                        sErrorMessage = sErrorMessage + "Введён неверный результат проверочного выражения\n";
                    }
                }

                if (!sErrorMessage.equals("")){
                    Notification.show("Ошибка сохранения:",
                            sErrorMessage,
                            Notification.Type.TRAY_NOTIFICATION);
                } else {

                    tUserAttributes userAttributes = new tUserAttributes(
                            sLog
                            ,sPswd
                            ,sPhone
                            ,sMail
                            ,sPost
                            ,sSubjType
                            ,sSubjName
                            ,sSubjAddr
                            ,sSubjInn
                            ,sSubjKpp
                            ,sFirName
                            ,sSecName
                            ,sMidName
                            ,dBirthdate
                    );

                    sendEmail(sMail,userAttributes);
                    //System.out.println("sendedCaptchaImage : " + sendedCaptchaImage.captchaRes);

                }
            }
        });

        ClearFormButton = new Button("Очистить форму");
        ClearFormButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        ClearFormButton.addStyleName(ValoTheme.BUTTON_SMALL);
        ClearFormButton.setIcon(VaadinIcons.ERASER);

        ClearFormButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                LoginField.setValue("");
                PassWordField.setValue("");
                ConfirmPassWordField.setValue("");
                PhoneTextField.setValue("");
                MailTextField.setValue("");
                PostCodeField.setValue("");
                captchaLayout.ResultTextField.setValue("");

                if (SubjectTypeSelect.getValue().equals("юридическое лицо")) {
                    SubjectNameTextField.setValue("");
                    SubjectAddressTextField.setValue("");
                    SubjectInnTextField.setValue("");
                    SubjectKppField.setValue("");
                } else {
                    FirstNameTextField.setValue("");
                    SecondNameTextField.setValue("");
                    MiddleNameTextField.setValue("");
                    BirthDateField.setValue(null);
                }

            }
        });

        HorizontalLayout FormHeaderButtons = new HorizontalLayout(
                SendMailButton
                ,ClearFormButton
        );
        FormHeaderButtons.setSpacing(true);
        FormHeaderButtons.setSizeUndefined();

        SubjectTypeSelect = new NativeSelect("Тип субъекта :");
        SubjectTypeSelect.addItem("физическое лицо");
        SubjectTypeSelect.addItem("юридическое лицо");
        SubjectTypeSelect.setNullSelectionAllowed(false);
        SubjectTypeSelect.select("физическое лицо");

        SubjectTypeSelect.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                String SelectedValue = (String) valueChangeEvent.getProperty().getValue();
                //System.out.println("SubjectTypeSelect SelectedValue : " + SelectedValue);

                if (SelectedValue.equals("юридическое лицо")) {

                    PersonalForm.removeComponent(FirstNameTextField);
                    PersonalForm.removeComponent(SecondNameTextField);
                    PersonalForm.removeComponent(MiddleNameTextField);
                    PersonalForm.removeComponent(BirthDateField);

                    SubjectNameTextField = new TextField("Наименование организации :");
                    SubjectNameTextField.setNullRepresentation("");
                    SubjectNameTextField.setInputPrompt("От 5 до 150 символов (ООО Контакт)");

                    SubjectAddressTextField = new TextField("Адрес организации :");
                    SubjectAddressTextField.setNullRepresentation("");
                    SubjectAddressTextField.setInputPrompt("От 5 до 150 символов (г. Москва ул. Косыгина д.19)");

                    SubjectInnTextField = new TextField("Инн организации :");
                    SubjectInnTextField.setNullRepresentation("");
                    SubjectInnTextField.setInputPrompt("Строго 10 цифр (7714698320)");

                    SubjectKppField = new TextField("Кпп организации :");
                    SubjectKppField.setNullRepresentation("");
                    SubjectKppField.setInputPrompt("Строго 9 цифр (773301001)");

                    PersonalForm.addComponent(SubjectNameTextField);
                    PersonalForm.addComponent(SubjectAddressTextField);
                    PersonalForm.addComponent(SubjectInnTextField);
                    PersonalForm.addComponent(SubjectKppField);


                } else {

                    PersonalForm.removeComponent(SubjectNameTextField);
                    PersonalForm.removeComponent(SubjectAddressTextField);
                    PersonalForm.removeComponent(SubjectInnTextField);
                    PersonalForm.removeComponent(SubjectKppField);

                    PersonalForm.removeComponent(FirstNameTextField);
                    PersonalForm.removeComponent(SecondNameTextField);
                    PersonalForm.removeComponent(MiddleNameTextField);
                    PersonalForm.removeComponent(BirthDateField);

                    PersonalForm.addComponent(FirstNameTextField);
                    PersonalForm.addComponent(SecondNameTextField);
                    PersonalForm.addComponent(MiddleNameTextField);
                    PersonalForm.addComponent(BirthDateField);


                }
            }
        });

        HorizontalLayout FormHeaderLayout = new HorizontalLayout(
                Header
                ,FormHeaderButtons
        );
        FormHeaderLayout.setWidth("100%");
        FormHeaderLayout.setHeightUndefined();
        FormHeaderLayout.setComponentAlignment(Header, Alignment.MIDDLE_LEFT);
        FormHeaderLayout.setComponentAlignment(FormHeaderButtons, Alignment.MIDDLE_RIGHT);


        LoginField = new TextField("Логин :");
        LoginField.setIcon(VaadinIcons.USER);
        LoginField.setNullRepresentation("");
        LoginField.setInputPrompt("Мнемоническое имя, содержащее латиницу и цифры от 7 до 50 символов (GlushkovVM1923)");


        //NameTextField = new TextField("Имя пользователя:");
        //NameTextField.setIcon(VaadinIcons.CLIPBOARD_USER);
        //NameTextField.setNullRepresentation("");
        //NameTextField.setInputPrompt("ФИО от 5 до 150 символов (Глушков Виктор Михайлович)");

        PassWordField = new PasswordField("Пароль :");
        PassWordField.setIcon(VaadinIcons.KEY);
        //PassWordField.setValue(null);
        ConfirmPassWordField = new PasswordField("Подтверждение пароля :");
        ConfirmPassWordField.setIcon(VaadinIcons.KEY_O);
        //ConfirmPassWordField.setValue(null);

        PhoneTextField = new TextField("Номер телефона :");
        PhoneTextField.setIcon(VaadinIcons.PHONE);
        PhoneTextField.setNullRepresentation("");
        PhoneTextField.setInputPrompt("Номер телефона 11 символов (79160000000)");
        //PhoneTextField.setValue(null);

        MailTextField = new TextField("Адрес электронной почты :");
        MailTextField.setIcon(VaadinIcons.ENVELOPE);
        MailTextField.setNullRepresentation("");
        MailTextField.setInputPrompt("Имя почтового ящика с доменом до 150 символов (GlushkovVM@ussras.ru)");
        //MailTextField.setValue(null);

        PostCodeField = new TextField("Почтовый индекс :");
        //PostCodeField.setIcon(VaadinIcons.ENVELOPE);
        PostCodeField.setNullRepresentation("");
        PostCodeField.setInputPrompt("6 цифр (119334)");
        //PostCodeField.setValue(null);

//        TextField FirstNameTextField;
//        TextField SecondNameTextField;
//        TextField MiddleNameTextField;
//        DateField BirthDateField;

        FirstNameTextField = new TextField("Имя :");
        FirstNameTextField.setNullRepresentation("");
        FirstNameTextField.setInputPrompt("Виктор");

        SecondNameTextField = new TextField("Фамилия :");
        SecondNameTextField.setNullRepresentation("");
        SecondNameTextField.setInputPrompt("Глушков");

        MiddleNameTextField = new TextField("Отчество :");
        MiddleNameTextField.setNullRepresentation("");
        MiddleNameTextField.setInputPrompt("Михайлович");

        BirthDateField = new DateField("Дата рождения: "){
            @Override
            protected Date handleUnparsableDateString(String dateString)
                    throws Converter.ConversionException {
                throw new Converter.ConversionException("Формат даты неверен. Используйте dd.MM.yyyy");
            }
        };
        BirthDateField.setResolution(Resolution.DAY);
        BirthDateField.setImmediate(true);
        BirthDateField.setDateFormat("dd.MM.yyyy");


        PersonalForm = new FormLayout(
                LoginField
                , PassWordField
                , ConfirmPassWordField
                , PhoneTextField
                , MailTextField
                , PostCodeField
                , SubjectTypeSelect
                , FirstNameTextField
                , SecondNameTextField
                , MiddleNameTextField
                , BirthDateField

        );

        PersonalForm.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        PersonalForm.addStyleName("FormFont");
        PersonalForm.setMargin(false);
        PersonalForm.setWidth("100%");
        PersonalForm.setHeightUndefined();

//        Image cImage = new Image(null,new StreamResource(new tCaptchaImage(), String.valueOf(tUsefulFuctions.genRandInt(1,1000)) + ".png"));
//
//        VerticalLayout CaptImageLayout = new VerticalLayout(
//                cImage
//        );
        captchaLayout = new tCaptchaLayout();


        VerticalLayout FormLayout = new VerticalLayout(
                PersonalForm
                ,captchaLayout
        );
        FormLayout.addStyleName(ValoTheme.LAYOUT_CARD);
        FormLayout.setWidth("900px");
        FormLayout.setHeightUndefined();
        FormLayout.setComponentAlignment(PersonalForm,Alignment.MIDDLE_CENTER);
        FormLayout.setComponentAlignment(captchaLayout,Alignment.MIDDLE_CENTER);
        FormLayout.setSpacing(true);
        FormLayout.setMargin(new MarginInfo(false,false,true,false));
        VerticalLayout ContentLayout = new VerticalLayout(
                FormHeaderLayout
                , FormLayout
        );
        ContentLayout.setSpacing(true);
        //ContentLayout.setWidth("100%");
        ContentLayout.setSizeUndefined();

        this.addComponent(ContentLayout);
    }

    private void sendEmail(String to,tUserAttributes userAttr) {
        try {

            tCaptchaImage sendedCaptchaImage = new tCaptchaImage();

            InputStream inputStream = sendedCaptchaImage.getStream();
            String from = "snslog@mail.ru";
            String subject = "Регистрация на snslog.ru";
            String text = "Введите результат арифметического выражения в окне активации на snslog.ru";
            String fileName = LoginField.getValue()+".png";
            String mimeType = "image/png";

            SpringEmailService.send(from, to, subject, text, inputStream, fileName, mimeType);

            inputStream.close();

            Notification.show("Письмо для подтверждения отправлено",
                    null,
                    Notification.Type.TRAY_NOTIFICATION);

            UI.getCurrent().addWindow(new tAddUserAccountWindow(
                    userAttr
                    ,sendedCaptchaImage.captchaRes)
            );

        } catch (Exception e) {
            //e.printStackTrace();
            Notification.show("Ошибка отправки письма, проверьте адрес электронной почты"
                    , Notification.Type.ERROR_MESSAGE
            );
            System.out.println("Ошибка отправки письма на :" + to);
        }
    }

}
