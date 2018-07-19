package com.vaadin;


import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.userDataContent.tUserDataFormLayout;

/**
 * Created by SemenovNA on 03.08.2016.
 */
public class tMainView extends CustomComponent implements View {

    public static final String NAME = "";

    String CurrentUsr;
    tTreeContentLayout TreeContentUsr;
    tUserDataFormLayout dataUserFormLayout;

    Button LogOutButton = new Button("Выйти", new Button.ClickListener() {
        @Override
        public void buttonClick(Button.ClickEvent event) {
            // "Logout" the user
            getSession().setAttribute("user", null);
            // Refresh this view, should redirect to login view
            getUI().getNavigator().navigateTo(NAME);
        }
    });


    private TabSheet t;

    public tMainView(){

    }

    public void enter(ViewChangeListener.ViewChangeEvent event) {

        this.CurrentUsr = (String) getSession().getAttribute("user");


        //setSizeFull();

        VerticalLayout tMainViewContent = new VerticalLayout();
        LogOutButton.addStyleName(ValoTheme.BUTTON_LINK);
        LogOutButton.addStyleName(ValoTheme.BUTTON_SMALL);
        LogOutButton.setIcon(FontAwesome.SIGN_OUT);

        ThemeResource resource = new ThemeResource("TJAY.png");

        Image image = new Image(null,resource);
        image.setWidth("907px");
        image.setHeight("100px");


        VerticalLayout imgLay = new VerticalLayout(image);
        imgLay.setHeightUndefined();
        imgLay.setWidth("100%");
        imgLay.setComponentAlignment(image,Alignment.MIDDLE_CENTER);

        VerticalLayout topButtonLayout = new VerticalLayout(
                LogOutButton
        );
        topButtonLayout.setComponentAlignment(LogOutButton,Alignment.TOP_RIGHT);
        topButtonLayout.setHeightUndefined();
        topButtonLayout.setWidth("100%");

        VerticalLayout TopSec = new VerticalLayout(
                topButtonLayout
                ,imgLay
        );
        TopSec.setHeightUndefined();
        TopSec.setWidth("100%");

        tMainViewContent.setSizeFull();

        HorizontalSplitPanel MidSec = new HorizontalSplitPanel();

//        VerticalLayout DeviceForm = new VerticalLayout();
//        DeviceForm.setMargin(true);
//        DeviceForm.addComponent(new tDeviceLayout());

        tTree DeviceTree = new tTree(this.CurrentUsr,this);
        DeviceTree.addStyleName("CaptionTree");

        DeviceTree.setSizeFull();
        //DeviceForm.addStyleName(ValoTheme.LAYOUT_CARD);
        //DeviceTree.addStyleName(ValoTheme.LAYOUT_CARD);

        MidSec.addComponent(DeviceTree);

        this.TreeContentUsr = new tTreeContentLayout(CurrentUsr,DeviceTree);
        MidSec.addComponent(this.TreeContentUsr);

        //MidSec.setHeight("1000px");
        //MidSec.setWidth("1000px");
        MidSec.setSizeFull();
        MidSec.setSplitPosition(35, Unit.PERCENTAGE); // percent
        //MidSec.addStyleName(ValoTheme.LAYOUT_CARD);


        tMainViewContent.addComponent(TopSec);


        VerticalLayout Tab1Cont = new VerticalLayout();
        Tab1Cont.setMargin(true);
        Tab1Cont.addComponent(MidSec);
        // Tab 2 content

        VerticalLayout Tab2Cont = new VerticalLayout();
        Tab2Cont.setMargin(true);
        Tab2Cont.setSizeFull();
        dataUserFormLayout = new tUserDataFormLayout(CurrentUsr);
        dataUserFormLayout.setSizeUndefined();
        Tab2Cont.addComponent(dataUserFormLayout);
        Tab2Cont.setComponentAlignment(dataUserFormLayout,Alignment.MIDDLE_CENTER);

        VerticalLayout Tab3Cont = new VerticalLayout();
        Tab3Cont.setMargin(true);
        Tab2Cont.setSizeFull();
        //tDiagramLayout testDiagram = new tDiagramLayout();
        Tab3Cont.addComponent(new Label("123123"));
        //Tab3Cont.setComponentAlignment(testDiagram,Alignment.MIDDLE_CENTER);

        t = new TabSheet();
        //t.addStyleName(ValoTheme.TABSH);
        //t.setHeight("200px");
        //t.setWidth("400px");

        t.addTab(Tab1Cont, "Дерево устройств", VaadinIcons.FILE_TREE);
        t.addTab(Tab2Cont, "Личные данные", VaadinIcons.USER_CARD);
        t.addTab(Tab3Cont, "Руководство пользователя", FontAwesome.QUESTION_CIRCLE);
        //t.addListener(this);
        t.setSizeFull();

        tMainViewContent.addComponent(t);

        setCompositionRoot(tMainViewContent);
    }
}
