package ru.yandex.fixcolor.tests.spc.lib.plot2.test.swing;

import ru.yandex.fixcolor.tests.spc.lib.plot2.Plot;
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
        plotParameters.trend1_zeroY_min = -6;
        plotParameters.trend1_zeroY_max = 52;
//        plotParameters.trend2_zeroY_min = -0;
//        plotParameters.trend2_zeroY_max = 950;
        Plot plot = Plot.createSwing(plotParameters, panel);
        plot.clear();
        panel.repaint();
    }
}
