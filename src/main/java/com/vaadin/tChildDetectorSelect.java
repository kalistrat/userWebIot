package com.vaadin;

import com.vaadin.ui.NativeSelect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalistrat on 31.05.2017.
 */
public class tChildDetectorSelect extends NativeSelect {

    List<tDeviceIdName> ChildDetectors;


    public tChildDetectorSelect(tActuatorStatesLayout eActuatorStatesLayout){

        ChildDetectors = new ArrayList<>();

        Integer iParentLeafId = eActuatorStatesLayout
                .iParentContentLayout.GetParentLeafById(eActuatorStatesLayout.iCurrentLeafId);

        List<Integer> ChildLeafs = eActuatorStatesLayout
                .iParentContentLayout
                .getChildAllLeafsById(iParentLeafId);

        for (Integer iL : ChildLeafs) {
            String ChildLeafActionType = (String) eActuatorStatesLayout
                    .iParentContentLayout
                    .itTree
                    .TreeContainer.getItem(iL).getItemProperty(7).getValue();

            if (ChildLeafActionType != null) {
                if (ChildLeafActionType.equals("Измерительное устройство")) {
                    Integer ChildUserDeviceId = eActuatorStatesLayout
                            .iParentContentLayout
                            .getLeafUserDeviceId(iL);
                    String ChildUserDeviceName = eActuatorStatesLayout
                            .iParentContentLayout
                            .GetLeafNameById(iL);

                    ChildDetectors.add(new tDeviceIdName(ChildUserDeviceId, ChildUserDeviceName));
                    addItem(ChildUserDeviceName);
                }
            }
        }

    }

    public Integer getUserDeviceIdByName(String qUserDeviceName){
        Integer sUserDeviceId = null;
        for (tDeviceIdName iL : ChildDetectors){
            if (iL.UserDeviceName.equals(qUserDeviceName)) {
                sUserDeviceId = iL.UserDeviceId;
            }
        }
        return sUserDeviceId;
    }

}
