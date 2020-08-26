package org.example.lib;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MySwingUtil {
    public static void outFlyMessage(Component parent, String title, String text, int timeout) {
        JDialog dialog;
        Window window = (Window) parent;
        JOptionPane pane = new JOptionPane(
                text,
                JOptionPane.ERROR_MESSAGE,
                -1,
                null, null, null
        );
        pane.setInitialValue(null);
        dialog = new JDialog( (Frame) window,
                "title",
                false);
        dialog.setComponentOrientation(pane.getComponentOrientation());

        final PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (dialog.isVisible() && event.getSource() == pane &&
                        (event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) &&
                        event.getNewValue() != null &&
                        event.getNewValue() != JOptionPane.UNINITIALIZED_VALUE) {
                    dialog.setVisible(false);
                }
            }
        };
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                pane.removePropertyChangeListener(listener);
                dialog.getContentPane().removeAll();
            }
        });
        pane.addPropertyChangeListener(listener);

        Container contentPane = dialog.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(pane, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(pane);
        pane.selectInitialValue();
        dialog.setVisible(true);
        new Thread(()->{
            final int kvant = 50;
            final int fistDelay = 500;
            final int allDelay = timeout;
            int n = 1 + (allDelay - fistDelay) / kvant;
            try {
                Thread.sleep(fistDelay);
                do {
                    Thread.sleep(kvant);
                    n--;
                } while (dialog.isVisible() && n > 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                dialog.dispose();
            }
        }, "popup").start();
    }


}
