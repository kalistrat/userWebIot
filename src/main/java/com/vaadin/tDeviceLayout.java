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

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalistrat on 25.11.2016.
 */
public class tDeviceLayout extends VerticalLayout {


    public tDeviceLayout(int eLeafId, tTreeContentLayout eParentContentLayout){

        Integer iUserDeviceId = (Integer) eParentContentLayout.itTree.getItem(eLeafId).getItemProperty(6).getValue();
        String iActionType = (String) eParentContentLayout.itTree.getItem(eLeafId).getItemProperty(7).getValue();
        String iLeafName = (String) eParentContentLayout.itTree.getItem(eLeafId).getItemProperty(4).getValue();
        //System.out.println("eLeafId: " + eLeafId);

        if (iActionType.equals("Измерительное устройство")) {
            this.addComponent(new tDetectorLayout(Integer.valueOf(iUserDeviceId),iLeafName,eLeafId,eParentContentLayout));
        }
        if (iActionType.equals("Исполнительное устройство")) {
            this.addComponent(new tActuatorLayout(Integer.valueOf(iUserDeviceId),iLeafName,eLeafId,eParentContentLayout));
        }

    }

}
