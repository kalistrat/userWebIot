package com.vaadin.folderContent;



import com.vaadin.*;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalistrat on 13.01.2017.
 */
public class tFolderLayout extends VerticalLayout {

    List<Integer> ChildLeafs = new ArrayList<Integer>();
    List<HorizontalLayout> ChildLeafsRows = new ArrayList<HorizontalLayout>();
    Table tFolderTable;
    IndexedContainer tFolderContainer;
    Button tReturnParentFolderButton;
    tTreeContentLayout tParentContentLayout;
    Integer tCurrentLeafId;

    Button DeleteSubTreeButton;
    Button EditSubTreeNameButton;
    Label TopLabel;


    public  tFolderLayout(int LeafId, tTreeContentLayout ParentContentLayout){

        VerticalLayout FolderContentLayout = new VerticalLayout();
        this.tParentContentLayout = ParentContentLayout;
        this.tCurrentLeafId = LeafId;


        int ncol = 0;
        int nrow = 0;


        tFolderTable = new Table();
        tFolderContainer = new IndexedContainer();

        tFolderContainer.addContainerProperty(1, tTableNodeLayout.class, null);
        tFolderContainer.addContainerProperty(2, tTableNodeLayout.class, null);
        tFolderContainer.addContainerProperty(3, tTableNodeLayout.class, null);

        this.ChildLeafs = ParentContentLayout.GetChildLeafsById(LeafId);

        if (this.ChildLeafs.size() != 0) {


            for (int i = 0; i< Math.ceil(this.ChildLeafs.size()/(double) 3); i++){
                this.tFolderContainer.addItem();
                for (int j = 0; j < 3; j++) {
                    this.tFolderContainer.getItem(this.tFolderContainer.getIdByIndex(this.tFolderContainer.size() - 1))
                            .getItemProperty(j+1).setValue(new tTableNodeLayout());
                }
            }

            for (Integer Chids : this.ChildLeafs) {
                ncol = ncol + 1;
                tTableNodeLayout tTableNodeLayoutNode = (tTableNodeLayout) this.tFolderContainer.getItem(this.tFolderContainer.getIdByIndex(nrow))
                        .getItemProperty(ncol).getValue();
                tLeafButtonLayout tLeafButtonNodeLayout = new tLeafButtonLayout(Chids, ParentContentLayout);
                tTableNodeLayoutNode.addComponent(tLeafButtonNodeLayout);
                tTableNodeLayoutNode.setComponentAlignment(tLeafButtonNodeLayout,Alignment.MIDDLE_CENTER);
                if (ncol > 2) {
                    ncol = 0;
                    nrow = nrow + 1;
                }

            }


            this.tFolderTable.setContainerDataSource(this.tFolderContainer);
            this.tFolderTable.setPageLength(this.tFolderContainer.size());
            this.tFolderTable.addStyleName(ValoTheme.TABLE_NO_HEADER);
            this.tFolderTable.addStyleName(ValoTheme.TABLE_NO_STRIPES);
            this.tFolderTable.setColumnAlignment(1,Table.Align.CENTER);
            this.tFolderTable.setColumnAlignment(2,Table.Align.CENTER);
            this.tFolderTable.setColumnAlignment(3,Table.Align.CENTER);


            FolderContentLayout.addComponent(this.tFolderTable);
            FolderContentLayout.setComponentAlignment(this.tFolderTable,Alignment.TOP_CENTER);

        }


        TopLabel = new Label();
        TopLabel.setContentMode(ContentMode.HTML);

        TopLabel.setValue(FontAwesome.FOLDER.getHtml() + " " + ParentContentLayout.GetLeafNameById(LeafId));
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
                if (iParentLeafId != 0){
                    tParentContentLayout.tTreeContentLayoutRefresh(iParentLeafId,0);
                }
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
                if (tParentContentLayout.GetParentLeafById(tCurrentLeafId)!=0) {
                    UI.getCurrent().addWindow(new tFolderDeleteWindow(tCurrentLeafId
                            ,tParentContentLayout
                    ));
                } else {
                    Notification.show(null,
                            "Корневой каталог не может быть удалён",
                            Notification.Type.TRAY_NOTIFICATION);
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

                if (tParentContentLayout.GetParentLeafById(tCurrentLeafId)!=0) {
                    UI.getCurrent().addWindow(new tChangeNameWindow(tCurrentLeafId
                    ,tParentContentLayout
                            ,TopLabel
                            ,null
                    ));
                } else {
                    Notification.show(null,
                            "Корневой каталог не может быть изменён",
                            Notification.Type.TRAY_NOTIFICATION);
                }
            }
        });

        MenuBar EditFolderMenu = new MenuBar();

        MenuBar.MenuItem AdditionItem = EditFolderMenu.addItem("Добавить"
                , VaadinIcons.PLUS
                , null);

        MenuBar.Command mycommand = new MenuBar.Command() {
            public void menuSelected(MenuBar.MenuItem selectedItem) {

//                if (selectedItem.getText().equals("Добавить устройство")) {
//
//                    if (tParentContentLayout.GetParentLeafById(tCurrentLeafId)!=0) {
//                        UI.getCurrent().addWindow(new tAddDeviceWindow(tCurrentLeafId
//                                ,tParentContentLayout
//                        ));
//                    } else {
//                        Notification.show(null,
//                                "В корневой каталог нельзя добавлять устройства, только контроллеры",
//                                Notification.Type.TRAY_NOTIFICATION);
//                    }
//                }

                UI.getCurrent().addWindow(new tAddDeviceWindow(tCurrentLeafId
                        ,tParentContentLayout
                ));

            }
        };

