package com.vaadin;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;

import java.io.InputStream;
import java.sql.*;

/**
 * Created by kalistrat on 03.01.2017.
 */
public class tRemindWindow extends Window {

    VerticalLayout RemindWindowContent = new VerticalLayout();


    public tRemindWindow(){

        this.setModal(true);
        this.setIcon(FontAwesome.QUESTION);
        this.setCaption(" Напомнить пароль");

        TextField MailTxt = new TextField("Почта, указанная при регистрации");
        MailTxt.setIcon(FontAwesome.ENVELOPE);
        RemindWindowContent.addComponent(MailTxt);

        Button SentLogPassButton = new Button("Прислать логин и пароль", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (!GetLoginByMail(MailTxt.getValue()).equals("")) {
                    sendEmail(MailTxt.getValue());
                } else {
                    Notification.show("Указанная почта не используется ни одним из пользователей");
                }
            }
        });

        SentLogPassButton.setIcon(FontAwesome.SEND);
        RemindWindowContent.addComponent(SentLogPassButton);

        RemindWindowContent.setComponentAlignment(MailTxt, Alignment.MIDDLE_CENTER);
        RemindWindowContent.setComponentAlignment(SentLogPassButton,Alignment.MIDDLE_CENTER);
        RemindWindowContent.setSizeUndefined();
        RemindWindowContent.setSpacing(true);
        RemindWindowContent.setMargin(true);

        this.setContent(RemindWindowContent);
    }

    private void sendEmail(String to) {
        try {

                // all values as variables to clarify its usage
                InputStream inputStream = getClass().getResourceAsStream("/file.pdf");
                String from = "kalique@bk.ru";
                String subject = "Your PDF";
                String text = "Here there is your <b>PDF</b> file!";
                String fileName = "file.pdf";
                String mimeType = "application/pdf";

                SpringEmailService.send(from, to, subject, text, inputStream, fileName, mimeType);

                inputStream.close();

                Notification.show("Письмо с логином и паролем отправлено");

        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Ошибка отправки письма", Notification.Type.ERROR_MESSAGE);
        }
    }

    private String GetLoginByMail(String MailAddressValue){
        String UserLogin = "";
        try {
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection conn = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    ,tUsefulFuctions.USER
                    ,tUsefulFuctions.PASS);

            CallableStatement UserLoginStmt = conn.prepareCall("{? = call f_get_loginbymail(?)}");
            UserLoginStmt.registerOutParameter (1, Types.VARCHAR);
            UserLoginStmt.setString(2, MailAddressValue);
            UserLoginStmt.execute();
            UserLogin = UserLoginStmt.getString(1);
            //System.out.println(UserLogin);


            conn.close();
        } catch(SQLException SQLe){
            //Handle errors for JDBC
            SQLe.printStackTrace();
        }catch(Exception e1){
            //Handle errors for Class.forName
            e1.printStackTrace();
        }
        return  UserLogin;
    }
}
