package org.example.test24.loader.dialog;

import org.example.test24.bd.BaseData;
import org.example.test24.lib.swing.CreateComponents;

import javax.swing.*;

public class ViewArchive {
    private BaseData conn;
    public ViewArchive(BaseData conn) {
        this.conn = conn;
        new Thread(this::start, "thread view archive").start();
    }

    private JFrame frame;
    private void start() {
        initComponents();
    }
    private void initComponents() {
        frame = CreateComponents.getFrame("View Archive", 1024, 800, false, null, null);
        frame.setVisible(true);
    }
}
