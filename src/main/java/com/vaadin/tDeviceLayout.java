package com.vaadin;


import com.vaadin.actuatorContent.tActuatorLayout;
import com.vaadin.detectorContent.tDetectorLayout;
import com.vaadin.ui.VerticalLayout;


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
