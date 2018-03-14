package com.vaadin;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalistrat on 29.05.2017.
 */
public class tVarConditionLayout extends VerticalLayout {

    List<tVarNativeSelect> VarList;
    Button getVariableButton;
    VerticalLayout varListLayout;
    tActuatorStatesLayout iActuatorStatesLayout;

    class DoubleList{
        Double Dvalue;
        List<String> vlist;
        DoubleList(Double dvalue,List<String> Vlist){
            Dvalue = dvalue;
            vlist = Vlist;
        }
    }


    public tVarConditionLayout(int StateConditionId
                ,TextField leftExpressonTextFiled
                ,TextField rightExpressonTextFiled
                ,tActuatorStatesLayout actuatorStatesLayout
                ,boolean isSelectEnable
    ){
        VarList = new ArrayList<>();
        iActuatorStatesLayout = actuatorStatesLayout;

        getVariableButton = new Button("Выбрать переменные");
        //getVariableButton.setIcon(FontAwesome.SAVE);
        getVariableButton.addStyleName(ValoTheme.BUTTON_TINY);
        getVariableButton.addStyleName(ValoTheme.BUTTON_LINK);
        getVariableButton.setHeight("20px");

        getVariableButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                removeComponent(varListLayout);
                varListLayout.removeAllComponents();
                VarList.clear();
                DoubleList leftSideResult = getVariablesFromStrExpression(leftExpressonTextFiled.getValue());
                DoubleList rightSideResult = getVariablesFromStrExpression(rightExpressonTextFiled.getValue());
                int ExprFalse = 0;


                if (leftSideResult.vlist.size() == 0 && leftSideResult.Dvalue == null) {
                    Label leftErrLabel = new Label();
                    leftErrLabel.addStyleName(ValoTheme.LABEL_TINY);
                    leftErrLabel.setValue("Левая часть не распознана");
                    varListLayout.addComponent(leftErrLabel);
                    ExprFalse = ExprFalse + 1;
                }

                if (leftSideResult.vlist.size() != 0 && leftSideResult.Dvalue == null) {
                    Label leftErrLabel = new Label();
                    leftErrLabel.addStyleName(ValoTheme.LABEL_TINY);
                    leftErrLabel.setValue("Левая часть не распознана");
                    varListLayout.addComponent(leftErrLabel);
                    ExprFalse = ExprFalse + 1;
                }

                if (rightSideResult.vlist.size() == 0 && rightSideResult.Dvalue == null) {
                    Label rightErrLabel = new Label();
                    rightErrLabel.addStyleName(ValoTheme.LABEL_TINY);
                    rightErrLabel.setValue("Правая часть не распознана");
                    varListLayout.addComponent(rightErrLabel);
                    ExprFalse = ExprFalse + 1;
                }

                if (rightSideResult.vlist.size() != 0 && rightSideResult.Dvalue == null) {
                    Label rightErrLabel = new Label();
                    rightErrLabel.addStyleName(ValoTheme.LABEL_TINY);
                    rightErrLabel.setValue("Правая часть не распознана");
                    varListLayout.addComponent(rightErrLabel);
                    ExprFalse = ExprFalse + 1;
                }

                if (rightSideResult.vlist.size() == 0 && leftSideResult.vlist.size() == 0 && ExprFalse == 0) {
                    Label comErrLabel = new Label();
                    comErrLabel.addStyleName(ValoTheme.LABEL_TINY);
                    comErrLabel.setValue("Не найдено ни одной переменной");
                    varListLayout.addComponent(comErrLabel);
                    ExprFalse = ExprFalse + 1;
                }


                if (ExprFalse == 0) {
                    List<String> mergedList = mergeTwoList(leftSideResult.vlist,rightSideResult.vlist);

                    for (String iVarSym : mergedList){

                        Label VarLabel = new Label();
                        VarLabel.setContentMode(ContentMode.HTML);
                        VarLabel.setValue(iVarSym+ " " + VaadinIcons.ARROW_RIGHT.getHtml());
                        VarLabel.addStyleName(ValoTheme.LABEL_COLORED);
                        VarLabel.addStyleName(ValoTheme.LABEL_SMALL);
                        VarLabel.addStyleName("TopLabel");

                        tChildDetectorSelect VarSelect = new tChildDetectorSelect(iActuatorStatesLayout);
                        VarSelect.setNullSelectionAllowed(false);
                        VarSelect.setEnabled(isSelectEnable);
                        VarSelect.select(VarSelect.ChildDetectors.get(0).UserDeviceName);
                        VarList.add(new tVarNativeSelect(iVarSym,VarSelect));
                        HorizontalLayout VarLayout = new HorizontalLayout(
                                VarLabel
                                ,VarSelect
                        );
                        VarLayout.setSizeUndefined();
                        VarLayout.setSpacing(true);
                        varListLayout.addComponent(VarLayout);


                    }
                    mergedList.clear();
                    rightSideResult.vlist.clear();
                    leftSideResult.vlist.clear();

                }

                addComponent(varListLayout);



            }
        });

        varListLayout = new VerticalLayout();
        varListLayout.setSpacing(true);

        this.addComponent(getVariableButton);
        if (StateConditionId != 0) {
            getVariableButton.setEnabled(false);
            this.setConditionVariables(StateConditionId,isSelectEnable);
            this.addComponent(varListLayout);
        }
        this.setComponentAlignment(getVariableButton,Alignment.MIDDLE_CENTER);
        this.setSpacing(true);
        this.setMargin(new MarginInfo(false,true,false,true));
        this.setSizeUndefined();

    }

    public void setConditionVariables(int qStateConditionId, boolean qSelectEnable){
        try {
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );
            VarList.clear();

            String DataSql = "select condv.var_code\n" +
                    ",ud.device_user_name\n" +
                    "from user_state_condition_vars condv\n" +
                    "join user_device ud on ud.user_device_id=condv.user_device_id\n" +
                    "where condv.actuator_state_condition_id = ?\n" +
                    "order by state_condition_vars_id";

            PreparedStatement DataStmt = Con.prepareStatement(DataSql);
            DataStmt.setInt(1,qStateConditionId);

            ResultSet DataRs = DataStmt.executeQuery();

            while (DataRs.next()) {

                Label VarLabel = new Label();
                VarLabel.setContentMode(ContentMode.HTML);
                VarLabel.setValue(DataRs.getString(1)+ " " + VaadinIcons.ARROW_RIGHT.getHtml());
                VarLabel.addStyleName(ValoTheme.LABEL_COLORED);
                VarLabel.addStyleName(ValoTheme.LABEL_SMALL);
                VarLabel.addStyleName("TopLabel");

                tChildDetectorSelect VarSelect = new tChildDetectorSelect(iActuatorStatesLayout);
                VarSelect.setNullSelectionAllowed(false);
                VarSelect.setEnabled(qSelectEnable);
                VarSelect.select(DataRs.getString(2));
                VarList.add(new tVarNativeSelect(DataRs.getString(1),VarSelect));

                HorizontalLayout VarLayout = new HorizontalLayout(
                        VarLabel
                        ,VarSelect
                );
                VarLayout.setSizeUndefined();
                VarLayout.setSpacing(true);
                varListLayout.addComponent(VarLayout);

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

    public DoubleList getVariablesFromStrExpression(String strExpr){


        MathParser exprParser = new MathParser();

        int k = 0;
        Double ParseValue = null;
        while (k < 150) {

            try {
                ParseValue = exprParser.Parse(strExpr);
                k = 150;
            } catch (Exception e) {
                String MessAge = e.getMessage();
                if (MessAge.contains("нет переменной")) {
                    List<String> MessPieces = tUsefulFuctions.GetListFromString(MessAge, "|");
                    exprParser.setVariable(MessPieces.get(1), 7.0);
                    k = k + 1;
                } else {
                    exprParser.VarList.clear();
                    exprParser.var.clear();
                    k = 150;
                }
            }
        }

        List<String> varExprList = new ArrayList<>(exprParser.VarList);

        exprParser.VarList.clear();
        exprParser.var.clear();

        return new DoubleList(ParseValue,varExprList);
    }

    public List<String> mergeTwoList(List<String> one, List<String> two){

        if (one.size()>0) {
            for (String x : two) {
                if (!one.contains(x))
                    one.add(x);
            }
            return new ArrayList<>(one);
        } else {
            for (String x : one) {
                if (!two.contains(x))
                    two.add(x);
            }
            return new ArrayList<>(two);
        }

    }
}
