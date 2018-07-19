package com.vaadin.registrationContent;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.tUsefulFuctions;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.hezamu.canvas.Canvas;

import java.util.Random;

/**
 * Created by kalistrat on 14.06.2017.
 */
public class tCaptchaLayout extends VerticalLayout {

    Canvas canvas;
    TextField ResultTextField;
    int captchaRes;

    public tCaptchaLayout(){

        canvas = new Canvas();
        int capWidth = 120;
        int capHeight = 50;

        canvas.setWidth(String.valueOf(capWidth) + "px");
        canvas.setHeight(String.valueOf(capHeight) + "px");

        canvas.beginPath();
        canvas.setLineWidth(2);
        canvas.setStrokeStyle("white");

        canvas.moveTo(0, 0);
        canvas.lineTo(0, capHeight);
        canvas.lineTo(capWidth, capHeight);
        canvas.lineTo(capWidth, 0);
        canvas.lineTo(0, 0);

        canvas.stroke();
        canvas.closePath();

        int ca = tUsefulFuctions.genRandInt(1,99);
        int cb = tUsefulFuctions.genRandInt(1,99);
        String csign = tUsefulFuctions.genSign();
        String genExpr = String.valueOf(ca) + "  " + csign + "  " + String.valueOf(cb);


        if (csign.equals("+")) {
            captchaRes = ca + cb;
        }
        else if (csign.equals("-")) {
            captchaRes = ca - cb;
        }
        else if (csign.equals("*")) {
            captchaRes = ca * cb;
        }
        else {
            captchaRes = ca + cb;
        }
        //System.out.println("captchaRes : " + String.valueOf(captchaRes));

        canvas.setFont("italic bold 25px sans-serif");
        canvas.setFillStyle("red");
        canvas.fillText(genExpr,15,Math.round(0.5*capHeight) + 10,100);

        ResultTextField = new TextField();
        ResultTextField.setNullRepresentation("");
        ResultTextField.setInputPrompt("Введите результат арифметического выражения, расположенного слева от поля");
        ResultTextField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        ResultTextField.addStyleName("TopLabel");
        ResultTextField.setWidth("570px");
        HorizontalLayout ContentLayout = new HorizontalLayout(
                canvas
                ,ResultTextField
        );
        ContentLayout.setSpacing(true);
        ContentLayout.setSizeUndefined();
        this.addComponent(ContentLayout);
        this.setSizeUndefined();

    }

}
