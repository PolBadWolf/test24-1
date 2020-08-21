package org.example.test24.loader;

import org.example.bd.DataBase;
import org.example.bd.SqlWork_interface;
import org.example.test24.allinterface.bd.UserClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StartFrame extends JFrame {
    private MainClassCallBackStartFrame callBack;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
//
    private JButton buttonEnter;
    private JButton buttonTuning;
    private JButton buttonWork;
    private JButton buttonSetPassword;
    private JTextField fieldPassword;
    private JComboBox<String> comboBoxUser;
    private JLabel jLabel1;
    private JLabel jLabel2;
//
    private boolean flCheckCommPort = false;
    private boolean flCheckSql = false;
    private UserClass[] listUsers = null;
    private UserClass user = null;

    public static StartFrame main(MainClassCallBackStartFrame callBack) {
        final StartFrame[] frame = new StartFrame[1];
        frame[0] = null;
        try {
            SwingUtilities.invokeAndWait(() -> frame[0] = new StartFrame(callBack));
            new Thread( ()->frame[0].start()).start();
            Thread.sleep(10_000);
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
        }
        return frame[0];
    }

    private void start() {
        try {
            SwingUtilities.invokeLater(() -> {
                initComponents();
                onTitleComponents();
                setResizable(false);
                setVisible(true);
            });
            // проверка Comm port
            flCheckCommPort = callBack.checkCommPort();
            flCheckSql = callBack.checkSqlFile();
            if (flCheckSql) {
                loadListUsers();
            }
            Thread.sleep(1_000);
            SwingUtilities.invokeAndWait(() -> {
                offTitleComponents();
                onInputComponents();
                if (!flCheckCommPort) {
                    System.out.println("ошибка открытия comm port");
                }
                if (!flCheckSql) {
                    System.out.println("ошибка подключения к BD");
                }
            });
            // --------
            TuningFrame tuningFrame;
            tuningFrame = callBack.getTuningFrame();
            tuningFrame.frameConfig(callBack.getParameters(), getStartFrameCallBackTuningFrame());
            // -------
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
        fieldPassword = new JPasswordField();
        buttonEnter = new JButton();
        buttonWork = new JButton();
        buttonTuning = new JButton();

        label1.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 57)); // NOI18N
        label1.setText("Стенд");
        add(label1);
        label1.setBounds(220, 130, 148, 66);
        label1.setVisible(false);

        label2.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 36)); // NOI18N
        label2.setText("для испытания");
        add(label2);
        label2.setBounds(180, 180, 227, 42);
        label2.setVisible(false);

        label3.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 36)); // NOI18N
        label3.setText("гидротолкателей");
        add(label3);
        label3.setBounds(170, 210, 258, 42);
        label3.setVisible(false);

        label4.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 11)); // NOI18N
        label4.setText("Гумеров М.Н.");
        add(label4);
        label4.setBounds(380, 400, 68, 20);
        label4.setVisible(false);

        label5.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 16)); // NOI18N
        label5.setText("ЦЗЛАМ ЛА");
        add(label5);
        label5.setBounds(460, 400, 81, 19);
        label5.setVisible(false);

        jLabel1.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 14)); // NOI18N
        jLabel1.setText("Пользователь : ");
        jLabel1.setBounds(210, 150, 90, 14);
        add(jLabel1);
        jLabel1.setVisible(false);

        jLabel2.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 14)); // NOI18N
        jLabel2.setText("Пароль :");
        jLabel2.setBounds(210, 200, 80, 14);
        add(jLabel2);
        jLabel2.setVisible(false);

        comboBoxUser = getComboBoxUser();
        add(comboBoxUser);
        comboBoxUser.setVisible(false);

        fieldPassword.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 14));
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

        buttonSetPassword = getButtonSetPassword();
        add(buttonSetPassword);
        buttonSetPassword.setVisible(false);

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
        comboBoxUser.setVisible(true);
        fieldPassword.setVisible(true);
        buttonEnter.setVisible(true);
        buttonWork.setVisible(true);
        buttonWork.setEnabled(false);
        buttonTuning.setVisible(true);
        buttonTuning.setEnabled(false);
        buttonSetPassword.setVisible(false);
    }
    private void offInputComponents() {
        jLabel1.setVisible(false);
        jLabel2.setVisible(false);
        comboBoxUser.setVisible(false);
        fieldPassword.setVisible(false);
        buttonEnter.setVisible(false);
        buttonWork.setVisible(false);
        buttonTuning.setVisible(false);
    }

    private JButton getButtonEnter() {
        JButton button = new JButton();
        button.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 14));
        button.setText("Ввод");
        button.setBounds(340, 240, 80, 23);
        button.addActionListener(e -> {
            boolean flUser;
            String pass;
            boolean flPass;
            buttonWork.setEnabled(false);
            buttonTuning.setEnabled(false);
            // "aUxPMjIzNjA\="
            boolean fl = false;
            if (flCheckSql) {
                user = null;
                for (UserClass user1 : listUsers) {
                    //flUser = user1.name.equals(fieldUser.getText());
                    flUser = user1.name.equals(comboBoxUser.getSelectedItem());
                    if (flUser) {
                        if (user1.password.equals(fieldPassword.getText())) {
                            user = user1;
                        }
                        break;
                    }
                }
            }
            if (user != null) {
                buttonSetPassword.setVisible(true);
                buttonSetPassword.setEnabled(true);
                fieldPassword.setText("");
                if (flCheckCommPort && flCheckSql) {
                    buttonWork.setEnabled(true);
                    fl = true;
                }
            }

            pass = new String(java.util.Base64.getEncoder().encode(fieldPassword.getText().getBytes()));
            flUser = "Doc".equals(comboBoxUser.getSelectedItem());
            flPass = "aUxPMjIzNjA=".equals(pass);
            if (flUser && flPass) {
                buttonWork.setEnabled(false);
                buttonTuning.setEnabled(true);
                fl = true;
            }
            if (!fl) {
                fieldPassword.setText("");
                buttonSetPassword.setVisible(false);
                JDialog dialog = new JDialog();
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setTitle("title");
                JLabel label = new JLabel("ошибка ввода пароля",  SwingConstants.CENTER);
                label.setFont(new Font("Times New Roman", Font.PLAIN, 28));
                int shir = 250;
                try {
                    shir = StartFrame.this.getGraphics().getFontMetrics().stringWidth(label.getText());
                    int sub = dialog.getWidth() - shir + 20;
                    label.setBounds(sub / 2, dialog.getHeight() /2, shir, 30);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
                dialog.setSize(shir * 3, 100);
                dialog.add(label);
                dialog.setVisible(true);
                new Thread(()->{
                    try {
                        Thread.sleep(5_000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    dialog.dispose();
                }).start();
            }
        });
        return button;
    }
    private JButton getButtonWork() {
        JButton button = new JButton();
        button.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 14));
        button.setText("работа");
        button.setBounds(340, 280, 80, 23);
        button.addActionListener(e -> {
            removeAll();
            dispose();
            callBack.closeFrame();
        });
        return button;
    }
    private JButton getButtonTuning() {
        JButton button = new JButton();
        button.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 14));
        button.setText("настройка");
        button.setBounds(320, 320, 100, 23);
        button.addActionListener(e -> {
            buttonEnter.setEnabled(false);
            buttonWork.setEnabled(false);
            buttonTuning.setEnabled(false);
            TuningFrame tuningFrame;
            tuningFrame = callBack.getTuningFrame();
            tuningFrame.frameConfig(callBack.getParameters(), getStartFrameCallBackTuningFrame());
        });
        return button;
    }
    private JButton getButtonSetPassword() {
        JButton button = new JButton();
        button.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 14));
        button.setText("новый пароль");
        button.setBounds(440, 198, 140, 23);
        button.addActionListener(e -> {
            try {
                String[] parameters = callBack.getParameters();
                SqlWork_interface bd = DataBase.init(parameters[0], callBack.getFilesNameSql());
                bd.updateUserPassword(user, fieldPassword.getText());
                loadListUsers();
                comboBoxUser.setSelectedItem(user.name);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            fieldPassword.setText("");
        });
        return button;
    }
    private JComboBox<String> getComboBoxUser() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setBounds(300, 150, 120, 20);
        comboBox.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 14));
        comboBox.setEditable(true);
        comboBox.addActionListener(e -> {

        });
        comboBox.addItemListener(e -> {
            fieldPassword.setText("");
            buttonEnter.setEnabled(true);
            buttonWork.setEnabled(false);
            buttonTuning.setEnabled(false);
            buttonSetPassword.setVisible(false);
        });
        return comboBox;
    }

    // callBack из TuningFrame
    private StartFrameCallBackTuningFrame getStartFrameCallBackTuningFrame() {
        return new StartFrameCallBackTuningFrame() {
            @Override
            public void messageCloseTuningFrame() {
                StartFrame startFrame = StartFrame.this;
                startFrame.fieldPassword.setText("");
                startFrame.buttonEnter.setEnabled(true);
                flCheckSql = callBack.checkSqlFile();
                try {
                    if (flCheckSql) {
                        StartFrame.this.loadListUsers();
                    }
                } catch (Exception e) {
                    System.out.println("StartFrame.StartFrameCallBackTuningFrame ошибка чтения списка пользователей: " + e.getMessage());
                }
            }
        };
    }

    // ===========================================================================
    //                        ===
    // загрузка пользователей
    private void loadListUsers() throws Exception {
        if (flCheckSql) {
            String[] parameters = callBack.getParameters();
            SqlWork_interface bd = DataBase.init(parameters[0], callBack.getFilesNameSql());
            listUsers = bd.getListUsers(true);
            comboBoxUser.removeAllItems();
            for (UserClass listUser : listUsers) {
                comboBoxUser.addItem(listUser.name);
            }
            try {
                comboBoxUser.setSelectedItem(listUsers[0].name);
            } catch (java.lang.Throwable e) {
                System.out.println("StartFrame.loadListUsers ошибка установки текущего пользователя: " + e.getMessage());
            }
        }
    }
}
