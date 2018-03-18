package com.vaadin;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.*;
import com.vaadin.ui.*;

import java.sql.*;

/**
 * Created by SemenovNA on 02.08.2016.
 */
public class tLoginView extends CustomComponent implements View {

    public static final String NAME = "Login";

    //Кнопки
    Button LogOnButton = new Button("Войти");
    Button RemindPassButton = new Button("Напомнить пароль");

    //Метка
    //Label LogInLabel = new Label("Авторизация");

    //Поля
    TextField LogInField = new TextField("Имя пользователя");
    PasswordField PassField = new PasswordField("Пароль");


    public tLoginView(){
        setSizeFull();
        VerticalLayout LoginViewLayOut = new VerticalLayout();


        VerticalLayout LoginBox = new VerticalLayout();
        LoginBox.setSpacing(true);

        LogInField.setWidth("320px");
        LogInField.setIcon(FontAwesome.USER);
        //LogInField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        PassField.setWidth("320px");
        PassField.setIcon(FontAwesome.KEY);
        //PassField.addStyleName(ValoTheme.TEXTFIELD_SMALL);

        ThemeResource resource = new ThemeResource("TJAY.png");

        Image image = new Image(null,resource);
        image.setWidth("907px");
        image.setHeight("100px");

        //LogInLabel.setSizeUndefined();
        //LoginBox.addComponent(LogInLabel);
        LoginBox.addComponent(image);
        LoginBox.addComponent(new Label());
        LoginBox.addComponent(LogInField);
        LoginBox.addComponent(PassField);

        //LoginBox.setComponentAlignment(LogInLabel,Alignment.MIDDLE_CENTER);
        LoginBox.setComponentAlignment(LogInField,Alignment.MIDDLE_CENTER);
        LoginBox.setComponentAlignment(PassField,Alignment.MIDDLE_CENTER);
        LoginBox.setComponentAlignment(image,Alignment.MIDDLE_CENTER);

        HorizontalLayout ButtonsBox = new HorizontalLayout();
        ButtonsBox.setSpacing(true);
        ButtonsBox.setSizeUndefined();

        LogOnButton.setSizeUndefined();
        LogOnButton.setIcon(FontAwesome.SIGN_IN);
        //LogOnButton.addStyleName(ValoTheme.BUTTON_SMALL);

        RemindPassButton.setSizeUndefined();
        RemindPassButton.setIcon(FontAwesome.QUESTION);
        //RemindPassButton.addStyleName(ValoTheme.BUTTON_SMALL);

        RemindPassButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

            }
        });

        LogOnButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                String username = LogInField.getValue();
                String password = tUsefulFuctions.sha256(PassField.getValue());
                String db_Password = "";

                try {
                    Class.forName(tUsefulFuctions.JDBC_DRIVER);
                    Connection conn = DriverManager.getConnection(
                            tUsefulFuctions.DB_URL
                            ,tUsefulFuctions.USER
                            ,tUsefulFuctions.PASS
                    );

                    CallableStatement CheckUserStmt = conn.prepareCall("{? = call f_get_user_password(?)}");
                    CheckUserStmt.registerOutParameter (1, Types.VARCHAR);
                    CheckUserStmt.setString(2, username);

                    CheckUserStmt.execute();
                    db_Password = CheckUserStmt.getString(1);

                    conn.close();
                } catch(SQLException SQLe){
                    //Handle errors for JDBC
                    SQLe.printStackTrace();
                }catch(Exception e1){
                    //Handle errors for Class.forName
                    e1.printStackTrace();
                }

                if (db_Password == null) {
                    db_Password = "";
                }

                if (db_Password.equals(password) && !password.equals("")) {

                    // Store the current user in the service session
                    getSession().setAttribute("user", username);

                    // Navigate to main view
                    getUI().getNavigator().navigateTo(tMainView.NAME);//

                } else {

                    PassField.setValue("");
                    LogInField.setValue("");
                    PassField.focus();
                    Notification.show("Ошибка авторизации!",
                            "Логин или пароль неверен",
                            Notification.Type.TRAY_NOTIFICATION);

                }
            }
        });

        ButtonsBox.addComponent(LogOnButton);
        ButtonsBox.addComponent(RemindPassButton);
        ButtonsBox.setComponentAlignment(LogOnButton,Alignment.BOTTOM_LEFT);
        ButtonsBox.setComponentAlignment(RemindPassButton,Alignment.BOTTOM_RIGHT);

        LoginBox.addComponent(ButtonsBox);
        LoginBox.setComponentAlignment(ButtonsBox,Alignment.MIDDLE_CENTER);
        LoginBox.setSizeUndefined();


        LoginViewLayOut.addComponent(LoginBox);
        LoginViewLayOut.setComponentAlignment(LoginBox,Alignment.MIDDLE_CENTER);

        LoginViewLayOut.setSizeFull();

        LoginViewLayOut.setExpandRatio(LoginBox,2);



        setCompositionRoot(LoginViewLayOut);

    }

    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // focus the username field when user arrives to the login view
        LogInField.focus();
    }

}
