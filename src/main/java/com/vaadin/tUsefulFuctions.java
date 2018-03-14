package com.vaadin;

import com.vaadin.ui.NativeSelect;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;
import java.security.MessageDigest;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kalistrat on 24.01.2017.
 */
public class tUsefulFuctions {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/things";
    static final String USER = "kalistrat";
    static final String PASS = "045813";

    public static List<String> GetListFromString(String DevidedString,String Devider){
        List<String> StrPieces = new ArrayList<String>();
        int k = 0;
        String iDevidedString = DevidedString;

        if (DevidedString.contains(Devider)) {

            while (!iDevidedString.equals("")) {
                int Pos = iDevidedString.indexOf(Devider);
                StrPieces.add(iDevidedString.substring(0, Pos));
                iDevidedString = iDevidedString.substring(Pos + 1);
                k = k + 1;
                if (k > 100000) {
                    iDevidedString = "";
                }
            }
        }

        return StrPieces;
    }

    public static Document loadXMLFromString(String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }


    public static List<tMark> GetMarksFromString(String MarksString,String AxeTitle){
        List<tMark> MarksList = new ArrayList<tMark>();
        //System.out.println("MarksString :" + MarksString);
        List<String> MarksPairs = GetListFromString(MarksString,"/");
        for (String sPair : MarksPairs){
            int iPos = sPair.indexOf("#");
            if (AxeTitle.equals("x")) {
                tMark tPair = new tMark(Integer.parseInt(sPair.substring(iPos+1)),0,sPair.substring(0, iPos));
                MarksList.add(tPair);
            } else {
                tMark tPair = new tMark(0,Integer.parseInt(sPair.substring(iPos+1)),sPair.substring(0, iPos));
                MarksList.add(tPair);
            }
        }
        return MarksList;
    }

    public static List<String> GetCaptionList(List<tIdCaption> eIdCaptionList){
        List<String> iIdCaptionList = new ArrayList<String>();
        for (tIdCaption iIdC : eIdCaptionList){
            iIdCaptionList.add(iIdC.tCaption);
        }
        return iIdCaptionList;
    }

    public static Integer GetIdByCaption(List<tIdCaption> eIdCaptionList,String eCaption){
        Integer iId = null;
        for (tIdCaption iIdC : eIdCaptionList){
            if (iIdC.tCaption.equals(eCaption)){
                iId = iIdC.tId;
            }
        }

        return iId;
    }

    public static Double GetDoubleFromString(String Val){
        Double dVal = null;
        if ((Val != null) || (!Val.equals(""))) {
            dVal = Double.parseDouble(Val.replace(",", "."));
        }
        return dVal;
    }

