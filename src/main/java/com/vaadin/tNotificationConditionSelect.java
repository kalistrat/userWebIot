package com.vaadin;

import com.vaadin.data.Property;
import com.vaadin.ui.NativeSelect;

/**
 * Created by kalistrat on 02.11.2017.
 */
public class tNotificationConditionSelect extends NativeSelect {

    public tNotificationConditionSelect(tNotificationCriteriaField tCre){

        this.addItem("Измеряемая величина > критического значения");
        this.addItem("Измеряемая величина находится в интервале значений");
        this.addItem("Измеряемая величина < критического значения");

        this.select("Измеряемая величина > критического значения");
        this.setNullSelectionAllowed(false);

        this.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if (((String) valueChangeEvent.getProperty().getValue()).equals("Измеряемая величина находится в интервале значений")){
                    tCre.reBuildLayout(true);
                } else {
                    tCre.reBuildLayout(false);
                }
            }
        });

    }
}
