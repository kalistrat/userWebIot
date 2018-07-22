package com.vaadin;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.*;

/**
 * Created by kalistrat on 13.05.2017.
 */
public class tChangeNameWindow extends Window {

    Button SaveButton;
    TextField EditTextField;
    tTreeContentLayout iTreeContentLayout;
    int iLeafId;
    Label iTopLabel;


    public tChangeNameWindow(int eLeafId
            ,tTreeContentLayout eParentContentLayout
                             ,Label eTopLabel
                             ,TextField ChangingTextField

    ){
        iLeafId = eLeafId;
        iTreeContentLayout = eParentContentLayout;
        iTopLabel = eTopLabel;


        this.setIcon(VaadinIcons.PENCIL);
        this.setCaption(" Введите новое наименование");

        EditTextField = new TextField("Новое наименование");
        EditTextField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        EditTextField.setValue(
                iTreeContentLayout.GetLeafNameById(iLeafId)
        );


        SaveButton = new Button("Сохранить");

        SaveButton.setData(this);
        SaveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        SaveButton.setIcon(FontAwesome.SAVE);

        SaveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                String sErrorMessage = "";
                String sFieldValue = EditTextField.getValue();

                if (sFieldValue == null){
                    sErrorMessage = "Новое значение не задано\n";
                }

                if (sFieldValue.equals("")){
                    sErrorMessage = "Новое значение не задано\n";
                }

                if (sFieldValue.length() > 30){
                    sErrorMessage = "Длина наименования превышает 30 символов\n";
                }

                if (tUsefulFuctions.fIsLeafNameBusy(iTreeContentLayout.iUserLog,sFieldValue) > 0){
                    sErrorMessage = "Указанное наименование уже используется. Введите другое.\n";
                }

                if (!sErrorMessage.equals("")){
                    Notification.show("Ошибка сохранения:",
                            sErrorMessage,
                            Notification.Type.TRAY_NOTIFICATION);
                } else {

                    String IconCode = iTreeContentLayout.getLeafIconCode(iLeafId);

                    if (IconCode.equals("FOLDER")) {
                        iTopLabel.setValue(VaadinIcons.FOLDER.getHtml() + " " + sFieldValue);
                    }

                    if (IconCode.equals("TACHOMETER")) {
                        iTopLabel.setValue(FontAwesome.TACHOMETER.getHtml() + " " + sFieldValue);
                    }

                    if (IconCode.equals("AUTOMATION")) {
                        iTopLabel.setValue(VaadinIcons.AUTOMATION.getHtml() + " " + sFieldValue);
                    }

                    if (IconCode.equals("QUESTION")) {
                        iTopLabel.setValue(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + " " + sFieldValue);
                    }

                    if (IconCode.equals("CLOSE_CIRCLE")) {
                        iTopLabel.setValue(VaadinIcons.CLOSE_CIRCLE.getHtml() + " " + sFieldValue);
                    }

                    if (ChangingTextField != null) {
                        ChangingTextField.setValue(sFieldValue);
                    }


                    iTreeContentLayout.setNewLeafName(iLeafId,sFieldValue);
                    renameUserLeaf(iTreeContentLayout.iUserLog,iLeafId,sFieldValue);

                    Notification.show("Наименование изменено!",
                            null,
                            Notification.Type.TRAY_NOTIFICATION);
                    UI.getCurrent().removeWindow((tChangeNameWindow) clickEvent.getButton().getData());

                }


            }
        });

        HorizontalLayout ButtonsLayout = new HorizontalLayout(
                SaveButton
        );

        ButtonsLayout.setSizeUndefined();
        ButtonsLayout.setSpacing(true);

        VerticalLayout MessageLayout = new VerticalLayout(
                EditTextField
        );
        MessageLayout.setSpacing(true);
        MessageLayout.setWidth("320px");
        MessageLayout.setHeightUndefined();
        MessageLayout.setMargin(true);
        MessageLayout.setComponentAlignment(EditTextField, Alignment.MIDDLE_CENTER);
        MessageLayout.addStyleName(ValoTheme.LAYOUT_WELL);

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


    public void renameUserLeaf(String qUserLog,int qLeafId,String qNewLeafName){
        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement NewLeafNameStmt = Con.prepareCall("{call p_rename_leaf(?, ?, ?)}");
            NewLeafNameStmt.setString(1, qUserLog);
            NewLeafNameStmt.setInt(2, qLeafId);
            NewLeafNameStmt.setString(3, qNewLeafName);
            NewLeafNameStmt.execute();
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
