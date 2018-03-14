package com.vaadin;

import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by kalistrat on 30.05.2017.
 */
public class tSignConditionLayout extends VerticalLayout {

    NativeSelect SignValueSelect;

    public tSignConditionLayout(String svalue, boolean isSelectEnabled){
        SignValueSelect = new NativeSelect();
        SignValueSelect.addItem(">");
        SignValueSelect.addItem("<");
        SignValueSelect.addItem("=");
        SignValueSelect.addItem(">=");
        SignValueSelect.addItem("<=");
        SignValueSelect.addStyleName("SelectFont");
        SignValueSelect.setNullSelectionAllowed(false);
        SignValueSelect.select(svalue);
        SignValueSelect.setEnabled(isSelectEnabled);

        this.addComponent(SignValueSelect);
        this.setSizeUndefined();
        this.setMargin(false);
    }
}
