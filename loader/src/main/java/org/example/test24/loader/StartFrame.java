package org.example.test24.loader;

import javax.swing.*;

public class StartFrame extends JFrame {
    private MainClassCallBack callBack = null;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
//
    private JButton buttonEnter;
    private JButton buttonTuning;
    private JButton buttonWork;
    private JPasswordField fieldPassword;
    private JTextField fieldUser;
    private JLabel jLabel1;
    private JLabel jLabel2;
//

    public static StartFrame main(MainClassCallBack callBack) {
        final StartFrame[] frame = new StartFrame[1];
        frame[0] = null;
        try {
//            SwingUtilities.invokeAndWait(new Runnable() {
//                @Override
//                public void run() {
                    frame[0] = new StartFrame(callBack);
                    frame[0].setVisible(true);
//                }
//            });
            frame[0].start();
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
        }
        return frame[0];
    }

    private StartFrame(MainClassCallBack callBack) {
        this.callBack = callBack;
        setLayout(null);
    }

    private void initComponents() {
        label1 = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        label4 = new JLabel();
        label5 = new JLabel();

        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        fieldUser = new JTextField();
        fieldPassword = new JPasswordField();
        buttonEnter = new JButton();
        buttonWork = new JButton();
        buttonTuning = new JButton();

        label1.setFont(new java.awt.Font("Times New Roman", 0, 57)); // NOI18N
        label1.setText("Стенд");
        add(label1);
        label1.setBounds(200, 130, 148, 66);
        label1.setVisible(false);

        label2.setFont(new java.awt.Font("Times New Roman", 0, 36)); // NOI18N
        label2.setText("для испытания");
        add(label2);
        label2.setBounds(160, 180, 227, 42);
        label2.setVisible(false);

        label3.setFont(new java.awt.Font("Times New Roman", 0, 36)); // NOI18N
        label3.setText("гидротолкателей");
        add(label3);
        label3.setBounds(150, 210, 258, 42);
        label3.setVisible(false);

        label4.setFont(new java.awt.Font("Times New Roman", 0, 11)); // NOI18N
        label4.setText("Гумеров М.Н.");
        add(label4);
        label4.setBounds(380, 400, 68, 20);
        label4.setVisible(false);

        label5.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        label5.setText("ЦЗЛАМ ЛА");
        add(label5);
        label5.setBounds(460, 400, 81, 19);
        label5.setVisible(false);

        jLabel1.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel1.setText("Пользователь : ");
        jLabel1.setBounds(210, 150, 90, 14);
        add(jLabel1);
        jLabel1.setVisible(false);

        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel2.setText("Пароль :");
        jLabel2.setBounds(210, 200, 80, 14);
        add(jLabel2);
        jLabel2.setVisible(false);

        fieldUser.setBounds(300, 150, 120, 20);
        fieldUser.setFont(new java.awt.Font("Times New Roman", 0, 14));
        add(fieldUser);
        fieldUser.setVisible(false);

        fieldPassword.setFont(new java.awt.Font("Times New Roman", 0, 14));
        fieldPassword.setBounds(300, 200, 120, 20);
        add(fieldPassword);
        fieldPassword.setVisible(false);

        buttonEnter = getButtonEnter();
        add(buttonEnter);
        buttonEnter.setVisible(false);

        buttonWork = getButtonWork();
        add(buttonWork);
        buttonWork.setVisible(false);

/*
        buttonTuning.setFont(new java.awt.Font("Times New Roman", 0, 14));
        buttonTuning.setText("настройка");
        buttonTuning.setBounds(320, 320, 100, 23);
        buttonTuning.setEnabled(false);
        add(buttonTuning);
        buttonTuning.setVisible(false);
*/
        pack();
    }

    private void onTitleComponents() {
        label1.setVisible(true);
        label2.setVisible(true);
        label3.setVisible(true);
        label4.setVisible(true);
        label5.setVisible(true);
    }

    private void offTitleComponents() {
        label1.setVisible(false);
        label2.setVisible(false);
        label3.setVisible(false);
        label4.setVisible(false);
        label5.setVisible(false);
    }

    private void onInputComponents() {
        jLabel1.setVisible(true);
        jLabel2.setVisible(true);
        fieldUser.setVisible(true);
        fieldPassword.setVisible(true);
        buttonEnter.setVisible(true);
        buttonWork.setVisible(true);
    }

    private void offInputComponents() {
        jLabel1.setVisible(false);
        jLabel2.setVisible(false);
        fieldUser.setVisible(false);
        fieldPassword.setVisible(false);
        buttonEnter.setVisible(false);
        buttonWork.setVisible(false);
    }

    private void start() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    setPreferredSize(new java.awt.Dimension(640, 480));
                    initComponents();
                    onTitleComponents();
                }
            });
            Thread.sleep(10_000);
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    offTitleComponents();
                    onInputComponents();
                }
            });
            Thread.sleep(10_000);
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
        }
        //titlePanel.setVisible(false);

        /*boolean flCommPort = false;
        boolean flSqlServ = false;
        boolean flSqlStruct = false;
        flCommPort = callBack.checkCommPort();
        //flSqlServ;*/
    }

    private JButton getButtonEnter() {
        JButton button = new JButton();
        button.setFont(new java.awt.Font("Times New Roman", 0, 14));
        button.setText("Ввод");
        button.setBounds(340, 240, 80, 23);
        return button;
    }

    private JButton getButtonWork() {
        JButton button = new JButton();
        button.setFont(new java.awt.Font("Times New Roman", 0, 14));
        button.setText("работа");
        button.setBounds(340, 280, 80, 23);
        button.setEnabled(false);
        return button;
    }

    private JButton getButtonTuning() {
        JButton button = new JButton();
        button.setFont(new java.awt.Font("Times New Roman", 0, 14));
        button.setText("настройка");
        button.setBounds(320, 320, 100, 23);
        button.setEnabled(false);
        return button;
    }
}
