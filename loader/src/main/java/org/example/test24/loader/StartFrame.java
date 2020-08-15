package org.example.test24.loader;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class StartFrame extends JFrame {
    private MainClassCallBack callBack = null;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JPanel titlePanel;

    public static StartFrame main(MainClassCallBack callBack) {
        final StartFrame[] frame = new StartFrame[1];
        frame[0] = null;
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    frame[0] = new StartFrame(callBack);
                    frame[0].setVisible(true);
                    frame[0].start();
                }
            });
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
        }
        return frame[0];
    }

    private StartFrame(MainClassCallBack callBack) {
        this.callBack = callBack;
        initComponents();
    }

    private void initComponents() {
        titlePanel = new JPanel();
        label1 = new javax.swing.JLabel();
        label2 = new javax.swing.JLabel();
        label3 = new javax.swing.JLabel();
        label4 = new javax.swing.JLabel();
        label5 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(640, 480));

        titlePanel.setBackground(new java.awt.Color(250, 250, 255));
        //titlePanel.setPreferredSize(preferredSize());
        titlePanel.setLayout(null);

        label1.setFont(new java.awt.Font("Times New Roman", 0, 57)); // NOI18N
        label1.setText("Стенд");
        titlePanel.add(label1);
        label1.setBounds(200, 130, 148, 66);

        label2.setFont(new java.awt.Font("Times New Roman", 0, 36)); // NOI18N
        label2.setText("для испытания");
        titlePanel.add(label2);
        label2.setBounds(160, 180, 227, 42);

        label3.setFont(new java.awt.Font("Times New Roman", 0, 36)); // NOI18N
        label3.setText("гидротолкателей");
        titlePanel.add(label3);
        label3.setBounds(150, 210, 258, 42);

        label4.setFont(new java.awt.Font("Times New Roman", 0, 11)); // NOI18N
        label4.setText("Гумеров М.Н.");
        titlePanel.add(label4);
        label4.setBounds(380, 400, 68, 20);

        label5.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        label5.setText("ЦЗЛАМ ЛА");
        titlePanel.add(label5);
        label5.setBounds(460, 400, 81, 19);

        add(titlePanel);
        pack();
    }

    private void start() {

    }
}
