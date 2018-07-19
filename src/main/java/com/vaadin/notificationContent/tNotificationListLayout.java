package com.vaadin.notificationContent;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.tUsefulFuctions;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalistrat on 02.11.2017.
 */
public class tNotificationListLayout extends VerticalLayout {
    List<CheckBox> checkBoxes;

    public tNotificationListLayout() {
            checkBoxes = new ArrayList<>();
            setNotificationTypesList();
        for (CheckBox iChb: checkBoxes) {
            addComponent(iChb);
        }
    }

    private void setNotificationTypesList(){
        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );


            String sqlString = "select nt.notification_code\n" +
                    "from notification_type nt";
            PreparedStatement Stmt = Con.prepareStatement(sqlString);
            ResultSet sqlRs = Stmt.executeQuery();
            while (sqlRs.next()) {

                CheckBox iCheckBox = new CheckBox();
                iCheckBox.setEnabled(true);
                if (sqlRs.getString(1).equals("WHATSUP")) {
                    iCheckBox.setIcon(FontAwesome.WHATSAPP);
                } else if (sqlRs.getString(1).equals("MAIL")) {
                    iCheckBox.setIcon(VaadinIcons.ENVELOPE);
                } else if (sqlRs.getString(1).equals("SMS")) {
                    iCheckBox.setIcon(VaadinIcons.COMMENT);
                }
                iCheckBox.setCaption(sqlRs.getString(1));
                iCheckBox.addStyleName(ValoTheme.CHECKBOX_SMALL);
                checkBoxes.add(iCheckBox);
            }
            Con.close();

        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }
    }

    public void markNotification(String notificationType){
        for (CheckBox iChb: checkBoxes){
            if (iChb.getCaption().equals(notificationType)){
                iChb.setValue(true);
            }
        }
    }

    public void unmarkNotification(String notificationType){
        for (CheckBox iChb: checkBoxes){
            if (iChb.getCaption().equals(notificationType)){
                iChb.setValue(false);
            }
        }
    }

    public void setEnabledFalse(){
        for (CheckBox iChb: checkBoxes){
            iChb.setEnabled(false);
        }
    }

    public String getValuesStr(){
        String s = "";
        for (CheckBox iChb: checkBoxes) {
            if (iChb.getValue()){
                s = s + iChb.getCaption() + "|";
            }
        }
        return  s;
    }

}
