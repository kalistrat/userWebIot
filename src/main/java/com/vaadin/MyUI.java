package com.vaadin;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

    //public static String OVERALL_WSE_LOCATION;

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        //setPollInterval(3000);

        new Navigator(this, this);
        getNavigator().addView(tLoginView.NAME, tLoginView.class);//
        getNavigator().addView(tMainView.NAME,tMainView.class);
        getNavigator().addView(tRegistrationVeiw.NAME,tRegistrationVeiw.class);


        getNavigator().addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {

                boolean isLoggedIn = getSession().getAttribute("user") != null;
                boolean isLoginView = event.getNewView() instanceof tLoginView;
                boolean isRegistrationView = event.getNewView() instanceof tRegistrationVeiw;

                if (isRegistrationView) {
                return true;
                } else {

                    if (!isLoggedIn && !isLoginView) {
                        getNavigator().navigateTo(tLoginView.NAME);
                        return false;
                    } else if (isLoggedIn && isLoginView) {
                        return false;
                    }

                }
                return true;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {

            }
        });
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }


}
