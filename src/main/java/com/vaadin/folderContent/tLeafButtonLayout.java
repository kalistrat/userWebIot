package com.vaadin.folderContent;

import com.vaadin.tTreeContentLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by kalistrat on 02.03.2017.
 */
public class tLeafButtonLayout extends VerticalLayout {
    tLeafButton LeafButton;
    public tLeafButtonLayout(int iButtonLeafId,tTreeContentLayout iParentContentLayout){

        LeafButton = new tLeafButton(iButtonLeafId, iParentContentLayout);
        this.addComponent(this.LeafButton);
        //this.setMargin(true);
        this.setSpacing(true);
        this.setComponentAlignment(this.LeafButton, Alignment.MIDDLE_CENTER);
        this.setSizeUndefined();
    }
}
