package com.vaadin;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by kalistrat on 19.01.2017.
 */
public class tCameraLayout extends VerticalLayout {

    public tCameraLayout(int eUserDeviceId, String eLeafName){

        Label TopLabel = new Label();
        TopLabel.setContentMode(ContentMode.HTML);


        TopLabel.setValue(FontAwesome.VIDEO_CAMERA.getHtml() + " " + eLeafName);
        TopLabel.addStyleName(ValoTheme.LABEL_COLORED);


        Panel CurrentImagePanel = new Panel("Текущee изображение");
        Panel ScheduleRecordPanel = new Panel("График записи видео");

        VerticalLayout CamaraDataLayout = new VerticalLayout(CurrentImagePanel,ScheduleRecordPanel);

        VerticalLayout TopLabelLayout = new VerticalLayout(TopLabel);
        TopLabelLayout.setSizeFull();
        CamaraDataLayout.setSizeFull();
        TopLabelLayout.setMargin(new MarginInfo(false, true, false, true));
        CamaraDataLayout.setMargin(true);

        VerticalSplitPanel SplPanel = new VerticalSplitPanel();
        SplPanel.setFirstComponent(TopLabelLayout);
        SplPanel.setSecondComponent(CamaraDataLayout);
        SplPanel.setSplitPosition(25, Sizeable.UNITS_PIXELS);
        SplPanel.setHeight("500px");

        this.addComponent(SplPanel);
        this.setSpacing(true);
        this.setSizeFull();


    }
}
