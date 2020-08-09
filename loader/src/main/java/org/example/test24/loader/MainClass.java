package org.example.test24.loader;

import org.example.bd.BdWork;
import org.example.test24.RS232.CommPort;
import org.example.test24.allinterface.Closer;
import org.example.test24.RS232.BAUD;
import org.example.test24.runner.Running;
import org.example.test24.screen.MainFrame;
import org.example.test24.screen.ScreenClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.util.Arrays;
import java.util.Properties;


public class MainClass {
    private ScreenClass mainFx = null;
    private Running runner = null;
    private CommPort commPort = null;
    private BdWork bdWork = null;

    public static void main(String[] args) {
        new MainClass().start(args);
    }

    private void start(String[] args) {
        String[] param = getConfig();
        String namePort = param[1];

        mainFx= new ScreenClass();
        runner = new Running();
        commPort = new CommPort();

        Closer.getCloser().init(() -> commPort.Close(), () -> runner.Close(), mainFx);

        frameConfig(param);

        int checkComm = commPort.Open((bytes, lenght) -> runner.reciveRsPush(bytes, lenght), namePort, BAUD.baud57600);
        if (checkComm != CommPort.INITCODE_OK) {
            errorCommMessage(checkComm, commPort);
            System.exit(0);
        }

        try {
            bdWork = new BdWork(param[0]);
            bdWork.getConnect();
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }


        (new Thread(mainFx)).start();
        while (MainFrame.mainFrame == null) {
            Thread.yield();
        }

        runner.init(bdWork, commPort, MainFrame.mainFrame);

        commPort.ReciveStart();
    }

    private void frameConfig(String[] parametrs) {
        JFrame frameStart = new JFrame("настройка");
        frameStart.setPreferredSize(new Dimension(640, 480));
        frameStart.setLayout(null);
        //
        JPanel panel1 = new JPanel();
        panel1.setBounds(10,10,200,200);
        panel1.setBorder(BorderFactory.createTitledBorder("выбор Comm порта"));
        panel1.setLayout(null);
        //
        Container container = frameStart.getContentPane();
        container.add(panel1);
        //
        JLabel labelPortLabel = new JLabel("текщий порт: ");
        labelPortLabel.setBounds(5,5,100,30);
        panel1.add(labelPortLabel);

        JLabel labelPort = new JLabel(parametrs[1]);
        int x= labelPortLabel.getGraphics().getFontMetrics().stringWidth(labelPortLabel.getText());
        labelPort.setBounds(x,5,100, 30);
        panel1.add(labelPort);
        //
        JTextField textField = new JTextField("textField");
        textField.setBounds(100,100,100,50);
        //textArea.setBounds(250, 100, 100, 50);
        //
        String[] stringListPorts = commPort.getListPortsName();
        Arrays.sort(stringListPorts);
        JComboBox<String> portList = new JComboBox<>(stringListPorts);
        portList.setBounds(70, 40, 100, 20);
        portList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String portName = (String) ((JComboBox) e.getSource()).getSelectedItem();
                int ch =  commPort.Open(null, portName, BAUD.baud57600);
                if (ch == CommPort.INITCODE_OK) {
                    labelPort.setText(portName);
                    commPort.Close();
                }
                System.out.println(labelPortLabel.getGraphics().getFontMetrics().stringWidth(labelPortLabel.getText()));
            }
        });
        panel1.add(portList);
        //
        //
        //container.add(labelPort);
        //container.add(portList);
        //container.add(textField);
        //
//        panel1.add(labelPortLabel);
//        panel1.add(labelPort);
        //
        frameStart.pack();
        frameStart.setVisible(true);
        frameStart.addWindowListener(new WindowListener() {
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

    private void errorCommMessage(int checkComm, CommPort commPort) {
        switch (checkComm) {
            case CommPort.INITCODE_ERROROPEN:
                System.out.println("ошибка открытия порта");
                break;
            case CommPort.INITCODE_NOTEXIST:
                System.out.println("указанный порт отсутствует");
                System.out.println("имеющиеся порты в системе:");
                for (String name : commPort.getListPortsName()) {
                    System.out.println(name);
                }
                break;
        }
    }

    private String[] getConfig() {
        Properties properties = new Properties();
        boolean flagReload = false;
        String[] strings = new String[2];

        try {
            properties.load(new FileReader("config.txt"));
            strings[0] = properties.getProperty("DataBase");
            strings[1] = properties.getProperty("CommPort");

            if (strings[0] == null || strings[1] == null)   flagReload = true;

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("файл config.txt не найден");
            flagReload = true;
        }

        if (flagReload) {
            strings[0] = "MY_SQL";
            strings[1] = "com2";
            try {
                properties.setProperty("DataBase", strings[0]);
                properties.setProperty("CommPort", strings[1]);
                properties.store(new FileWriter("config.txt"), "config.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return strings;
    }
}
