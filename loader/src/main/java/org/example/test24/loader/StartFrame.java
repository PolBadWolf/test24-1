package org.example.test24.loader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class StartFrame extends JFrame {
    private MainClassCallBackStartFrame callBack = null;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
//
    private JButton buttonEnter;
    private JButton buttonTuning;
    private JButton buttonWork;
    private JTextField fieldPassword;
    private JTextField fieldUser;
    private JLabel jLabel1;
    private JLabel jLabel2;
//
    private boolean flCheckCommPort = false;
    private boolean flCheckSql = false;

    public static StartFrame main(MainClassCallBackStartFrame callBack) {
        final StartFrame[] frame = new StartFrame[1];
        frame[0] = null;
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    frame[0] = new StartFrame(callBack);
                }
            });
            new Thread( ()->frame[0].start()).start();
            Thread.sleep(10_000);
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
        }
        return frame[0];
    }

    private void start() {
        try {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    initComponents();
                    onTitleComponents();
                    setVisible(true);
                }
            });
            flCheckCommPort = callBack.checkCommPort();
            flCheckSql = callBack.checkSql();
            Thread.sleep(1_000);
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    offTitleComponents();
                    onInputComponents();
                    if (!flCheckCommPort) {
                        System.out.println("ошибка открытия comm port");
                    }
                    if (!flCheckSql) {
                        System.out.println("ошибка подключения к BD");
                    }
                }
            });
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
        }

    }

    private StartFrame(MainClassCallBackStartFrame callBack) {
        this.callBack = callBack;
        setLayout(null);
        addWindowListener(new WindowListener() {
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
    }

    private void initComponents() {
        setPreferredSize(new Dimension(640, 480));

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
        label1.setBounds(220, 130, 148, 66);
        label1.setVisible(false);

        label2.setFont(new java.awt.Font("Times New Roman", 0, 36)); // NOI18N
        label2.setText("для испытания");
        add(label2);
        label2.setBounds(180, 180, 227, 42);
        label2.setVisible(false);

        label3.setFont(new java.awt.Font("Times New Roman", 0, 36)); // NOI18N
        label3.setText("гидротолкателей");
        add(label3);
        label3.setBounds(170, 210, 258, 42);
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

        buttonTuning = getButtonTuning();
        add(buttonTuning);
        buttonTuning.setVisible(false);
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
        buttonWork.setEnabled(false);
        buttonTuning.setVisible(true);
        buttonTuning.setEnabled(false);
    }

    private void offInputComponents() {
        jLabel1.setVisible(false);
        jLabel2.setVisible(false);
        fieldUser.setVisible(false);
        fieldPassword.setVisible(false);
        buttonEnter.setVisible(false);
        buttonWork.setVisible(false);
        buttonTuning.setVisible(false);
    }

    private JButton getButtonEnter() {
        JButton button = new JButton();
        button.setFont(new java.awt.Font("Times New Roman", 0, 14));
        button.setText("Ввод");
        button.setBounds(340, 240, 80, 23);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonWork.setEnabled(false);
                buttonTuning.setEnabled(false);
                // "aUxPMjIzNjA\="
                boolean fl = false;
                if (flCheckSql) {

                }
                if (fl) {
                    if (flCheckCommPort && flCheckSql) {
                        buttonWork.setEnabled(true);
                    }
                }

                String pswd = new String(java.util.Base64.getEncoder().encode(fieldPassword.getText().getBytes()));
                boolean flUser = "Doc".equals(fieldUser.getText());
                boolean flPass = "aUxPMjIzNjA=".equals(pswd);
                if (flUser && flPass) {
                    buttonWork.setEnabled(false);
                    buttonTuning.setEnabled(true);
                }
            }
        });
        return button;
    }

    private JButton getButtonWork() {
        JButton button = new JButton();
        button.setFont(new java.awt.Font("Times New Roman", 0, 14));
        button.setText("работа");
        button.setBounds(340, 280, 80, 23);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAll();
                dispose();
                callBack.closeFrame();
            }
        });
        return button;
    }

    private JButton getButtonTuning() {
        JButton button = new JButton();
        button.setFont(new java.awt.Font("Times New Roman", 0, 14));
        button.setText("настройка");
        button.setBounds(320, 320, 100, 23);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TuningFrame tuningFrame;
                //tuningFrame = new TuningFrame(this);
                //tuningFrame.frameConfig(parameters);
            }
        });
        return button;
    }
}
