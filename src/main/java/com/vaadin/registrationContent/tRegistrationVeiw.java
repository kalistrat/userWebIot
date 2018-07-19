package com.vaadin.registrationContent;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.tLoginView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by kalistrat on 14.12.2016.
 */
public class tRegistrationVeiw extends CustomComponent implements View {

    public static final String NAME = "Registration";
    Button ReturnLog;

    public tRegistrationVeiw(){
    }

    public void enter(ViewChangeListener.ViewChangeEvent event) {

        ReturnLog = new Button("Вернуться");
        ReturnLog.addStyleName(ValoTheme.BUTTON_LINK);
        ReturnLog.addStyleName(ValoTheme.BUTTON_SMALL);
        ReturnLog.setIcon(com.vaadin.icons.VaadinIcons.ENTER_ARROW);

        ThemeResource resource = new ThemeResource("SNSLOG.png");
        Image image = new Image(null,resource);
        image.setWidth("300px");
        image.setHeight("80px");
        VerticalLayout emptLay = new VerticalLayout();

        ReturnLog.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                getUI().getNavigator().navigateTo(tLoginView.NAME);
            }
        });

        HorizontalLayout TopSec = new HorizontalLayout(
                new Label(),image,ReturnLog
        );
        TopSec.setComponentAlignment(ReturnLog,Alignment.TOP_RIGHT);
        TopSec.setComponentAlignment(image,Alignment.MIDDLE_CENTER);
        TopSec.setHeight("90px");
        TopSec.setWidth("100%");

        tRegistrationFormLayout RegForm = new tRegistrationFormLayout();
        RegForm.setSizeUndefined();
        VerticalLayout BottomSec = new VerticalLayout(
                RegForm
        );
        //BottomSec.setWidth("100%");
        BottomSec.setSizeFull();
        BottomSec.setMargin(true);
        BottomSec.setComponentAlignment(RegForm,Alignment.TOP_CENTER);

        VerticalSplitPanel ContentPanel = new VerticalSplitPanel();
        ContentPanel.setFirstComponent(TopSec);
        ContentPanel.setSecondComponent(BottomSec);
        ContentPanel.setSplitPosition(90, Unit.PIXELS);
        ContentPanel.setMaxSplitPosition(90, Unit.PIXELS);
        ContentPanel.setMinSplitPosition(90,Unit.PIXELS);

        ContentPanel.setHeight("800px");

        VerticalLayout tRegistrationVeiwContent = new VerticalLayout(
                ContentPanel
        );
        tRegistrationVeiwContent.setSizeFull();


        setCompositionRoot(tRegistrationVeiwContent);
    }
}
