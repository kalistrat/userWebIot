package com.vaadin.diagramContent;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;

/**
 * Created by kalistrat on 13.11.2017.
 */
@JavaScript({"d3.min.js","diagram_connector.js"})

public class Diagram extends AbstractJavaScriptComponent {
    public void setCoords(final String coords) {
        getState().setCoords(coords);
    }


    @Override
    public DiagramState getState() {
        return (DiagramState) super.getState();
    }
}