    public static Double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber.replace(",", "."));
            } catch(Exception e) {
                return null;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return null;
    }


    public static int fIsLeafNameBusy(String qUserLog,String qNewLeafName){
        int IsBusy = 0;

        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement LeafNameBusyStmt = Con.prepareCall("{? = call fIsLeafNameExists(?, ?)}");
            LeafNameBusyStmt.registerOutParameter (1, Types.INTEGER);
            LeafNameBusyStmt.setString(2, qUserLog);
            LeafNameBusyStmt.setString(3, qNewLeafName);
            LeafNameBusyStmt.execute();
            IsBusy = LeafNameBusyStmt.getInt(1);
            Con.close();

        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }

        return IsBusy;
    }

    public static void deleteUserDevice(
            String qUserLog
            ,int qLeafId
    ){
        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement deleteDeviceStmt = Con.prepareCall("{call p_delete_user_device(?, ?)}");
            deleteDeviceStmt.setString(1, qUserLog);
            deleteDeviceStmt.setInt(2, qLeafId);
            deleteDeviceStmt.execute();

            Con.close();


        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();

        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();

        }

    }

    public static void deleteTreeLeaf(
            String qUserLog
            ,int qLeafId
    ){
        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement deleteLeafStmt = Con.prepareCall("{call p_delete_tree_leaf(?, ?)}");
            deleteLeafStmt.setString(1, qUserLog);
            deleteLeafStmt.setInt(2, qLeafId);
            deleteLeafStmt.execute();

            Con.close();

        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }

    }

    public static void refreshUserTree(
            String qUserLog
    ){
        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement treeStmt = Con.prepareCall("{call p_refresh_user_tree(?)}");
            treeStmt.setString(1, qUserLog);
            treeStmt.execute();

            Con.close();

        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }

    }

    public static void getUserDetectorData(
            int qUserDeviceId
            ,tDetectorFormLayout qParamsForm
            //,tDescriptionLayout qDescriptionForm
            ,tDetectorUnitsLayout qUnitsForm
    ){

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");


        try {
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            String DataSql = "select ud.device_user_name\n" +
                    ",ud.user_device_measure_period\n" +
                    ",ud.user_device_date_from\n" +
                    ",ud.device_units\n" +
                    ",ud.mqtt_topic_write\n" +
                    ",ser.server_ip mqqtt\n" +
                    ",ud.description\n" +
                    ",concat(un.unit_name,concat(' : ',un.unit_symbol))\n" +
                    ",uf.factor_value\n" +
                    ",ifnull(ud.device_log,'') device_log\n" +
                    ",ifnull(ud.device_pass,'') device_pass\n" +
                    ",ud.measure_data_type\n" +
                    "from user_device ud\n" +
                    "left join mqtt_servers ser on ser.server_id = ud.mqqt_server_id\n" +
                    "left join unit un on un.unit_id = ud.unit_id\n" +
                    "left join unit_factor uf on uf.factor_id = ud.factor_id\n" +
                    "where ud.user_device_id = ?";

            PreparedStatement DetectorDataStmt = Con.prepareStatement(DataSql);
            DetectorDataStmt.setInt(1,qUserDeviceId);

            ResultSet DetectorDataRs = DetectorDataStmt.executeQuery();

            while (DetectorDataRs.next()) {
                qParamsForm.NameTextField.setValue(DetectorDataRs.getString(1));
                qParamsForm.PeriodMeasureSelect.select(DetectorDataRs.getString(2));
                if (DetectorDataRs.getTimestamp(3) != null) {
                    qParamsForm.DetectorAddDate.setValue(df.format(new Date(DetectorDataRs.getTimestamp(3).getTime())));
                } else {
                    qParamsForm.DetectorAddDate.setValue("");
                }
                qUnitsForm.UnitTextField.setValue(DetectorDataRs.getString(4));
                qParamsForm.InTopicNameField.setValue(DetectorDataRs.getString(5));
                qUnitsForm.UnitSymbolSelect.select(DetectorDataRs.getString(8));
                qUnitsForm.UnitFactorSelect.select(DetectorDataRs.getString(9));
                qParamsForm.ArrivedDataTypeSelect.select(DetectorDataRs.getString(12));

            }


            Con.close();

        } catch (SQLException se3) {
            //Handle errors for JDBC
            se3.printStackTrace();
        } catch (Exception e13) {
            //Handle errors for Class.forName
            e13.printStackTrace();
        }
    }


    public static void updateDeviceDescription(
            int qUserDeviceId
            ,String qDescValue
    ){
        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement Stmt = Con.prepareCall("{call p_device_description_update(?, ?)}");
            Stmt.setInt(1, qUserDeviceId);
            Stmt.setString(2, qDescValue);

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

    public static String sendMessAgeToSubcribeServer(
            int qEntityId
            ,String qUserLog
            ,String qActionType
            ,String MessAgeType
    ){
        try {

            Socket s = new Socket("localhost", 3128);
            String InMessageValue = qActionType + "/" + qUserLog + "/" + MessAgeType +"/" + String.valueOf(qEntityId) + "/";


            s.getOutputStream().write(InMessageValue.getBytes());
            // читаем ответ
            byte buf[] = new byte[256 * 1024];
            int r = s.getInputStream().read(buf);
            String outSubscriberMessage = new String(buf, 0, r);
            List<String> MessageAttr = GetListFromString(outSubscriberMessage,"|");
            //System.out.println("Is operation Sussess :" + MessageAttr.get(0));
            //System.out.println("Operation Message:" + MessageAttr.get(0));
            s.close();

            if (MessageAttr.get(0).equals("N")) {
                return MessageAttr.get(1);
            } else {
                return "";
            }

        }
        catch(IOException e) {
            return "Ошибка подключения к серверу подписки";
        }
    }

    public static boolean isSubscriberExists(){
        try {

            Socket s = new Socket("localhost", 3128);
            s.getOutputStream().write("test".getBytes());
            byte buf[] = new byte[256 * 1024];
            int r = s.getInputStream().read(buf);
            String outSubscriberMessage = new String(buf, 0, r);
            s.close();
//            System.out.println(outSubscriberMessage);

            if (outSubscriberMessage != null) {
                return true;
            } else {
                return false;
            }

        }
        catch(IOException e) {
            return false;
        }
    }

    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static void getMqttServerData(NativeSelect qMqttServerSelect){

        try {
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            String DataSql = "select s.server_ip\n" +
                    "from mqtt_servers s";

            PreparedStatement MqttDataStmt = Con.prepareStatement(DataSql);

            ResultSet MqttDataRs = MqttDataStmt.executeQuery();

            while (MqttDataRs.next()) {
                qMqttServerSelect.addItem(MqttDataRs.getString(1));
            }


            Con.close();

        } catch (SQLException se3) {
            //Handle errors for JDBC
            se3.printStackTrace();
        } catch (Exception e13) {
            //Handle errors for Class.forName
            e13.printStackTrace();
        }
    }

    public static boolean IsLatinAndDigits(String stringCode){
        Pattern p1 = Pattern.compile("^[a-zA-Z0-9]+$");
        Matcher m1 = p1.matcher(stringCode);
        //System.out.println("m.matches() :" + m.matches());
        //System.out.println("Matcher m :" + m);

        return m1.matches();
    }

    public static boolean IsDigits(String stringCode){
        Pattern p2 = Pattern.compile("^[0-9]+$");
        Matcher m2 = p2.matcher(stringCode);
        //System.out.println("m.matches() :" + m.matches());
        //System.out.println("Matcher m :" + m);

        return m2.matches();
    }

    public static boolean IsEmailName(String stringCode){
        Pattern p3 = Pattern.compile("^[-\\w.]+@([A-z0-9][-A-z0-9]+\\.)+[A-z]{2,4}$");
        Matcher m3 = p3.matcher(stringCode);
        //System.out.println("m.matches() :" + m.matches());
        //System.out.println("Matcher m :" + m);

        return m3.matches();
    }

    public static Integer StrToIntValue(String Sval) {

        try {
            //System.out.println(Sval);
            return Integer.parseInt(Sval);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static void updateActuatorLoginPassWord(
            int qUserDeviceId
            ,String qDeviceLog
            ,String qDevicePass
    ){
        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement Stmt = Con.prepareCall("{call p_device_login_update(?, ?, ?)}");
            Stmt.setInt(1, qUserDeviceId);
            Stmt.setString(2, qDeviceLog);
            Stmt.setString(3, qDevicePass);

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

    public static int genRandInt(int mii,int mai){
        Random rnd = new Random(System.currentTimeMillis());
        int number = mii + rnd.nextInt(mai - mii + 1);
        rnd = null;
        System.gc();

        return  number;
    }

    public static String genSign() {
        Random rnds = new Random(System.currentTimeMillis());
        int SignNum = 1 + rnds.nextInt(3);
        rnds = null;
        System.gc();

        switch (SignNum) {
            case 1 : return "+";
            case 2 : return "-";
            case 3 : return "*";
            default: return  "+";
        }

    }


    public static Integer calculateAge(Date birthday)
    {

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTime(birthday);
        // include day of birth
        dob.add(Calendar.DAY_OF_MONTH, -1);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) <= dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    public static void setTimeZoneList(NativeSelect eListBox){

        try {
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            String DataSql = "select tz.timezone_value\n" +
                    "from timezones tz";

            PreparedStatement DataStmt = Con.prepareStatement(DataSql);

            ResultSet DataRs = DataStmt.executeQuery();

            while (DataRs.next()) {
                eListBox.addItem(DataRs.getString(1));
            }


            Con.close();

        } catch (SQLException se3) {
            //Handle errors for JDBC
            se3.printStackTrace();
        } catch (Exception e13) {
            //Handle errors for Class.forName
            e13.printStackTrace();
        }
    }

    public static Integer isExistsContLogIn(String qLogIn){
        Integer isE = 0;
        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement callStmt = Con.prepareCall("{? = call fIsExistsContLogin(?)}");
            callStmt.registerOutParameter(1, Types.INTEGER);
            callStmt.setString(2, qLogIn);
            callStmt.execute();

            isE =  callStmt.getInt(1);

            Con.close();

        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }
        return isE;
    }

    public static Integer isExistsUserMail(String qMailValue){
        Integer isE = 0;
        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement callStmt = Con.prepareCall("{? = call fisExistsUserMail(?)}");
            callStmt.registerOutParameter(1, Types.INTEGER);
            callStmt.setString(2, qMailValue);
            callStmt.execute();

            isE =  callStmt.getInt(1);

            Con.close();

        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }
        return isE;
    }

    public static Integer isExistsUserLogin(String qLoginValue){
        Integer isE = 0;
        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement callStmt = Con.prepareCall("{? = call fisExistsUserLogin(?)}");
            callStmt.registerOutParameter(1, Types.INTEGER);
            callStmt.setString(2, qLoginValue);
            callStmt.execute();

            isE =  callStmt.getInt(1);

            Con.close();

        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }
        return isE;
    }

    public static String getDataBaseXMLString(
            String storedFunctionCall
            ,int storedVariable
    ){
        try {

            Class.forName(JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    DB_URL
                    , USER
                    , PASS
            );

            CallableStatement Stmt = Con.prepareCall("{? = call " + storedFunctionCall + "(?)}");
            Stmt.registerOutParameter(1, Types.BLOB);
            Stmt.setInt(2,storedVariable);
            Stmt.execute();
            Blob CondValue = Stmt.getBlob(1);
            String resultStr = new String(CondValue.getBytes(1l, (int) CondValue.length()));
            Con.close();
            return resultStr;

        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
            return null;
        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
            return null;
        }
    }




}
