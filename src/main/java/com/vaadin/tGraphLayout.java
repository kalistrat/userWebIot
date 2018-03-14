package com.vaadin;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.hezamu.canvas.Canvas;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kalistrat on 25.01.2017.
 */
public class tGraphLayout extends VerticalLayout {

    Canvas Cnvs;
    //Label TitleStr;

    public tGraphLayout(List<tMark> iMarkXList
            ,List<tMark> iMarkYList
            ,List<tMark> iXYList
            ,String iSym
    //        ,Integer iYD
    ) {


        Cnvs = new Canvas();

        int dGraphWidth = 400;
        int dGraphHeight = 200;

        int Pad12 = 20;

        int Pad06 = 80;
        int Pad03 = 90;

        int xMarkPad = 20;
        int yMarkPad = GetNd(iMarkYList)*5+10;
        int Pad09 = 60 + yMarkPad;
        int SymPad = 5;

        int GraphWidth = dGraphWidth + Pad03 + Pad09;
        int GraphHeight = dGraphHeight + Pad12 + Pad06;


        Cnvs.setWidth(String.valueOf(GraphWidth)+"px");
        Cnvs.setHeight(String.valueOf(GraphHeight)+"px");

        Cnvs.beginPath();
        Cnvs.setLineWidth(1);
        Cnvs.setStrokeStyle("white");


        double YScale = (double) dGraphHeight/(iMarkYList.get(iMarkYList.size()-1).y-iMarkYList.get(0).y);
        double XScale = (double) dGraphWidth/(iMarkXList.get(iMarkXList.size()-1).x-iMarkXList.get(0).x);

        String pattern = "##0.000000";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);

        //Рисую вертикальные линии сетки

        for (tMark xMark : iMarkXList) {
            int xFrom = Pad09 +  (int) Math.round(xMark.x*XScale);
            int yFrom = (int) Math.round((Pad12 + dGraphHeight) - xMark.y*YScale);
            int xTo = xFrom;
            int yTo = Pad12;
            Cnvs.moveTo(xFrom, yFrom);
            Cnvs.lineTo(xTo, yTo);
        }

        //Рисую горизонтальные линии сетки

        for (tMark yMark : iMarkYList) {
            int xFrom = Pad09 + (int) Math.round(yMark.x*XScale);
            int yFrom = (int) Math.round((Pad12 + dGraphHeight) - yMark.y*YScale);
            int xTo = Pad09 + dGraphWidth;
            int yTo = yFrom;

            Cnvs.moveTo(xFrom, yFrom);
            Cnvs.lineTo(xTo, yTo);
        }

        Cnvs.stroke();
        Cnvs.closePath();

        if (iXYList.size() != 0) {

            Cnvs.beginPath();
            Cnvs.setLineWidth(3);
            Cnvs.setStrokeStyle("red");

            //перемещение курсора на начальную точку графика

            int xFrom = Pad09 + (int) Math.round(iXYList.get(0).x * XScale);
            int yFrom = (Pad12 + dGraphHeight) - (int) Math.round((iXYList.get(0).y - Integer.parseInt(iMarkYList.get(0).title)) * YScale);
            Cnvs.moveTo(xFrom, yFrom);

            //отрисовка графика

            for (int k = 0; k < iXYList.size() - 1; k++) {
                int xTo = Pad09 + (int) Math.round(iXYList.get(k + 1).x * XScale);
                int yTo = (Pad12 + dGraphHeight) - (int) Math.round((iXYList.get(k + 1).y - Integer.parseInt(iMarkYList.get(0).title)) * YScale);
                Cnvs.lineTo(xTo, yTo);
            }
            Cnvs.stroke();
            Cnvs.closePath();

        }


        //Простановка меток вдоль оси x

        for (tMark xMark : iMarkXList) {
            int xXMark = Pad09 + (int) Math.round(xMark.x*XScale);
            int yXMark = (int) Math.round((Pad12 + dGraphHeight) - xMark.y*YScale);
            Cnvs.translate(xXMark,yXMark);
            Cnvs.rotate(0.17*Math.PI);
            Cnvs.fillText(xMark.title,0,xMarkPad,100);
            Cnvs.rotate(-0.17*Math.PI);
            Cnvs.translate(-xXMark,-yXMark);

        }

        //Простановка меток вдоль оси y

        for (tMark yMark : iMarkYList) {
            int xXMark = Pad09 + (int) Math.round(yMark.x*XScale);
            int yXMark = (int) Math.round((Pad12 + dGraphHeight) - yMark.y*YScale);
            Cnvs.translate(xXMark,yXMark);
            Cnvs.fillText(yMark.title,-yMarkPad,0,100);
            Cnvs.translate(-xXMark,-yXMark);
        }

        int xXMark = SymPad ;
        int yXMark = (int) Math.round((Pad12 + 0.5*dGraphHeight));

        Cnvs.translate(xXMark,yXMark);
        Cnvs.fillText(iSym,0,0,100);
        //Cnvs.translate(-xXMark,-yXMark);

        Cnvs.restoreContext();
        this.addComponent(this.Cnvs);

    }

    public double closest(double of, List<Double> in) {
        double min = Double.MAX_VALUE;
        double closest = of;

        for (double v : in) {
            final double diff = Math.abs(v - of);

            if (diff < min) {
                min = diff;
                closest = v;
            }
        }

        return closest;
    }

    public double MaxAbsValList(List<Double> in) {
        List<Double> AbsList = new ArrayList<Double>();
        int k = 0;

        for (double v : in) {
            AbsList.add(k,Math.abs(v));
            k = k + 1;
        }

        return Collections.max(AbsList);
    }

    public int GetNd(List<tMark> tMs){
        int Nd = 1;

        for (tMark val : tMs){
        double MarkValue = Double.parseDouble(val.title);
        int Ns = (int) Math.ceil(Math.log10(Math.abs(MarkValue) + 0.5));
            if (Ns > Nd) {
                Nd = Ns;
            }
        }
        return Nd;
    }
}
