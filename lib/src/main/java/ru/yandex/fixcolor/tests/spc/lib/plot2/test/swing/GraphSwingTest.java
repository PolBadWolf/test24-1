package ru.yandex.fixcolor.tests.spc.lib.plot2.test.swing;

import ru.yandex.fixcolor.tests.spc.lib.plot2.Plot;
import ru.yandex.fixcolor.tests.spc.lib.plot2.test.CycleTest;
import ru.yandex.fixcolor.tests.spc.lib.swing.CreateComponents;
import ru.yandex.fixcolor.tests.spc.lib.swing.MPanel;
import ru.yandex.fixcolor.tests.spc.lib.swing.Scale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GraphSwingTest {
    public static void main(String[] args) {
        new GraphSwingTest().start();
    }
    JFrame frame;
    MPanel panel;
    Plot plot;
    Thread threadCycle;
    CycleTest cycleTest = null;
    private void start() {
        frame = new JFrame("test plot2 swing");
        frame.setResizable(false);
        //frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);
        frame.setPreferredSize(new Dimension(1200, 700));
        panel = CreateComponents.getMPanel(
                null,
                null, //new Font("Times New Roman", Font.PLAIN, 12),
                "plots",
                100, 50, 1000, 550,
                true, true
        );
        frame.add(panel);
        Plot.Parameters plotParameters = new Plot.Parameters();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cycleTest.flOnWork = false;
                try {
                    while (threadCycle.isAlive()) {
                        Thread.yield();
                        Thread.sleep(1);
                    }
                } catch (InterruptedException interruptedException) { }
                new Thread(()->{
                    try {
                        plot.closeApp();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }).start();
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                super.windowClosing(e);
            }
        });
        frame.pack();
        frame.setVisible(true);
        // ---
        plotParameters.scale_img = Scale.scaleUp;
        plotParameters.trend1_zeroY_min = 0;
        plotParameters.trend1_zeroY_max = 120;
        plotParameters.trend2_zeroY_min = 0;
        plotParameters.trend2_zeroY_max = 5_000;
        plotParameters.trend2_AutoZoomY = Plot.ZOOM_Y_OFF;
        plotParameters.scaleZero_maxX = 5_000;
        plotParameters.scaleZero_zoomX = Plot.ZOOM_X_SHIFT;
        plot = Plot.createSwing(plotParameters, panel);
        plot.clearScreen();
        plot.reFresh();
        //panel.repaint();
        cycleTest = new CycleTest(plot);
        threadCycle = new Thread(cycleTest);
        threadCycle.start();
    }
}
