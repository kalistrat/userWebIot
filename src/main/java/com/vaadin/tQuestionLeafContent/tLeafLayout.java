package com.vaadin.tQuestionLeafContent;

import com.vaadin.*;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by kalistrat on 15.03.2018.
 */
public class tLeafLayout extends VerticalLayout {

    Button tReturnParentFolderButton;
    Label TopLabel;
    Button EditSubTreeNameButton;
    Button DeleteSubTreeButton;

    tLeafFormLayout leafFormLayout;

    public tLeafLayout(int eLeafId
            ,tTreeContentLayout eParentContentLayout
    ){


        TopLabel = new Label();
        TopLabel.setContentMode(ContentMode.HTML);


        TopLabel.setValue(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + " " + eParentContentLayout.GetLeafNameById(eLeafId));
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
                Integer iParentLeafId = eParentContentLayout.GetParentLeafById(eLeafId);
                if (iParentLeafId != 0){
                    eParentContentLayout.tTreeContentLayoutRefresh(iParentLeafId,0);
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
                UI.getCurrent().addWindow(new tChangeNameWindow(eLeafId
                        ,eParentContentLayout
                        ,TopLabel
                        ,null
                ));
            }
        });

        DeleteSubTreeButton = new Button("Удалить");
        DeleteSubTreeButton.setIcon(VaadinIcons.CLOSE_CIRCLE);
        DeleteSubTreeButton.addStyleName(ValoTheme.BUTTON_SMALL);
        DeleteSubTreeButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        DeleteSubTreeButton.addStyleName("TopButton");


        HorizontalLayout LeafEditLayout = new HorizontalLayout(
                DeleteSubTreeButton
                ,tReturnParentFolderButton
        );
        LeafEditLayout.setSizeUndefined();

        HorizontalLayout LabelEditLayout = new HorizontalLayout(
                TopLabel
                ,EditSubTreeNameButton
        );
        LabelEditLayout.setSizeUndefined();
        LabelEditLayout.setSpacing(true);

        HorizontalLayout TopLayout = new HorizontalLayout(
                LabelEditLayout
                ,LeafEditLayout
        );

        TopLayout.setComponentAlignment(LabelEditLayout, Alignment.MIDDLE_LEFT);
        TopLayout.setComponentAlignment(LeafEditLayout,Alignment.MIDDLE_RIGHT);

        TopLayout.setSizeFull();
        TopLayout.setMargin(new MarginInfo(false, true, false, true));

        leafFormLayout = new tLeafFormLayout(eLeafId,eParentContentLayout);

        VerticalLayout ContentPrefLayout = new VerticalLayout(
                leafFormLayout
        );

        ContentPrefLayout.setMargin(true);
        ContentPrefLayout.setSpacing(true);
        ContentPrefLayout.setWidth("100%");
        ContentPrefLayout.setHeightUndefined();


//        TabSheet LeafTabSheet = new TabSheet();
//        LeafTabSheet.addTab(ContentPrefLayout, "Параметры устройства", VaadinIcons.FORM,0);
//
//        LeafTabSheet.addStyleName(ValoTheme.TABSHEET_COMPACT_TABBAR);
//        LeafTabSheet.addStyleName(ValoTheme.TABSHEET_FRAMED);
//        LeafTabSheet.setSizeFull();
//        LeafTabSheet.addStyleName("TabSheetSmall");


        VerticalLayout ContentLayout = new VerticalLayout(
                ContentPrefLayout
        );

        ContentLayout.setMargin(false);
        ContentLayout.setSpacing(true);
        ContentLayout.setSizeFull();

        VerticalSplitPanel SplPanel = new VerticalSplitPanel();
        SplPanel.setFirstComponent(TopLayout);
        SplPanel.setSecondComponent(ContentLayout);
        SplPanel.setSplitPosition(40, Sizeable.Unit.PIXELS);
        SplPanel.setMaxSplitPosition(40, Sizeable.Unit.PIXELS);
        SplPanel.setMinSplitPosition(40, Sizeable.Unit.PIXELS);

        SplPanel.setHeight("500px");

        this.addComponent(SplPanel);
        this.setSpacing(true);
        this.setHeight("100%");
        this.setWidth("100%");


    }
}
