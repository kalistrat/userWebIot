package com.vaadin;

import com.vaadin.data.Item;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.*;
import java.util.List;

/**
 * Created by kalistrat on 15.05.2017.
 */
public class tFolderDeleteWindow extends Window {

    Button DeleteButton;
    Button CancelButton;
    Label WarningLabel;
    tTreeContentLayout iTreeContentLayout;
    int iLeafId;


    public tFolderDeleteWindow(int eLeafId
            ,tTreeContentLayout eParentContentLayout
    ){
        iLeafId = eLeafId;
        iTreeContentLayout = eParentContentLayout;


        this.setIcon(VaadinIcons.FOLDER_REMOVE);
        this.setCaption(" Удаление подкаталога");

        WarningLabel = new Label();

        WarningLabel = new Label(
                "ВНИМАНИЕ! Все подкаталоги и устройства,\n"
                + "находящиеся внутри данного каталога\n"
                + "будут удалены. Вы не сможете востановить\n"
                + "информацию об измерениях и переходах"
        );
        WarningLabel.setContentMode(ContentMode.PREFORMATTED);
        WarningLabel.addStyleName("WarningFont");


        DeleteButton = new Button("Удалить");
        DeleteButton.setData(this);
        DeleteButton.addStyleName(ValoTheme.BUTTON_SMALL);
        DeleteButton.setIcon(VaadinIcons.FOLDER_REMOVE);

        DeleteButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

//                String sParentLeafName = iTreeContentLayout
//                        .GetLeafNameById(iTreeContentLayout.GetParentLeafById(iLeafId));

                List<Integer> ChildsLeafs = iTreeContentLayout.getChildAllLeafsById(iLeafId);
                int childLeafsCount = ChildsLeafs.size();

                if (childLeafsCount > 0) {
                    for (int i = 0; i < childLeafsCount; i++) {
                        int RemoveLeafId = ChildsLeafs.get(childLeafsCount-(i+1));

                        if (iTreeContentLayout.getLeafUserDeviceId(RemoveLeafId).intValue()!=0) {

//                            if (iTreeContentLayout.getLeafIconCode(RemoveLeafId).equals("TACHOMETER")) {
//                                tUsefulFuctions.sendMessAgeToSubcribeServer(
//                                        iTreeContentLayout.getLeafUserDeviceId(RemoveLeafId)
//                                        , iTreeContentLayout.iUserLog
//                                        , "delete"
//                                        , "sensor"
//                                );
//                            }




                            tUsefulFuctions.deleteUserDevice(iTreeContentLayout.iUserLog,RemoveLeafId);
                        } else {
                            tUsefulFuctions.deleteTreeLeaf(iTreeContentLayout.iUserLog,RemoveLeafId);
                        }
                    }
                }

                tUsefulFuctions.sendMessAgeToSubcribeServer(
                        iLeafId
                        , iTreeContentLayout.iUserLog
                        , "change"
                        , "server"
                );

                tUsefulFuctions.deleteTreeLeaf(iTreeContentLayout.iUserLog,iLeafId);
                iTreeContentLayout.reloadTreeContainer();
                //Integer iNewParentLeafId = iTreeContentLayout.getLeafIdByName(sParentLeafName);
                iTreeContentLayout.tTreeContentLayoutRefresh(1,0);

//                for (Object id : iTreeContentLayout.itTree.rootItemIds()) {
//                    iTreeContentLayout.itTree.expandItemsRecursively(id);
//                }


                Notification.show("Подкаталог удалён!",
                        null,
                        Notification.Type.TRAY_NOTIFICATION);
                UI.getCurrent().removeWindow((tFolderDeleteWindow) clickEvent.getButton().getData());

            }
        });

        CancelButton = new Button("Отменить");
        CancelButton.setData(this);
        CancelButton.addStyleName(ValoTheme.BUTTON_SMALL);
        CancelButton.setIcon(FontAwesome.HAND_STOP_O);

        CancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                UI.getCurrent().removeWindow((tFolderDeleteWindow) clickEvent.getButton().getData());
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
        MessageLayout.setWidth("400px");
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


}
