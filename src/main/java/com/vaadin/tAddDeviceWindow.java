package com.vaadin;

import com.vaadin.data.Item;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;

import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Created by kalistrat on 17.05.2017.
 */
public class tAddDeviceWindow extends Window {

    Button SaveButton;
    Button CancelButton;
    TextField EditTextField;
    TextField deviceUID;
    tTreeContentLayout iTreeContentLayout;
    int iLeafId;

    int iNewTreeId;
    int iNewLeafId;
    int iNewUserDeviceId;
    String iNewIconCode;

    NativeSelect TimeZoneSelect;
    TextField TimeSyncInterval;


    public tAddDeviceWindow(int eLeafId
            ,tTreeContentLayout eParentContentLayout
    ){
        iLeafId = eLeafId;
        iTreeContentLayout = eParentContentLayout;

        iNewTreeId = 0;
        iNewLeafId = 0;


        this.setIcon(VaadinIcons.PLUG);
        this.setCaption(" Добавление нового устройства");

        EditTextField = new TextField("Наименование устройства :");
        EditTextField.addStyleName(ValoTheme.TEXTFIELD_SMALL);

        deviceUID = new TextField("UID устройства :");
        deviceUID.addStyleName(ValoTheme.TEXTFIELD_SMALL);

        TimeZoneSelect = new NativeSelect("Часовой пояс устройства :");
        TimeZoneSelect.setNullSelectionAllowed(false);
        tUsefulFuctions.setTimeZoneList(TimeZoneSelect);
        TimeZoneSelect.select("UTC+3");
        TimeSyncInterval = new TextField("Интервал синхронизации времени (в сутках) :");
        TimeSyncInterval.setValue("1");
        TimeSyncInterval.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        StringToIntegerConverter plainIntegerConverter = new StringToIntegerConverter() {
            protected java.text.NumberFormat getFormat(Locale locale) {
                NumberFormat format = super.getFormat(locale);
                format.setGroupingUsed(false);
                return format;
            };
        };
        TimeSyncInterval.setConverter(plainIntegerConverter);
        TimeSyncInterval.addValidator(new IntegerRangeValidator("Значение может изменяться от 1 до 365", 1, 365));
        TimeSyncInterval.setConversionError("Введённое значение не является целочисленным");
        TimeSyncInterval.setNullRepresentation("");
        TimeSyncInterval.setValue("");


        SaveButton = new Button("Сохранить");

        SaveButton.setData(this);
        SaveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        SaveButton.setIcon(FontAwesome.SAVE);
        SaveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                String sErrorMessage = "";
                String sFieldValue = EditTextField.getValue();
                String sUID = deviceUID.getValue();
                String sTimeSync = TimeSyncInterval.getValue();



                if (sFieldValue == null){
                    sErrorMessage = "Наименование устройства не задано\n";
                }

                if (sFieldValue.equals("")){
                    sErrorMessage = sErrorMessage + "Наименование устройства не задано\n";
                }

                if (sUID == null){
                    sErrorMessage = "UID устройства не задан\n";
                }

                if (sUID.equals("")){
                    sErrorMessage = sErrorMessage + "UID устройства не задан\n";
                }

                if (sFieldValue.length() > 30){
                    sErrorMessage = sErrorMessage + "Длина наименования превышает 30 символов\n";
                }

                if (tUsefulFuctions.fIsLeafNameBusy(iTreeContentLayout.iUserLog,sFieldValue) > 0){
                    sErrorMessage = sErrorMessage + "Указанное наименование уже используется. Введите другое.\n";
                }

                if (!tUsefulFuctions.isSubscriberExists()) {
                    sErrorMessage = sErrorMessage + "Сервер подписки недоступен\n";
                }

                int timeSyncInt = 0;

                if (sTimeSync != null){

                    if (tUsefulFuctions.StrToIntValue(sTimeSync)!= null) {

                        timeSyncInt = Integer.parseInt(sTimeSync);

                        if (timeSyncInt < 1) {
                            sErrorMessage = sErrorMessage + "Интервал синхронизации не может быть меньше суток\n";
                        }
                        if (timeSyncInt > 365) {
                            sErrorMessage = sErrorMessage + "Интервал синхронизации превышает 365 суток\n";
                        }

                    } else {
                        sErrorMessage = sErrorMessage + "Интервал синхронизации некорректный\n";
                    }

                } else {
                    sErrorMessage = sErrorMessage + "Не задан интервал синхронизации\n";
                }

                if (sTimeSync.equals("")){
                    sErrorMessage = sErrorMessage + "Не задан интервал синхронизации\n";
                }

                String oWsResponse = overAllWsCheckUserDevice(sUID,iTreeContentLayout.iUserLog);

                if (oWsResponse != null) {
                    if (oWsResponse.equals("DEVICE_NOT_FOUND")) {
                        sErrorMessage = sErrorMessage + "Устройство с UID " + sUID + " не зарегистрировано в системе\n";
                    } else if (oWsResponse.equals("WRONG_LOGIN_PASSWORD")) {
                        sErrorMessage = sErrorMessage + "Для пользователя " + iTreeContentLayout.iUserLog + " не синхронизирован пароль в системе\n";
                    } else if (oWsResponse.equals("EXECUTION_ERROR")) {
                        sErrorMessage = sErrorMessage + "Произошла ошибка выполнения\n";
                    }
                } else {
                    sErrorMessage = sErrorMessage + "Проверка устройства не может быть произведена\n";
                }

                if (!sErrorMessage.equals("")){
                    Notification.show("Ошибка сохранения:",
                            sErrorMessage,
                            Notification.Type.TRAY_NOTIFICATION);
                } else {

                    String sDeviceLogin = iTreeContentLayout.iUserLog + RandomStringUtils.random(5, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
                    String sDevicePass = RandomStringUtils.random(7, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
                    String sDevicePassSha = tUsefulFuctions.sha256(sDevicePass);



                       addUserDevice(
                                iLeafId//int qParentLeafId
                                , sFieldValue//String qDeviceName
                                , iTreeContentLayout.iUserLog//String qUserLog
                                , "UNKNOWN" //String qActionTypeName
                                , sUID
                        );

                        String addSubsribeRes = "";
                        String addTaskRes = "";



                        int NewTaskId;

                        NewTaskId = addUserDeviceTask(
                                iNewUserDeviceId
                        , "SYNCTIME"
                        , 1
                        , "DAYS"
                        );

                        addTaskRes = tUsefulFuctions.sendMessAgeToSubcribeServer(
                                NewTaskId
                                , iTreeContentLayout.iUserLog
                                , "add"
                                , "task"
                        );



                        Item newItem = iTreeContentLayout.itTree.TreeContainer.addItem(iNewLeafId);
                        newItem.getItemProperty(1).setValue(iNewTreeId);
                        newItem.getItemProperty(2).setValue(iNewLeafId);
                        newItem.getItemProperty(3).setValue(iLeafId);
                        newItem.getItemProperty(4).setValue(sFieldValue);
                        newItem.getItemProperty(5).setValue(iNewIconCode);
                        newItem.getItemProperty(6).setValue(iNewUserDeviceId);
                        newItem.getItemProperty(7).setValue("UNKNOWN");

                        iTreeContentLayout.itTree.TreeContainer.setChildrenAllowed(iNewLeafId,false);
                        iTreeContentLayout.itTree.TreeContainer.setChildrenAllowed(iLeafId,true);

                        iTreeContentLayout.itTree.TreeContainer.setParent(iNewLeafId, iLeafId);

                    if (iNewIconCode.equals("FOLDER")) {
                            iTreeContentLayout.itTree.setItemIcon(iNewLeafId, VaadinIcons.FOLDER);
                        }
                        if (iNewIconCode.equals("TACHOMETER")) {
                            iTreeContentLayout.itTree.setItemIcon(iNewLeafId, FontAwesome.TACHOMETER);
                        }
                        if (iNewIconCode.equals("AUTOMATION")) {
                            iTreeContentLayout.itTree.setItemIcon(iNewLeafId, VaadinIcons.AUTOMATION);
                        }

                        iTreeContentLayout.tTreeContentLayoutRefresh(iLeafId, 0);
                        iTreeContentLayout.itTree.expandItem(iLeafId);




                    if (!addSubsribeRes.equals("")) {
                            Notification.show("Устройство добавлено c ошибкой",
                                    addSubsribeRes,
                                    Notification.Type.TRAY_NOTIFICATION);
                            UI.getCurrent().removeWindow((tAddDeviceWindow) clickEvent.getButton().getData());
                        } else if (!addTaskRes.equals("")) {

                            Notification.show("Устройство добавлено c ошибкой",
                                    addTaskRes,
                                    Notification.Type.TRAY_NOTIFICATION);
                            UI.getCurrent().removeWindow((tAddDeviceWindow) clickEvent.getButton().getData());
                        } else {
                            Notification.show("Устройство добавлено!",
                                    null,
                                    Notification.Type.TRAY_NOTIFICATION);
                            UI.getCurrent().removeWindow((tAddDeviceWindow) clickEvent.getButton().getData());
                       }
                    }


            }
        });

