package com.vaadin;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalistrat on 29.05.2017.
 */
public class tButtonTextFieldLayout extends VerticalLayout {
    Button button;
    TextField textfield;
    VerticalLayout KeyBoardLayout;
    Integer IsHidden;
    boolean IsButtonsEnabled;

    public tButtonTextFieldLayout(String sExpresson,boolean isButtonsEnabled){

        IsHidden = 1;
        IsButtonsEnabled = isButtonsEnabled;

        button = new Button();
        button.addStyleName(ValoTheme.BUTTON_SMALL);
        button.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        button.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        button.setIcon(VaadinIcons.KEYBOARD);
        button.setEnabled(IsButtonsEnabled);

        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (IsHidden == 1) {
                    addComponent(KeyBoardLayout);
                    IsHidden = 0;
                } else {
                    removeComponent(KeyBoardLayout);
                    IsHidden = 1;
                }
            }
        });


        textfield = new TextField();
        textfield.setWidth("325px");
        textfield.addStyleName(ValoTheme.TEXTFIELD_TINY);
        textfield.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
        textfield.setEnabled(false);
        textfield.setValue(sExpresson);

        HorizontalLayout TopLayout = new HorizontalLayout(
                button
                ,textfield
        );

        TopLayout.setSpacing(false);
        TopLayout.setMargin(false);
        TopLayout.setSizeUndefined();

        Integer CntRowSym = 11;

        List<String> Syms = new ArrayList<>();
        Syms.add("q");
        Syms.add("w");
        Syms.add("e");
        Syms.add("r");
        Syms.add("t");
        Syms.add("y");
        Syms.add("u");
        Syms.add("i");
        Syms.add("o");
        Syms.add("p");
        Syms.add("a");
        Syms.add("s");
        Syms.add("d");
        Syms.add("f");
        Syms.add("g");
        Syms.add("h");
        Syms.add("j");
        Syms.add("k");
        Syms.add("l");
        Syms.add("z");
        Syms.add("x");
        Syms.add("c");
        Syms.add("v");
        Syms.add("b");
        Syms.add("n");
        Syms.add("m");
        Syms.add("1");
        Syms.add("2");
        Syms.add("3");
        Syms.add("4");
        Syms.add("5");
        Syms.add("6");
        Syms.add("7");
        Syms.add("8");
        Syms.add("9");
        Syms.add("0");
        Syms.add("+");
        Syms.add("-");
        Syms.add("*");
        Syms.add("/");
        Syms.add("(");
        Syms.add(")");
        Syms.add("^");
        Syms.add(".");

        Syms.add("backspace");
        //Syms.add("очистить");

        int ncol = 0;
        int nrow = 1;


        KeyBoardLayout = new VerticalLayout();
        HorizontalLayout RowLayout = new HorizontalLayout();

        for (String iL : Syms) {

            if (ncol >= CntRowSym.intValue()) {
                ncol = 1;
                nrow = nrow + 1;
                KeyBoardLayout.addComponent(RowLayout);
                RowLayout = new HorizontalLayout();
            } else {
                ncol = ncol + 1;
            }
            Button SymButton;

            if (iL.equals("backspace")) {

                SymButton = new Button();
                SymButton.setIcon(VaadinIcons.BACKSPACE_A);
                SymButton.setWidth("15px");
                SymButton.setHeight("15px");
                SymButton.addStyleName(ValoTheme.BUTTON_TINY);
                SymButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
                //SymButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
                SymButton.setEnabled(IsButtonsEnabled);
                SymButton.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        //String Sym = clickEvent.getButton().getCaption();
                        String CurrentValue = textfield.getValue();
                        if (CurrentValue.length()>0) {
                            textfield.setValue(CurrentValue.substring(0, CurrentValue.length() - 1));
                        }
                    }
                });

            } else {

                SymButton = new Button(iL);
                SymButton.setWidth("15px");
                SymButton.setHeight("15px");
                SymButton.addStyleName(ValoTheme.BUTTON_TINY);
                SymButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
                SymButton.setEnabled(IsButtonsEnabled);
                SymButton.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        //String Sym = clickEvent.getButton().getCaption();

                        if (textfield.getValue().length() < 149) {
                            textfield.setValue(textfield.getValue() + clickEvent.getButton().getCaption());
                        } else {
                            Notification.show("Увеличить выражение нельзя:",
                                    "Его длина не должна превышать 150 символов",
                                    Notification.Type.TRAY_NOTIFICATION);
                        }
                    }
                });
            }

            RowLayout.addComponent(SymButton);

            if (iL.equals(Syms.get(Syms.size()-1))) {
                KeyBoardLayout.addComponent(RowLayout);
            }

        }

        KeyBoardLayout.setSizeUndefined();
        //KeyBoardLayout.addStyleName(ValoTheme.LAYOUT_CARD);

        addComponent(TopLayout);
        setComponentAlignment(TopLayout,Alignment.TOP_LEFT);
        setSpacing(false);
        setMargin(false);
        setSizeUndefined();

    }
}
