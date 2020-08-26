package org.example.test24.loader;

import org.example.bd.DataBase;
import org.example.lib.MySwingUtil;
import org.example.test24.allinterface.bd.UserClass;
import sun.awt.AppContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class StartFrame extends JFrame {
    private CallBack callBack;
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

    public interface CallBack {
        // проверка Comm Port
        boolean checkCommPort();
        // подключение к БД и структуры БД (параметры из файла конфигурации)
        boolean checkSqlFile();
        void closeFrame();
        // ---------------
        TuningFrame getTuningFrame();
        String[] getParameters();
        String[] getFilesNameSql();
        String getFileNameSql(String typeBd) throws Exception;
    }

    public static StartFrame main(CallBack callBack) {
        final StartFrame[] frame = new StartFrame[1];
        frame[0] = null;
        try {
            SwingUtilities.invokeAndWait(() -> frame[0] = new StartFrame(callBack));
            new Thread( ()->frame[0].start()).start();
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
            /*TuningFrame tuningFrame;
            tuningFrame = callBack.getTuningFrame();
            tuningFrame.frameConfig(callBack.getParameters(), new TuningFrameCallBack());*/
            // -------
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
        }

    }

    private StartFrame(CallBack callBack) {
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
        jLabel1.setBounds(100, 230, 90, 14);
        add(jLabel1);
        jLabel1.setVisible(false);

        jLabel2.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 14)); // NOI18N
        jLabel2.setText("Пароль :");
        jLabel2.setBounds(100, 280, 90, 14);
        add(jLabel2);
        jLabel2.setVisible(false);

        comboBoxUser = getComboBoxUser("Times New Roman", Font.PLAIN, 14, 190, 230, 350, 20);
        add(comboBoxUser);
        comboBoxUser.setVisible(false);

        fieldPassword = getFieldPassword("Times New Roman", Font.PLAIN, 14, 190, 280, 120, 20);
        add(fieldPassword);
        fieldPassword.setVisible(false);

        buttonEnter = getButtonEnter("проверка", "Times New Roman", Font.PLAIN, 14, 322, 280, 90, 23);
        add(buttonEnter);
        buttonEnter.setVisible(false);

        buttonWork = getButtonWork("работа", "Times New Roman", Font.PLAIN, 14, 322, 316, 90, 23);
        add(buttonWork);
        buttonWork.setVisible(false);

        buttonTuning = getButtonTuning("настройка", "Times New Roman", Font.PLAIN, 14, 424, 316, 116, 23);
        add(buttonTuning);
        buttonTuning.setVisible(false);

        buttonSetPassword = getButtonSetPassword("новый пароль", "Times New Roman", Font.PLAIN, 14, 424, 280, 116, 23);
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
        label1.setBounds(250, 40, 148, 66);
        label2.setBounds(210, 90, 227, 42);
        label3.setBounds(220, 120, 258, 42);
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

    private JTextField getFieldPassword(String fontName, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JTextField textField = new JPasswordField();
        textField.setFont(new Font(fontName, fontStyle, fontSize));
        textField.setBounds(x, y, width, height);
        textField.addActionListener(e -> callEnter());
        return textField;
    }
    private JButton getButtonEnter(String text, String fontName, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JButton button = new JButton();
        button.setFont(new java.awt.Font(fontName, fontStyle, fontSize));
        button.setText(text);
        button.setBounds(x, y, width, height);
        button.addActionListener(e ->callEnter());
        return button;
    }
    private JButton getButtonWork(String text, String fontName, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JButton button = new JButton();
        button.setFont(new java.awt.Font(fontName, fontStyle, fontSize));
        button.setText(text);
        button.setBounds(x, y, width, height);
        button.addActionListener(e -> {
            removeAll();
            dispose();
            callBack.closeFrame();
        });
        return button;
    }
    private JButton getButtonTuning(String text, String fontName, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JButton button = new JButton();
        button.setFont(new java.awt.Font(fontName, fontStyle, fontSize));
        button.setText(text);
        button.setBounds(x, y, width, height);
        button.addActionListener(e -> {
            buttonEnter.setEnabled(false);
            buttonWork.setEnabled(false);
            buttonTuning.setEnabled(false);
            fieldPassword.setEnabled(false);
            TuningFrame tuningFrame;
            tuningFrame = callBack.getTuningFrame();
            tuningFrame.frameConfig(callBack.getParameters(), new TuningFrameCallBack());
        });
        return button;
    }
    private JButton getButtonSetPassword(String text, String fontName, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JButton button = new JButton();
        button.setFont(new java.awt.Font(fontName, fontStyle, fontSize));
        button.setText(text);
        button.setBounds(x, y, width, height);
        button.addActionListener(e -> {
            try {
                String[] parameters = callBack.getParameters();
                DataBase bd = DataBase.init(parameters[0], callBack.getFilesNameSql());
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
    private JComboBox<String> getComboBoxUser(String fontName, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setFont(new java.awt.Font(fontName, fontStyle, fontSize));
        comboBox.setBounds(x, y, width, height);
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



    // обработка ввод
    private void callEnter() {
        boolean flUser;
        String pass;
        boolean flPass;
        buttonWork.setEnabled(false);
        buttonTuning.setEnabled(false);
        boolean fl = false;
        if (flCheckSql) {
            user = null;
            for (UserClass user1 : listUsers) {
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
            fl = true;
            buttonSetPassword.setVisible(true);
            buttonSetPassword.setEnabled(true);
            fieldPassword.setText("");
            if (!flCheckCommPort) {
                MySwingUtil.outFlyMessage(this, "Comm Port","Ошибка подключения к ком порту", 5_000);
                return;
            } else {
                if (!flCheckSql) {

                } else {
                    buttonWork.setEnabled(true);
                }
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
            fieldPassword.setEnabled(false);
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
                fieldPassword.setEnabled(true);
                dialog.dispose();
            }).start();
        }
    }

    // callBack из TuningFrame
    private class TuningFrameCallBack implements TuningFrame.CallBackToStartFrame {
        @Override
        public void messageCloseTuningFrame() {
            StartFrame startFrame = StartFrame.this;
            startFrame.fieldPassword.setText("");
            startFrame.fieldPassword.setEnabled(true);
            startFrame.buttonEnter.setEnabled(true);
            flCheckSql = callBack.checkSqlFile();
            flCheckCommPort = callBack.checkCommPort();
            try {
                if (flCheckSql) {
                    StartFrame.this.loadListUsers();
                }
            } catch (Exception e) {
                System.out.println("StartFrame.StartFrameCallBackTuningFrame ошибка чтения списка пользователей: " + e.getMessage());
            }
        }
    }

    // ===========================================================================
    //                        ===
    // загрузка пользователей
    private void loadListUsers() throws Exception {
        if (flCheckSql) {
            String[] parameters = callBack.getParameters();
            DataBase bd = DataBase.init(parameters[0], callBack.getFilesNameSql());
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
