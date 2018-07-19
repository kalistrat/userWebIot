package com.vaadin.actuatorContent;

import com.vaadin.*;
import com.vaadin.detectorContent.tDetectorLastMeasureLayout;
import com.vaadin.detectorContent.tDetectorMeasuresJournalLayout;
import com.vaadin.diagramContent.tDiagramLayout;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by kalistrat on 11.05.2017.
 */
public class tActuatorLayout extends VerticalLayout {

    Button tReturnParentFolderButton;
    Integer tCurrentLeafId;
    Label TopLabel;
    tTreeContentLayout tParentContentLayout;
    Button EditSubTreeNameButton;
    Button DeleteSubTreeButton;
    int iUserDeviceId;

    tActuatorDataFormLayout ActuatorDataFormLayout;
    tDescriptionLayout DeviceDescription;
    tActuatorStatesLayout ActuatorStatesLayout;
    tActuatorStateConditionLayout ActuatorStateConditionLayout;
    tDetectorLastMeasureLayout DeviceLastState;
    tDetectorMeasuresJournalLayout MeasureJournal;
    tActuatorTaskLayout actuatorTaskLayout;
    tDiagramLayout actuatorGraphLayout;

    public tActuatorLayout(int eUserDeviceId, String eLeafName, int eLeafId,tTreeContentLayout eParentContentLayout){

        this.tCurrentLeafId = eLeafId;
        this.tParentContentLayout = eParentContentLayout;
        iUserDeviceId = eUserDeviceId;

        ActuatorDataFormLayout = new tActuatorDataFormLayout(iUserDeviceId);

        TopLabel = new Label();
        TopLabel.setContentMode(ContentMode.HTML);


        TopLabel.setValue(VaadinIcons.AUTOMATION.getHtml() + " " + eLeafName);
        TopLabel.addStyleName(ValoTheme.LABEL_COLORED);
        TopLabel.addStyleName(ValoTheme.LABEL_SMALL);
        TopLabel.addStyleName("TopLabel");


        tReturnParentFolderButton = new Button("Вверх");
        tReturnParentFolderButton.setIcon(FontAwesome.LEVEL_UP);
        tReturnParentFolderButton.addStyleName(ValoTheme.BUTTON_SMALL);
        tReturnParentFolderButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        tReturnParentFolderButton.addStyleName("TopButton");

        tReturnParentFolderButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Integer iParentLeafId = tParentContentLayout.GetParentLeafById(tCurrentLeafId);
                //System.out.println("tCurrentLeafId: " + tCurrentLeafId);
                //System.out.println("iParentLeafId: " + iParentLeafId);
                if (iParentLeafId != 0){
                    tParentContentLayout.tTreeContentLayoutRefresh(iParentLeafId,0);
                }
            }
        });

        EditSubTreeNameButton = new Button();
        EditSubTreeNameButton.setIcon(VaadinIcons.EDIT);
        EditSubTreeNameButton.addStyleName(ValoTheme.BUTTON_SMALL);
        EditSubTreeNameButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);

        EditSubTreeNameButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                UI.getCurrent().addWindow(new tChangeNameWindow(tCurrentLeafId
                        ,tParentContentLayout
                        ,TopLabel
                        ,ActuatorDataFormLayout.NameTextField
                ));
            }
        });

        DeleteSubTreeButton = new Button("Удалить");
        DeleteSubTreeButton.setIcon(VaadinIcons.CLOSE_CIRCLE);
        DeleteSubTreeButton.addStyleName(ValoTheme.BUTTON_SMALL);
        DeleteSubTreeButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        DeleteSubTreeButton.addStyleName("TopButton");

        DeleteSubTreeButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                UI.getCurrent().addWindow(new tDeviceDeleteWindow(tCurrentLeafId
                        ,tParentContentLayout
                ));
            }
        });


        HorizontalLayout DetectorEditLayout = new HorizontalLayout(
                DeleteSubTreeButton
                ,tReturnParentFolderButton
        );
        DetectorEditLayout.setSizeUndefined();

        HorizontalLayout LabelEditLayout = new HorizontalLayout(
                TopLabel
                ,EditSubTreeNameButton
        );
        LabelEditLayout.setSizeUndefined();
        LabelEditLayout.setSpacing(true);

        HorizontalLayout TopLayout = new HorizontalLayout(
                LabelEditLayout
                ,DetectorEditLayout
        );

        TopLayout.setComponentAlignment(LabelEditLayout,Alignment.MIDDLE_LEFT);
        TopLayout.setComponentAlignment(DetectorEditLayout,Alignment.MIDDLE_RIGHT);

        TopLayout.setSizeFull();
        TopLayout.setMargin(new MarginInfo(false, true, false, true));



        DeviceDescription = new tDescriptionLayout(iUserDeviceId);
        DeviceLastState = new tDetectorLastMeasureLayout(iUserDeviceId,"ACTUATOR");
        MeasureJournal = new tDetectorMeasuresJournalLayout(iUserDeviceId,"ACTUATOR");
        actuatorGraphLayout = new tDiagramLayout(
                iUserDeviceId
                ,"DETECTOR"
                ,"текст"
        );
        ActuatorStatesLayout = new tActuatorStatesLayout(iUserDeviceId
                ,tParentContentLayout
                ,tCurrentLeafId
        );
        ActuatorStateConditionLayout = new tActuatorStateConditionLayout(
                iUserDeviceId
                ,ActuatorStatesLayout
                ,tCurrentLeafId
        );

        actuatorTaskLayout = new tActuatorTaskLayout(ActuatorStatesLayout);

        VerticalLayout ContentStatesLayout = new VerticalLayout(
                ActuatorStatesLayout
                ,ActuatorStateConditionLayout
                ,actuatorTaskLayout
        );

        VerticalLayout ContentImageStatesLayout = new VerticalLayout(
                ActuatorDataFormLayout
                ,DeviceLastState
                ,actuatorGraphLayout
                ,MeasureJournal
                ,DeviceDescription
        );

        ContentStatesLayout.setMargin(true);
        ContentStatesLayout.setSpacing(true);
        ContentStatesLayout.setWidth("100%");
        ContentStatesLayout.setHeightUndefined();

        ContentImageStatesLayout.setMargin(true);
        ContentImageStatesLayout.setSpacing(true);
        ContentImageStatesLayout.setWidth("100%");
        ContentImageStatesLayout.setHeightUndefined();

        TabSheet ActuatorTabSheet = new TabSheet();
        ActuatorTabSheet.addTab(ContentImageStatesLayout, "Журнал состояний актуатора", VaadinIcons.CHART,0);
        ActuatorTabSheet.addTab(ContentStatesLayout, "Настройка состояний актуатора", VaadinIcons.COGS,1);
        ActuatorTabSheet.addStyleName(ValoTheme.TABSHEET_COMPACT_TABBAR);
        ActuatorTabSheet.addStyleName(ValoTheme.TABSHEET_FRAMED);
        ActuatorTabSheet.setSizeFull();
        ActuatorTabSheet.addStyleName("TabSheetSmall");

        VerticalLayout ContentLayout = new VerticalLayout(
                ActuatorTabSheet
        );


        ContentLayout.setMargin(false);
        ContentLayout.setSpacing(true);
        ContentLayout.setWidth("100%");
        ContentLayout.setHeightUndefined();

        VerticalSplitPanel SplPanel = new VerticalSplitPanel();
        SplPanel.setFirstComponent(TopLayout);
        SplPanel.setSecondComponent(ContentLayout);
        SplPanel.setSplitPosition(40, Unit.PIXELS);
        SplPanel.setMaxSplitPosition(40, Unit.PIXELS);
        SplPanel.setMinSplitPosition(40,Unit.PIXELS);

        SplPanel.setHeight("1400px");
        //SplPanel.setWidth("1000px");

        this.addComponent(SplPanel);
        this.setSpacing(true);
        this.setHeight("100%");
        this.setWidth("100%");



    }
}
