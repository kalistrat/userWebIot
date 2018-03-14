package com.vaadin;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by kalistrat on 03.06.2017.
 */
public class tStateConditionDeleteWindow extends Window {

    Button DeleteButton;
    Button CancelButton;
    Label WarningLabel;

    public tStateConditionDeleteWindow(int eUserDeviceId
                ,String eParentStateName
                ,int eConditionNum
                ,tActuatorStateConditionLayout actuatorStateConditionLayout
    ){


        this.setIcon(VaadinIcons.CLOSE_CIRCLE);
        this.setCaption(" Удаление условия состояния");

        WarningLabel = new Label();

        WarningLabel = new Label(
                        "Вы уверены, что хотите удалить,\n"
                        + "выбранное условие. Откатить эту\n"
                        + "операцию будет невозможно."
        );
        WarningLabel.setContentMode(ContentMode.PREFORMATTED);
        WarningLabel.addStyleName("WarningFont");


        DeleteButton = new Button("Удалить");
        DeleteButton.setData(this);
        DeleteButton.addStyleName(ValoTheme.BUTTON_SMALL);
        DeleteButton.addStyleName(ValoTheme.BUTTON_LINK);
        DeleteButton.setIcon(VaadinIcons.CLOSE_CIRCLE);

        DeleteButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                deleteStateCondition(
                eUserDeviceId
                ,eParentStateName
                ,eConditionNum
                );

                actuatorStateConditionLayout.StateConditionTableRefresh();

                Notification.show("Условие удалёно!",
                        null,
                        Notification.Type.TRAY_NOTIFICATION);
                UI.getCurrent().removeWindow((tStateConditionDeleteWindow) clickEvent.getButton().getData());

            }
        });

        CancelButton = new Button("Отменить");
        CancelButton.setData(this);
        CancelButton.addStyleName(ValoTheme.BUTTON_SMALL);
        CancelButton.addStyleName(ValoTheme.BUTTON_LINK);
        CancelButton.setIcon(FontAwesome.HAND_STOP_O);

        CancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                UI.getCurrent().removeWindow((tStateConditionDeleteWindow) clickEvent.getButton().getData());
            }
        });

        HorizontalLayout ButtonsLayout = new HorizontalLayout(
                DeleteButton
                ,CancelButton
        );

        ButtonsLayout.setSizeUndefined();
        ButtonsLayout.setSpacing(true);

        VerticalLayout MessageLayout = new VerticalLayout(
                WarningLabel
        );
        MessageLayout.setSpacing(true);
        MessageLayout.setWidth("350px");
        MessageLayout.setHeightUndefined();
        MessageLayout.setMargin(true);
        MessageLayout.setComponentAlignment(WarningLabel, Alignment.MIDDLE_CENTER);
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

    public void deleteStateCondition(
            int qUserDeviceId
            ,String qParentStateName
            ,int qConditionNum
    ){

        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement Stmt = Con.prepareCall("{call p_delete_state_condition(?, ?, ?)}");
            Stmt.setInt(1, qUserDeviceId);
            Stmt.setString(2, qParentStateName);
            Stmt.setInt(3, qConditionNum);

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
