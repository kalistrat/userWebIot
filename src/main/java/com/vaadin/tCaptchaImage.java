package com.vaadin;


import com.vaadin.server.StreamResource;
import org.vaadin.hezamu.canvas.*;
import org.vaadin.hezamu.canvas.Canvas;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * Created by kalistrat on 15.06.2017.
 */
public class tCaptchaImage implements StreamResource.StreamSource {
    ByteArrayOutputStream imagebuffer = null;
    int reloads = 0;
    int captchaRes;

    // This method generates the stream contents
    public InputStream getStream () {
        // Create an image
        int capWidth = 120;
        int capHeight = 50;

        BufferedImage image = new BufferedImage (capWidth, capHeight,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D drawable = image.createGraphics();


        drawable.setStroke(new BasicStroke(5));
        drawable.setColor(Color.RED);
        drawable.fillRect(0, 0, capWidth, capHeight);
        drawable.setColor(Color.DARK_GRAY);
        drawable.fillRect(3, 3, capWidth-2*3, capHeight-2*3);
        drawable.setColor(Color.RED);
        drawable.setFont(new Font("Montserrat",Font.PLAIN, 20));

        int ca = tUsefulFuctions.genRandInt(1,99);
        int cb = tUsefulFuctions.genRandInt(1,99);
        String csign = tUsefulFuctions.genSign();

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
        String genExpr = String.valueOf(ca) + "  " + csign + "  " + String.valueOf(cb);

        drawable.drawString(genExpr,20,Math.round(0.5*capHeight) + 10);
        //drawable.drawRect(0, 0, capWidth, capHeight);
//        drawable.setColor(Color.BLACK);
//        drawable.drawOval(50, 50, 300, 300);

        // Draw something dynamic
//        drawable.setFont(new Font("Montserrat",
//                Font.PLAIN, 48));
//        drawable.drawString("Reloads=" + reloads, 75, 216);
//        reloads++;
//        drawable.setColor(new Color(0, 165, 235));
//        int x= (int) (200-10 + 150*Math.sin(reloads * 0.3));
//        int y= (int) (200-10 + 150*Math.cos(reloads * 0.3));
//        drawable.fillOval(x, y, 20, 20);

        try {
            // Write the image to a buffer
            imagebuffer = new ByteArrayOutputStream();
            ImageIO.write(image, "png", imagebuffer);

            // Return a stream from the buffer
            return new ByteArrayInputStream(
                    imagebuffer.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }

}
