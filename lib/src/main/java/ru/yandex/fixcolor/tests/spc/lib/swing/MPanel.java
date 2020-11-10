package ru.yandex.fixcolor.tests.spc.lib.swing;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MPanel extends MPanelPrintableCap {
    public BufferedImage image = null;

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        if (image == null) return;
        Graphics2D g2 = (Graphics2D) g;
        g2.drawRenderedImage(image, null);
    }

}
