package ru.yandex.fixcolor.tests.spc.lib.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;

public class MySwingUtil {
    public interface CallBack {
        void back();
    }
    public static void showMessage(Component parent, String title, String text, int timeout) {
        showMessage(parent, title, text, timeout, null);
    }
    public static void showMessage(Component parent, String title, String text, int timeout, CallBack callBack) {
        JDialog dialog;
        Window window = (Window) parent;
        JOptionPane pane = new JOptionPane(
                text,
                JOptionPane.ERROR_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null, null, null
        );
        pane.setInitialValue(null);
        dialog = new JDialog( (Frame) window,
                title,
                false);
        dialog.setComponentOrientation(pane.getComponentOrientation());

        final PropertyChangeListener listener = event -> {
            if (dialog.isVisible() && event.getSource() == pane &&
                    (event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) &&
                    event.getNewValue() != null &&
                    event.getNewValue() != JOptionPane.UNINITIALIZED_VALUE) {
                dialog.setVisible(false);
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
            int n = 1 + (timeout - fistDelay) / kvant;
            try {
                Thread.sleep(fistDelay);
                do {
                    Thread.sleep(kvant);
                    n--;
                } while (dialog.isVisible() && n > 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (callBack != null) {
                    callBack.back();
                }
                dialog.dispose();
            }
        }, "popup").start();
    }


}