        CancelButton = new Button("Отменить");

        CancelButton.setData(this);
        CancelButton.addStyleName(ValoTheme.BUTTON_SMALL);
        CancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                UI.getCurrent().removeWindow((tAddDeviceWindow) clickEvent.getButton().getData());
            }
        });

        HorizontalLayout ButtonsLayout = new HorizontalLayout(
                SaveButton
                ,CancelButton
        );

        ButtonsLayout.setSizeUndefined();
        ButtonsLayout.setSpacing(true);

        FormLayout IniDevParamLayout = new FormLayout(
                EditTextField
                ,deviceUID
                ,TimeZoneSelect
                ,TimeSyncInterval
        );

        IniDevParamLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        IniDevParamLayout.setSizeUndefined();
        IniDevParamLayout.addStyleName("FormFont");
        IniDevParamLayout.setMargin(false);

        VerticalLayout MessageLayout = new VerticalLayout(
                IniDevParamLayout
        );
        MessageLayout.setSpacing(true);
        MessageLayout.setWidth("520px");
        MessageLayout.setHeightUndefined();
        MessageLayout.setMargin(new MarginInfo(true,false,true,false));
        MessageLayout.setComponentAlignment(IniDevParamLayout, Alignment.MIDDLE_CENTER);
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


    public void addUserDevice(
        int qParentLeafId
        , String qDeviceName
        , String qUserLog
        , String qActionTypeName
        , String qInTopicName
    ){
        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement addDeviceStmt = Con.prepareCall("{call p_add_user_device(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            addDeviceStmt.setInt(1, qParentLeafId);
            addDeviceStmt.setString(2, qDeviceName);
            addDeviceStmt.setString(3, qUserLog);
            addDeviceStmt.setString(4, qActionTypeName);
            addDeviceStmt.registerOutParameter(5, Types.INTEGER);
            addDeviceStmt.registerOutParameter(6, Types.INTEGER);
            addDeviceStmt.registerOutParameter(7, Types.VARCHAR);
            addDeviceStmt.registerOutParameter(8, Types.INTEGER);
            addDeviceStmt.setString(9, qInTopicName);

            addDeviceStmt.execute();

            iNewTreeId = addDeviceStmt.getInt(5);
            iNewLeafId = addDeviceStmt.getInt(6);
            iNewIconCode = addDeviceStmt.getString(7);
            iNewUserDeviceId = addDeviceStmt.getInt(8);

            Con.close();


        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
            //return "Ошибка JDBC";
        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
            //return "Ошибка Class.forName";
        }

    }

    public Integer addUserDeviceTask(
            int qUserDeviceId
            , String eTaskTypeName
            , int eTaskInterval
            , String eIntervalType
    ){
        Integer iTaskId = 0;
        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement addDeviceTaskStmt = Con.prepareCall("{call p_add_task(?, ?, ?, ?, ?, ?)}");
            addDeviceTaskStmt.setInt(1, qUserDeviceId);
            addDeviceTaskStmt.setString(2, eTaskTypeName);
            addDeviceTaskStmt.setInt(3, eTaskInterval);
            addDeviceTaskStmt.setString(4, eIntervalType);
            addDeviceTaskStmt.setNull(5,Types.VARCHAR);
            addDeviceTaskStmt.registerOutParameter(6, Types.INTEGER);

            addDeviceTaskStmt.execute();

            iTaskId = addDeviceTaskStmt.getInt(6);

            Con.close();


        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
            //return "Ошибка JDBC";
        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
            //return "Ошибка Class.forName";
        }
        return iTaskId;

    }

    private String overAllWsCheckUserDevice(
            String UID
            ,String userLogin
    ){
        String respWs = null;

        try {

            List<String> WsArgs = getOverAllWseArgs(userLogin);
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(WsArgs.get(1));

            post.setHeader("Content-Type", "text/xml");

            String reqBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:com=\"http://com/\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <com:checkUserDevice>\n" +
                    "         <!--Optional:-->\n" +
                    "         <arg0>"+UID+"</arg0>\n" +
                    "         <!--Optional:-->\n" +
                    "         <arg1>"+userLogin+"</arg1>\n" +
                    "         <!--Optional:-->\n" +
                    "         <arg2>"+WsArgs.get(0)+"</arg2>\n" +
                    "      </com:checkUserDevice>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";

            StringEntity input = new StringEntity(reqBody, Charset.forName("UTF-8"));
            post.setEntity(input);
            HttpResponse response = client.execute(post);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            Document resXml = tUsefulFuctions.loadXMLFromString(rd.lines().collect(Collectors.joining()));
            respWs = XPathFactory.newInstance().newXPath()
                    .compile("//return").evaluate(resXml);


        } catch (Exception e){
            e.printStackTrace();

        }
        return respWs;
    }

    public List getOverAllWseArgs(String UserLog){
        List Args = new ArrayList<String>();

        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement Stmt = Con.prepareCall("{call getOverAllWseArgs(?,?,?)}");
            Stmt.setString(1,UserLog);
            Stmt.registerOutParameter (2, Types.VARCHAR);
            Stmt.registerOutParameter (3, Types.VARCHAR);
            Stmt.execute();
            Args.add(Stmt.getString(2));
            Args.add(Stmt.getString(3));
            Con.close();

        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }

        return Args;
    }
}
