package org.example.test24.loader;

import org.example.test24.RS232.BAUD;
import org.example.test24.RS232.CommPort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;

class StartFrame {
    private MainClass parentSuper = null;
    private JFrame frameStart = null;
    private JLabel labelPortLabel = null;
    public JLabel labelPortCurrent = null;
    public JComboBox<String> comboBoxCommPort = null;

    public StartFrame(MainClass parentSuper) {
        this.parentSuper = parentSuper;
    }

    void frameConfig(String[] parametrs) {
        frameStart = getFrameStart("настройка", new Dimension(640, 480));
        Container container = frameStart.getContentPane();
        JPanel panelCommPort = getPanelTitle("выбор Comm порта", new Rectangle(10, 10, 200, 200));
        container.add(panelCommPort);
        //
        labelPortLabel = getLabel("текщий порт: ", new Rectangle(5, 15, 100, 30));
        panelCommPort.add(labelPortLabel);
        frameStart.pack();
        //
        {
            int x = labelPortLabel.getGraphics().getFontMetrics().stringWidth(labelPortLabel.getText());
            x += x / labelPortLabel.getText().length();
            labelPortCurrent = getLabel(parametrs[1].toUpperCase(), new Rectangle(x, 15, 100, 30));
        }
        panelCommPort.add(labelPortCurrent);
        //
        comboBoxCommPort = getComboBoxCommPort(new Rectangle(26, 50, 100, 20), parametrs[1].toUpperCase());
        panelCommPort.add(comboBoxCommPort);
        //
        frameStart.pack();
        frameStart.setVisible(true);
        try {
            while (true) {
                Thread.sleep(1_000);
            }
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
        }
        frameStart.dispose();
        System.exit(1);
    }

    private JFrame getFrameStart(String title, Dimension size) {
        JFrame frame = new JFrame(title);
        frame.setSize(size);
        frame.setPreferredSize(size);
        frame.setLayout(null);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().removeAll();
                System.exit(2);
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        return frame;
    }
    private JPanel getPanelTitle(String title, Rectangle positionSize) {
        JPanel panel = new JPanel();
        panel.setBounds(positionSize);
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setLayout(null);
        return panel;
    }
    private JLabel getLabel(String text, Rectangle positionSize) {
        JLabel label = new JLabel(text);
        label.setBounds(positionSize);
        return label;
    }
    private JComboBox getComboBoxCommPort(Rectangle positionSize, String itemDefault) {
        String[] listCommPortName = parentSuper.commPort.getListPortsName();
        // sort
        Arrays.sort(listCommPortName);
        JComboBox<String> comboBox = new JComboBox<>(listCommPortName);
        comboBox.setSelectedItem(itemDefault);
        comboBox.setBounds(positionSize);
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String portName = (String) ((JComboBox) e.getSource()).getSelectedItem();
                int ch =  parentSuper.commPort.Open(null, portName, BAUD.baud57600);
                if (ch == CommPort.INITCODE_OK) {
                    labelPortCurrent.setText(portName);
                    parentSuper.commPort.Close();
                } else {

                }

            }
        });
        return comboBox;
    }


}
