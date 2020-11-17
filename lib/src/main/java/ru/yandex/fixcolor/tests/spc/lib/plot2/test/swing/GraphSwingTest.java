package ru.yandex.fixcolor.tests.spc.lib.plot2.test.swing;

import ru.yandex.fixcolor.tests.spc.lib.plot2.Plot;
import ru.yandex.fixcolor.tests.spc.lib.plot2.PlotParent;
import ru.yandex.fixcolor.tests.spc.lib.swing.CreateComponents;
import ru.yandex.fixcolor.tests.spc.lib.swing.MPanel;

import javax.swing.*;
import java.awt.*;

public class GraphSwingTest {
    public static void main(String[] args) {
        new GraphSwingTest().start();
    }
    JFrame frame;
    MPanel panel;
    private void start() {
        frame = new JFrame("test plot2");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        frame.pack();
        frame.setVisible(true);
        // ---
        plotParameters.trend1_zeroY_min = 0;
        plotParameters.trend1_zeroY_max = 52;
//        plotParameters.trend2_zeroY_min = -0;
//        plotParameters.trend2_zeroY_max = 950;
        plotParameters.zeroX_zoom = 1;
        Plot plot = Plot.createSwing(plotParameters, panel);
        plot.clear();
        panel.repaint();
        new Thread(new Cicl(plot)).start();
    }
    private class Cicl implements Runnable {
        public Cicl(Plot plot) {
            this.plot = plot;
        }
        Plot plot;
        int curX = 0;
        int tr1 = 0;
        int tr1_f = 150;
        boolean pl_tr1 = true;
        int tr2 = 0;
        int tr2_f = 777;
        boolean pl_tr2 = true;
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1);
                    plot.newData(curX);
                    plot.addTrend(tr1);
                    plot.addTrend(tr2);
                    plot.setData();
                    if (pl_tr1) {
                        if (tr1 >= tr1_f) pl_tr1 = false;
                    } else {
                        if (tr1 <= 0) pl_tr1 = true;
                    }
                    if (pl_tr2) {
                        if (tr2 >= tr2_f) pl_tr2 = false;
                    } else {
                        if (tr2 <= 0) pl_tr2 = true;
                    }
                    if (pl_tr1) tr1++;
                    else tr1--;
                    if (pl_tr2) tr2++;
                    else tr2--;
                    if (curX % 10 == 0) plot.paint();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            } while (++curX < 7_000);
        }
    }
}
