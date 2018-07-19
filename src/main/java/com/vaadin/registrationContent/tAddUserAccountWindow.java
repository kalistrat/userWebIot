package com.vaadin.registrationContent;


import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.tUsefulFuctions;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.*;


/**
 * Created by kalistrat on 13.07.2017.
 */
public class tAddUserAccountWindow extends Window {

    Button SaveButton;
    Button CancelButton;
    TextField ActivationPostCode;

    public tAddUserAccountWindow(tUserAttributes qUserAttrs, int qCaptchaResult){


        this.setIcon(FontAwesome.USER_PLUS);
        this.setCaption(" Добавление нового пользователя");

        SaveButton = new Button("Подтвердить");

        SaveButton.setData(this);
        SaveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        SaveButton.setIcon(VaadinIcons.CHECK);

        SaveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                String sErrorMessage = "";
                String sInputValue = ActivationPostCode.getValue();
                Integer parseResult = null;


                if (!sInputValue.equals("")){

                    if (tUsefulFuctions.StrToIntValue(sInputValue)!= null) {

                        parseResult = Integer.parseInt(sInputValue);

                        if (parseResult.intValue() != qCaptchaResult) {
                            sErrorMessage = sErrorMessage + "Введённый код не соответствует высланному выражению\n";
                        }

                    } else {
                        sErrorMessage = sErrorMessage + "Введёно не числовое значение\n";
                    }

                } else {
                        sErrorMessage = sErrorMessage + "Код не задан\n";
                }


                if (!sErrorMessage.equals("")){
                    Notification.show("Ошибка активации профиля:",
                            sErrorMessage,
                            Notification.Type.TRAY_NOTIFICATION);
                } else {

                    addNewUserProfile(qUserAttrs);
                    tUsefulFuctions.sendMessAgeToSubcribeServer(
                            777
                            , qUserAttrs.iLog
                            , "add"
                            , "server"
                    );

                    Notification.show("Новый профиль добавлен!",
                            null,
                            Notification.Type.TRAY_NOTIFICATION);
                    UI.getCurrent().removeWindow((tAddUserAccountWindow) clickEvent.getButton().getData());

                }


            }
        });

        CancelButton = new Button("Отменить");

        CancelButton.setData(this);
        CancelButton.addStyleName(ValoTheme.BUTTON_SMALL);
        CancelButton.setIcon(VaadinIcons.CLOSE);
        CancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                UI.getCurrent().removeWindow((tAddUserAccountWindow) clickEvent.getButton().getData());
            }
        });

        HorizontalLayout ButtonsLayout = new HorizontalLayout(
                SaveButton
                ,CancelButton
        );

        ButtonsLayout.setSizeUndefined();
        ButtonsLayout.setSpacing(true);

        ActivationPostCode = new TextField("Код активации :");
        ActivationPostCode.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        ActivationPostCode.setValue("");
        ActivationPostCode.setNullRepresentation("");
        ActivationPostCode.setInputPrompt("Введите результат арифметического выражения из картинки, высланной письмом");
        ActivationPostCode.setWidth("600px");
//        FormLayout ActParamLayout = new FormLayout(
//                ActivationPostCode
//        );
//        ActParamLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
//        ActParamLayout.setSizeUndefined();
//        ActParamLayout.addStyleName("FormFont");
//        ActParamLayout.setMargin(false);

        VerticalLayout ActParamLayout = new VerticalLayout(
                ActivationPostCode
        );
        //ActParamLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        ActParamLayout.setSizeUndefined();
        ActParamLayout.addStyleName("FormFont");
        ActParamLayout.setMargin(false);

        VerticalLayout MessageLayout = new VerticalLayout(
                ActParamLayout
        );

        MessageLayout.setSpacing(true);
        MessageLayout.setSizeUndefined();
        MessageLayout.setMargin(true);
        MessageLayout.setComponentAlignment(ActParamLayout, Alignment.MIDDLE_CENTER);
        MessageLayout.addStyleName(ValoTheme.LAYOUT_CARD);

        VerticalLayout WindowContentLayout = new VerticalLayout(
                MessageLayout
                ,ButtonsLayout
        );
        WindowContentLayout.setSizeUndefined();
        WindowContentLayout.setSpacing(true);
        WindowContentLayout.setMargin(true);
        WindowContentLayout.setComponentAlignment(ButtonsLayout, Alignment.BOTTOM_CENTER);

        this.setContent(WindowContentLayout);
        this.setSizeUndefined();
        this.setModal(true);
    }

    public void addNewUserProfile(
            tUserAttributes wUserAttrs
    ){
        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement Stmt = Con.prepareCall("{call pNewUserAdd(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");

            Stmt.setString(1, wUserAttrs.iLog);
            Stmt.setString(2, tUsefulFuctions.sha256(wUserAttrs.iPswd));
            Stmt.setString(3, wUserAttrs.iPhone);
            Stmt.setString(4, wUserAttrs.iMail);
            Stmt.setString(5, wUserAttrs.iPost);
            Stmt.setString(6, wUserAttrs.iSubjType);
            Stmt.setString(7, wUserAttrs.iSubjName);
            Stmt.setString(8, wUserAttrs.iSubjAddr);
            Stmt.setString(9, wUserAttrs.iSubjInn);
            Stmt.setString(10, wUserAttrs.iSubjKpp);
            Stmt.setString(11, wUserAttrs.iFirName);
            Stmt.setString(12, wUserAttrs.iSecName);
            Stmt.setString(13, wUserAttrs.iMidName);
            if (wUserAttrs.idBirthdate != null) {
                Stmt.setDate(14, new java.sql.Date(wUserAttrs.idBirthdate.getTime()));
            } else {
                Stmt.setNull(14, Types.DATE);
            }

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