//        AdditionItem.addItem("Добавить контроллер"
//                , VaadinIcons.FOLDER_ADD
//                , mycommand
//        );

        //Item1.setStyleName(ValoTheme.MENU_ITEM);

        AdditionItem.addItem("Добавить устройство"
                , VaadinIcons.PLUG
                , mycommand
        );

        EditFolderMenu.addStyleName(ValoTheme.MENUBAR_SMALL);
        EditFolderMenu.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
        EditFolderMenu.addStyleName("CaptionMenu");

        HorizontalLayout FolderEditLayout = new HorizontalLayout();

        if (tParentContentLayout.GetParentLeafById(tCurrentLeafId) == 0) {
            FolderEditLayout.addComponent(EditFolderMenu);
            FolderEditLayout.addComponent(DeleteSubTreeButton);
            FolderEditLayout.addComponent(tReturnParentFolderButton);
        } else {
            FolderEditLayout.addComponent(DeleteSubTreeButton);
            FolderEditLayout.addComponent(tReturnParentFolderButton);
        }

        FolderEditLayout.setSizeUndefined();

        HorizontalLayout LabelEditLayout = new HorizontalLayout(
                TopLabel
                ,EditSubTreeNameButton
        );
        LabelEditLayout.setSizeUndefined();
        LabelEditLayout.setSpacing(true);

        HorizontalLayout TopLayout = new HorizontalLayout(
                LabelEditLayout
                ,FolderEditLayout
        );

        TopLayout.setComponentAlignment(LabelEditLayout,Alignment.MIDDLE_LEFT);
        TopLayout.setComponentAlignment(FolderEditLayout,Alignment.MIDDLE_RIGHT);

//        TopLayout.setWidth("100%");
//        TopLayout.setHeight("40px");
        TopLayout.setSizeFull();

        FolderContentLayout.setSizeFull();
        TopLayout.setMargin(new MarginInfo(false, true, false, true));
        FolderContentLayout.setMargin(true);


        VerticalLayout ContentLayout = new VerticalLayout();


        if (tParentContentLayout.GetParentLeafById(tCurrentLeafId)!=0) {
            tFolderPrefsFormLayout FolderPrefLayout = new tFolderPrefsFormLayout(tCurrentLeafId, tParentContentLayout.iUserLog);
            FolderPrefLayout.setMargin(true);
            FolderPrefLayout.setSizeFull();

            TabSheet FolderTabSheet = new TabSheet();
            FolderTabSheet.addTab(FolderContentLayout, "Дочерние устройства", VaadinIcons.CLUSTER,0);
            FolderTabSheet.addTab(FolderPrefLayout, "Настройки контроллера", VaadinIcons.COGS,1);
            FolderTabSheet.addStyleName(ValoTheme.TABSHEET_COMPACT_TABBAR);
            FolderTabSheet.addStyleName(ValoTheme.TABSHEET_FRAMED);
            FolderTabSheet.setSizeFull();
            FolderTabSheet.addStyleName("TabSheetSmall");
            ContentLayout.addComponent(FolderTabSheet);
        } else {
            ContentLayout.addComponent(FolderContentLayout);
        }


        ContentLayout.setMargin(false);
        ContentLayout.setSpacing(true);
        ContentLayout.setSizeFull();

        VerticalSplitPanel SplPanel = new VerticalSplitPanel();
        SplPanel.setFirstComponent(TopLayout);
        SplPanel.setSecondComponent(ContentLayout);
        SplPanel.setSplitPosition(40, Unit.PIXELS);
        SplPanel.setMaxSplitPosition(40, Unit.PIXELS);
        SplPanel.setMinSplitPosition(40,Unit.PIXELS);
        SplPanel.setHeight("800px");
        //SplPanel.setWidth("100%");

        this.addComponent(SplPanel);
        this.setSpacing(true);
        this.setSizeFull();

        //this.setSizeFull();

    }

}
