package com.vaadin.folderContent;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.tTreeContentLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalistrat on 13.01.2017.
 */
public class tLeafButton extends Button {

    public tLeafButton(int eButtonLeafId,tTreeContentLayout iParentContentLayout){

        String iButtonCaption = (String) iParentContentLayout.itTree.getItem(eButtonLeafId).getItemProperty(4).getValue();
        String iButtonIconCode = (String) iParentContentLayout.itTree.getItem(eButtonLeafId).getItemProperty(5).getValue();
        Integer iUserDeviceId = (Integer) iParentContentLayout.itTree.getItem(eButtonLeafId).getItemProperty(6).getValue();

        //iButtonCaption = iButtonCaption.replace(" ","\n");
        //System.out.println(iButtonCaption);

        this.setCaption(doStringWrap(iButtonCaption));

        if (iButtonIconCode.equals("FOLDER")) {
            this.setIcon(VaadinIcons.FOLDER);
        }
        if (iButtonIconCode.equals("TACHOMETER")) {
            this.setIcon(FontAwesome.TACHOMETER);
        }
        if (iButtonIconCode.equals("AUTOMATION")) {
            this.setIcon(VaadinIcons.AUTOMATION);
        }
        if (iButtonIconCode.equals("QUESTION")) {
            this.setIcon(VaadinIcons.QUESTION_CIRCLE_O);
        }

        if (iButtonIconCode.equals("CLOSE_CIRCLE")) {
            this.setIcon(VaadinIcons.CLOSE_CIRCLE);
        }

        this.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                iParentContentLayout.tTreeContentLayoutRefresh(eButtonLeafId,iUserDeviceId);
            }
        });


        this.setSizeUndefined();
        this.addStyleName("ButtonHugeIcon");
        this.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_TOP);
        this.addStyleName(ValoTheme.BUTTON_LINK);


    }

    public static String doStringWrap(String inputStr){
        StringBuffer s0 = new StringBuffer(inputStr.trim());
        List<Integer> sIndxList = new ArrayList<Integer>();
        int k = 1;
        int sPacePos = 0;
        int csym = 0;
        int csymMax = 4;

        if (s0.substring(0, 1).equals("\n")) {
            s0.deleteCharAt(0);
        }

        if (s0.indexOf(" ") != -1) {

            for (int i = 0; i < s0.length(); i++) {

                if ((s0.substring(i, i + 1).equals(" ")) && (csym > csymMax)) {
                    sPacePos = i;
                    csym = 0;
                } else {
                    csym = csym + 1;
                }

                if ((!sIndxList.contains(sPacePos)) && (csym == 0)) {
                    k = k + 1;
                    sIndxList.add(sPacePos);
                }

            }

            for (Integer ii : sIndxList) {
                s0.setCharAt(ii, '\n');
            }

        } else {

            csymMax = 10;

            for (int i = 0; i < s0.length(); i++) {
                if (csym < csymMax) {
                    csym = csym + 1;
                } else {
                    csym = 0;
                    s0.insert(i,'\n');
                }
            }

        }

        if (s0.substring(s0.length()-1, s0.length()).equals("\n")) {
            s0.deleteCharAt(s0.length());
        }

        return s0.toString();
    }

}
