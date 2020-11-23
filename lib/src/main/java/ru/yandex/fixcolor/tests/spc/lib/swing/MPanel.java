package ru.yandex.fixcolor.tests.spc.lib.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class MPanel extends MPanelPrintableCap {
    public BufferedImage image = null;
    public double scale_img = 1.0;

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (image == null) return;
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform oldAt = g2.getTransform();
        try {
            double scale_imgI = 1 / scale_img;
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.scale(scale_imgI, scale_imgI);
            g2.drawRenderedImage(image, null);
        } finally {
            g2.setTransform(oldAt);
        }
    }

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
    }

}
