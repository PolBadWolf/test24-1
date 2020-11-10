import ru.yandex.fixcolor.tests.spc.lib.plot2.Plot;
import ru.yandex.fixcolor.tests.spc.lib.swing.CreateComponents;
import ru.yandex.fixcolor.tests.spc.lib.swing.MPanel;

import javax.swing.*;
import java.awt.*;

public class GraphTest {
    public static void main(String[] args) {
        new GraphTest().start();
    }
    JFrame frame;
    MPanel panel;
    private void start() {
        frame = new JFrame("test plot2");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);
        frame.setPreferredSize(new Dimension(1200, 1024));
        panel = CreateComponents.getMPanel(
                null,
                null, //new Font("Times New Roman", Font.PLAIN, 12),
                "plots",
                100, 50, 1000, 800,
                true, true
        );
        frame.add(panel);
        Plot.Parameters plotParameters = new Plot.Parameters(1000, 800);
        frame.pack();
        frame.setVisible(true);
        // ---
        Plot plot = Plot.createSwing(plotParameters, panel);
        plot.clear();
        panel.repaint();
    }
}
