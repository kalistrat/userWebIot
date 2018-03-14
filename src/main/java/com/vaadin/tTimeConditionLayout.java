package com.vaadin;

import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by kalistrat on 30.05.2017.
 */
public class tTimeConditionLayout extends VerticalLayout {
    TextField TimeIntervalTextField;

    public tTimeConditionLayout(String sValue,boolean isTextFieldEnabled){

        TimeIntervalTextField = new TextField();
        TimeIntervalTextField.setValue(sValue);
        TimeIntervalTextField.addStyleName(ValoTheme.TEXTFIELD_TINY);
        TimeIntervalTextField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
        TimeIntervalTextField.setEnabled(isTextFieldEnabled);
        StringToIntegerConverter plainIntegerConverter = new StringToIntegerConverter() {
            protected java.text.NumberFormat getFormat(Locale locale) {
                NumberFormat format = super.getFormat(locale);
                format.setGroupingUsed(false);
                return format;
            };
        };
        TimeIntervalTextField.setConverter(plainIntegerConverter);
        TimeIntervalTextField.addValidator(new IntegerRangeValidator("Значение может изменяться от 5 до 3600", 5, 3600));
        TimeIntervalTextField.setConversionError("Введённое значение не является целочисленным");
        TimeIntervalTextField.setNullRepresentation("");
        TimeIntervalTextField.setWidth("50px");
        Label TimeLabel = new Label("секунд");
        TimeLabel.addStyleName("TableLabel");
        HorizontalLayout TextValueLayout = new HorizontalLayout(
                TimeIntervalTextField
                ,TimeLabel
        );
        TextValueLayout.setSpacing(true);
        TextValueLayout.setMargin(false);
        TextValueLayout.setSizeUndefined();
        addComponent(TextValueLayout);
        setMargin(false);
        setSizeUndefined();
    }
}
