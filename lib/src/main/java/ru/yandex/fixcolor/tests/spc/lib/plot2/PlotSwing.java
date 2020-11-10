package ru.yandex.fixcolor.tests.spc.lib.plot2;

import ru.yandex.fixcolor.tests.spc.lib.swing.MPanel;

import java.awt.*;
import java.awt.image.BufferedImage;

class PlotSwing extends PlotParent implements PlotParent.TrendCallBack {
    private MPanel panel;
    public PlotSwing(Plot.Parameters parameters, MPanel panel) {
        super(parameters, panel.getWidth(), panel.getHeight());
        this.panel = panel;
        width = panel.getWidth();
        height = panel.getHeight();
        panel.image = new BufferedImage((int) width, (int) height,BufferedImage.TYPE_INT_ARGB);
        // тренд1
        trends[0] = new Trend(this);
        // тренд2
        trends[1] = new Trend(this);
        // sets
        setParametersTrends(parameters);
    }

    @Override
    public void clear() {
        Graphics2D g2d = (Graphics2D) panel.image.getGraphics();
        // заполнение полей
        g2d.setColor(fieldBackColor);
//        // top
        g2d.fillRect((int) fieldSizeLeft, (int) 0, (int) windowWidth, (int) fieldSizeTop);
//        // left
        g2d.fillRect((int) 0, (int) 0, (int) fieldSizeLeft, (int) height);
//        // right
        g2d.fillRect((int) (width - fieldSizeRight), (int) 0, (int) fieldSizeRight, (int) height);
//        // bottom
        g2d.fillRect((int) fieldSizeLeft, (int) (height - fieldSizeBottom), (int) windowWidth, (int) fieldSizeBottom);
        // окно
        g2d.setColor(windowBackColor);
        g2d.fillRect((int) (fieldSizeLeft), (int) (fieldSizeTop), (int) (windowWidth), (int) (windowHeight));
        // рамка
        g2d.setColor(fieldFrameColor);
        g2d.setStroke(new BasicStroke((float) fieldFrameWidth));
        g2d.drawRect((int) fieldSizeLeft, (int) fieldSizeTop, (int) windowWidth, (int) windowHeight);
        g2d.dispose();
    }

    @Override
    public void ll(TrendUnit[] units) {

    }
}
