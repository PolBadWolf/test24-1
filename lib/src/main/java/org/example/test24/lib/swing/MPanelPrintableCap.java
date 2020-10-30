package org.example.test24.lib.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

public class MPanelPrintableCap extends JPanel implements Printable {
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
        if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
        Graphics2D g2d = (Graphics2D) graphics;
        RepaintManager repaintManager = RepaintManager.currentManager(this);
        repaintManager.setDoubleBufferingEnabled(false);
        doScale(g2d, pageFormat);
        repaintManager.setDoubleBufferingEnabled(true);
        return Printable.PAGE_EXISTS;
    }

    private void doScale(Graphics2D g2d, PageFormat pf) {
        AffineTransform oldAt = g2d.getTransform();
        try {
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            double scale = Scale.minScale(pf.getImageableWidth(), pf.getImageableHeight(), getWidth(), getHeight());
            g2d.scale(scale, scale);
            paintComponent(g2d);
            //paintChildren(g2d);
        } finally {
            g2d.setTransform(oldAt);
        }
    }
}
