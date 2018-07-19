package com.vaadin.registrationContent;

import java.util.Date;

/**
 * Created by kalistrat on 13.07.2017.
 */
public class tUserAttributes {

    String iLog;
    String iPswd;
    String iPhone;
    String iMail;
    String iPost;
    String iSubjType;
    String iSubjName;
    String iSubjAddr;
    String iSubjInn;
    String iSubjKpp;
    String iFirName;
    String iSecName;
    String iMidName;
    Date idBirthdate;

    public tUserAttributes(
            String qLog
            ,String qPswd
            ,String qPhone
            ,String qMail
            ,String qPost
            ,String qSubjType
            ,String qSubjName
            ,String qSubjAddr
            ,String qSubjInn
            ,String qSubjKpp
            ,String qFirName
            ,String qSecName
            ,String qMidName
            ,Date qdBirthdate
    ){
        iLog = qLog;
        iPswd = qPswd;
        iPhone = qPhone;
        iMail = qMail;
        iPost = qPost;
        iSubjType = qSubjType;
        iSubjName = qSubjName;
        iSubjAddr = qSubjAddr;
        iSubjInn = qSubjInn;
        iSubjKpp = qSubjKpp;
        iFirName = qFirName;
        iSecName = qSecName;
        iMidName = qMidName;
        idBirthdate = qdBirthdate;
    }
}
