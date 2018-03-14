package com.vaadin;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.*;

/**
 * Created by kalistrat on 18.05.2017.
 */
public class tDetectorUnitsLayout extends VerticalLayout {

    Button SaveButton;
    Button EditButton;
    TextField UnitTextField;
    NativeSelect UnitSymbolSelect;
    NativeSelect UnitFactorSelect;
    int iUserDeviceId;

    public tDetectorUnitsLayout(int eUserDeviceId){

        iUserDeviceId = eUserDeviceId;

        Label Header = new Label();
        Header.setContentMode(ContentMode.HTML);
        Header.setValue(VaadinIcons.SCALE_UNBALANCE.getHtml() + "  " + "Единицы измерения");
        Header.addStyleName(ValoTheme.LABEL_COLORED);
        Header.addStyleName(ValoTheme.LABEL_SMALL);

        SaveButton = new Button();
        SaveButton.setIcon(FontAwesome.SAVE);
        SaveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        SaveButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        SaveButton.setEnabled(false);

        SaveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                String SymSelectValue = (String) UnitSymbolSelect.getValue();
                String FactorSelectValue = (String) UnitFactorSelect.getValue();

                updateUserDeviceUnits(
                        iUserDeviceId
                ,SymSelectValue
                ,FactorSelectValue
                );
                int PosSpl = SymSelectValue.indexOf(":");
                String UnitSym = (SymSelectValue.substring(PosSpl + 1,SymSelectValue.length())).trim();

                if (FactorSelectValue.equals("1")) {
                    UnitTextField.setValue(UnitSym);
                } else {
                    UnitTextField.setValue(UnitSym + " x " + FactorSelectValue);
                }

                SaveButton.setEnabled(false);
                EditButton.setEnabled(true);
                UnitSymbolSelect.setEnabled(false);
                UnitFactorSelect.setEnabled(false);

            }
        });

        EditButton = new Button();
        EditButton.setIcon(VaadinIcons.EDIT);
        EditButton.addStyleName(ValoTheme.BUTTON_SMALL);
        EditButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);

        EditButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                SaveButton.setEnabled(true);
                EditButton.setEnabled(false);
                UnitSymbolSelect.setEnabled(true);
                UnitFactorSelect.setEnabled(true);
            }
        });


        HorizontalLayout FormHeaderButtons = new HorizontalLayout(
                EditButton
                ,SaveButton
        );
        FormHeaderButtons.setSpacing(true);
        FormHeaderButtons.setSizeUndefined();

        HorizontalLayout FormHeaderLayout = new HorizontalLayout(
                Header
                ,FormHeaderButtons
        );
        FormHeaderLayout.setWidth("100%");
        FormHeaderLayout.setHeightUndefined();
        FormHeaderLayout.setComponentAlignment(Header, Alignment.MIDDLE_LEFT);
        FormHeaderLayout.setComponentAlignment(FormHeaderButtons,Alignment.MIDDLE_RIGHT);


        UnitTextField = new TextField("Текстовое обозначение :");
        UnitTextField.setEnabled(false);

        UnitTextField.setValue(String.valueOf(eUserDeviceId));

        UnitSymbolSelect = new NativeSelect("Обозначение из справочника :");
        UnitSymbolSelect.setEnabled(false);
        UnitSymbolSelect.setNullSelectionAllowed(false);
        getUnitsData();

        UnitFactorSelect = new NativeSelect("Множитель :");
        UnitFactorSelect.setEnabled(false);
        UnitFactorSelect.setNullSelectionAllowed(false);
        getFactorData();

        FormLayout UnitsForm = new FormLayout(
                UnitTextField
                ,UnitSymbolSelect
                ,UnitFactorSelect
        );

        UnitsForm.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        UnitsForm.addStyleName("FormFont");
        UnitsForm.setMargin(false);

        VerticalLayout UnitsFormLayout = new VerticalLayout(
                UnitsForm
        );
        UnitsFormLayout.addStyleName(ValoTheme.LAYOUT_CARD);
        UnitsFormLayout.setWidth("100%");
        UnitsFormLayout.setHeightUndefined();

        VerticalLayout ContentLayout = new VerticalLayout(
                FormHeaderLayout
                ,UnitsFormLayout
        );
        ContentLayout.setSpacing(true);
        ContentLayout.setWidth("100%");
        ContentLayout.setHeightUndefined();

        this.addComponent(ContentLayout);


    }

    public void getUnitsData(){

        try {
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            String DataSql = "select concat(u.unit_name,concat(' : ',u.unit_symbol))\n" +
                    "from unit u\n" +
                    "order by u.unit_name";

            PreparedStatement DataStmt = Con.prepareStatement(DataSql);

            ResultSet DataRs = DataStmt.executeQuery();

            while (DataRs.next()) {
                UnitSymbolSelect.addItem(DataRs.getString(1));
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

    public void getFactorData(){

        try {
            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            String DataSql = "select uf.factor_value\n" +
                    "from unit_factor uf";

            PreparedStatement DataStmt = Con.prepareStatement(DataSql);

            ResultSet DataRs = DataStmt.executeQuery();

            while (DataRs.next()) {
                UnitFactorSelect.addItem(DataRs.getString(1));
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

    public void updateUserDeviceUnits(
            int qUserDeviceId
            ,String UnitsTextSelect
            ,String FactorTextSelect
    ){
        try {

            Class.forName(tUsefulFuctions.JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    tUsefulFuctions.DB_URL
                    , tUsefulFuctions.USER
                    , tUsefulFuctions.PASS
            );

            CallableStatement Stmt = Con.prepareCall("{call p_detector_units_update(?, ?, ?)}");
            Stmt.setInt(1, qUserDeviceId);
            Stmt.setString(2, UnitsTextSelect);
            Stmt.setString(3, FactorTextSelect);

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
