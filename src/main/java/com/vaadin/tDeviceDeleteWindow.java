package com.vaadin;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalistrat on 19.05.2017.
 */
public class tDeviceDeleteWindow extends Window {
    Button DeleteButton;
    Button CancelButton;
    Label WarningLabel;
    tTreeContentLayout iTreeContentLayout;
    int iLeafId;


    public tDeviceDeleteWindow(int eLeafId
            ,tTreeContentLayout eParentContentLayout
    ) {
        iLeafId = eLeafId;
        iTreeContentLayout = eParentContentLayout;

        this.setIcon(VaadinIcons.CLOSE_CIRCLE);
        this.setCaption(" Удаление устройства");

        WarningLabel = new Label();

        WarningLabel = new Label(
                "ВНИМАНИЕ! Данное устройство будет удалено.\n"
                + "Вы не сможете востановить информацию\n"
                + "об измерениях и переходах"
        );
        WarningLabel.setContentMode(ContentMode.PREFORMATTED);
        WarningLabel.addStyleName("WarningFont");


        DeleteButton = new Button("Удалить");
        DeleteButton.setData(this);
        DeleteButton.addStyleName(ValoTheme.BUTTON_SMALL);
        DeleteButton.setIcon(VaadinIcons.CLOSE_CIRCLE);

        DeleteButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                String sErrorMessage = "";


                if (!tUsefulFuctions.isSubscriberExists()) {
                    sErrorMessage = sErrorMessage + "Сервер подписки недоступен\n";
                }

                if (!tUsefulFuctions.isDataBaseExists()) {
                    sErrorMessage = sErrorMessage + "Сервер базы данных недоступен\n";
                }

                String oWsResponse = tUsefulFuctions.overAllWsCheckUserDevice(iTreeContentLayout.getDeviceUID(iLeafId),iTreeContentLayout.iUserLog);

                if (oWsResponse == null) {
                    sErrorMessage = sErrorMessage + "Общий веб-сервис недоступен\n";
                } else {
                    if (oWsResponse.equals("DEVICE_NOT_FOUND")) {
                        sErrorMessage = sErrorMessage + "Устройство с UID " + iTreeContentLayout.getDeviceUID(iLeafId) + " не выпускалось\n";
                    } else if (oWsResponse.equals("WRONG_LOGIN_PASSWORD")) {
                        sErrorMessage = sErrorMessage + "Для пользователя " + iTreeContentLayout.iUserLog + " не синхронизирован пароль в системе\n";
                    } else if (oWsResponse.equals("EXECUTION_ERROR")) {
                        sErrorMessage = sErrorMessage + "Произошла ошибка выполнения\n";
                    }
                }

                if (!sErrorMessage.equals("")){
                    Notification.show("Ошибка удаления:",
                            sErrorMessage,
                            Notification.Type.TRAY_NOTIFICATION);
                } else {

                    String sParentLeafName = iTreeContentLayout
                            .GetLeafNameById(iTreeContentLayout.GetParentLeafById(iLeafId));

                    tUsefulFuctions.sendMessAgeToSubcribeServer(
                            iLeafId
                            , iTreeContentLayout.iUserLog
                            , "call"
                            , "drop"
                    );

                    tUsefulFuctions.deleteUserDevice(iTreeContentLayout.iUserLog, iLeafId);

                    tUsefulFuctions.sendMessAgeToSubcribeServer(
                            777
                            , iTreeContentLayout.iUserLog
                            , "change"
                            , "server"
                    );

                    tUsefulFuctions.overAllWsSetUserDevice(
                            iTreeContentLayout.getDeviceUID(iLeafId)
                            , iTreeContentLayout.iUserLog
                            , "OUTSIDE"
                    );

                    iTreeContentLayout.reloadTreeContainer();
                    Integer iNewParentLeafId = iTreeContentLayout.getLeafIdByName(sParentLeafName);


                    iTreeContentLayout.tTreeContentLayoutRefresh(iNewParentLeafId, 0);

                    Notification.show("Устройство удалёно!",
                            null,
                            Notification.Type.TRAY_NOTIFICATION);
                    UI.getCurrent().removeWindow((tDeviceDeleteWindow) clickEvent.getButton().getData());

                }

            }
        });

        CancelButton = new Button("Отменить");
        CancelButton.setData(this);
        CancelButton.addStyleName(ValoTheme.BUTTON_SMALL);
        CancelButton.setIcon(FontAwesome.HAND_STOP_O);

        CancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                UI.getCurrent().removeWindow((tDeviceDeleteWindow) clickEvent.getButton().getData());
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

    private List<Integer> getUsersTaskList() throws Throwable {
        List<Integer> taskList = new ArrayList<>();

        Document xmlDocument = tUsefulFuctions
                .loadXMLFromString(
                        tUsefulFuctions
                                .getDataBaseXMLString("w_task_device_list"
                                        ,iTreeContentLayout
                                                .getLeafUserDeviceId(iLeafId)
                                )
                );

        Node taskListNode = (Node) XPathFactory.newInstance().newXPath()
                .compile("/task_device_list").evaluate(xmlDocument, XPathConstants.NODE);

        NodeList nodeList = taskListNode.getChildNodes();

        for (int i=0; i<nodeList.getLength(); i++){

            NodeList childNodeList = nodeList.item(i).getChildNodes();

            for (int j=0; j<childNodeList.getLength();j++) {
                if (childNodeList.item(j).getNodeName().equals("task_id")) {
                    taskList.add(Integer.parseInt(childNodeList.item(j).getTextContent()));
                }
            }
        }

        return taskList;
    }
}
