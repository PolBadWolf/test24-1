package org.example.test24.loader;

import org.example.test24.bd.BaseData;
import org.example.lib.MySwingUtil;
import org.example.test24.bd.ParametersSql;
import org.example.test24.bd.UserClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Comparator;

public class StartFrame extends JFrame {
    private FrameCallBack callBack;
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
    private JComboBox<UserClass> comboBoxUser;
    private JLabel jLabel1;
    private JLabel jLabel2;
//
    private boolean flCheckCommPort = false;
    private boolean flCheckSql = false;
    private UserClass[] listUsers = null;
    private UserClass user = null;
    //
    private ParametersSql parametersSql;
    private BaseData.TypeBaseData typeBaseData;


    public static StartFrame main(FrameCallBack callBack) {
        final StartFrame[] frame = new StartFrame[1];
        frame[0] = null;
        try {
            SwingUtilities.invokeAndWait(() -> {
                frame[0] = new StartFrame(callBack);
            });
            new Thread( ()-> {
                frame[0].start();
            }).start();
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
        }
        return frame[0];
    }

    private void start() {
        boolean res;
        // загрузка компонентов и вывод загаловка
        SwingUtilities.invokeLater(() -> {
            initComponents();
            onTitleComponents();
            setResizable(false);
            setVisible(true);
        });
        // начальная загрузка параметров соединения с БД
        res =  beginInitParametersSql();
        if (res) {
            // начальная инициация соединения c БД и получение списка пользователей
            res = beginInitConnectBdGetListUsers();
        }
        // задержка на пока начального экрана
        try {
            Thread.sleep(2_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // чтение порта из конфига
        String portName = callBack.getCommPortNameFromConfig();
        // проверка Comm port
        flCheckCommPort = callBack.checkCommPort(portName);
        try {
            SwingUtilities.invokeAndWait(() -> {
                offTitleComponents();
                onInputComponents();
                // загрузка пользователей в комбо бокс
                loadUsersToComboBox();
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

    // загрузка пользователей в комбо бокс
    private void loadUsersToComboBox() {
        comboBoxUser.removeAllItems();
        Arrays.stream(listUsers).sorted(new Comparator<UserClass>() {
            @Override
            public int compare(UserClass a, UserClass b) {
                return a.name.compareTo(b.name);
            }
        }).forEach (u->comboBoxUser.addItem(u));
    }

    // начальная загрузка параметров соединения с БД
    private boolean beginInitParametersSql() {
        // тип БД
        typeBaseData = callBack.getTypeBaseDataFromConfig();
        if (typeBaseData == BaseData.TypeBaseData.ERROR) {
            listUsers = new UserClass[0];
            flCheckSql = false;
            return false;
        }
        int result;
        // чтение параметров из конфига
        parametersSql = callBack.getParametersSqlFromConfig(typeBaseData);
        if (parametersSql.getStat() != ParametersSql.OK) {
            listUsers = new UserClass[0];
            flCheckSql = false;
            return false;
        }
        return true;
    }

    // начальная инициация соединения c БД и получение списка пользователей
    private boolean beginInitConnectBdGetListUsers() {
        int result;
        // установка тестового соединения
        result = callBack.createTestConnectBd(typeBaseData,
                new BaseData.Parameters(
                        parametersSql.urlServer,
                        parametersSql.portServer,
                        parametersSql.user,
                        parametersSql.password,
                        parametersSql.dataBase
                )
        );
        if (result != BaseData.OK) {
            listUsers = new UserClass[0];
            flCheckSql = false;
            return false;
        }
        // проверка структуры БД
        result = callBack.testConnectCheckStructure(parametersSql.dataBase);
        if (result != BaseData.OK) {
            listUsers = new UserClass[0];
            flCheckSql = false;
            return false;
        }
        // установка рабочего соединения
        result = callBack.createWorkConnect(typeBaseData,
                new BaseData.Parameters(
                        parametersSql.urlServer,
                        parametersSql.portServer,
                        parametersSql.user,
                        parametersSql.password,
                        parametersSql.dataBase
                )
        );
        if (result != BaseData.OK) {
            listUsers = new UserClass[0];
            flCheckSql = false;
            return false;
        }
        flCheckSql = true;
        // загрузка списка пользователей
        listUsers = callBack.getListUsers(true);
        if (listUsers.length == 0) {
            return false;
        }
        return true;
    }

    private StartFrame(FrameCallBack callBack) {
        this.callBack = callBack;
        setLayout(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().removeAll();
                System.exit(2);
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
        label5.setBounds(460, 400, 90, 19);
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
        buttonSetPassword.setVisible(true);
        buttonSetPassword.setEnabled(false);
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
        textField.addActionListener(e -> {
            buttonEnter.setEnabled(true);
            callEnter();
        });
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
//            tuningFrame.frameConfig(callBack.getParameters(), new TuningFrameCallBack());
            tuningFrame.frameConfig(null, new TuningFrameCallBack());
        });
        return button;
    }
    private JButton getButtonSetPassword(String text, String fontName, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JButton button = new JButton();
        button.setFont(new java.awt.Font(fontName, fontStyle, fontSize));
        button.setText(text);
        button.setBounds(x, y, width, height);
        button.addActionListener(e -> {
            callSetNewPassword();
        });
        return button;
    }
    private JComboBox<UserClass> getComboBoxUser(String fontName, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JComboBox<UserClass> comboBox = new JComboBox<>();
        comboBox.setFont(new java.awt.Font(fontName, fontStyle, fontSize));
        comboBox.setBounds(x, y, width, height);
        comboBox.setEditable(true);
        comboBox.addActionListener(e -> {

        });
        comboBox.addItemListener(e -> {
            if (e.getStateChange() == 1) return;
            fieldPassword.setText("");
            fieldPassword.setEnabled(true);
            buttonEnter.setEnabled(true);
            buttonWork.setEnabled(false);
            buttonTuning.setEnabled(false);
            buttonSetPassword.setEnabled(false);
        });
        return comboBox;
    }

    // проверка встроенного администратор
    private boolean checkIntegratedAdministrator(String surName, String password) {
        return  surName.equals("Doc") && password.equals("aUxPMjIzNjA=");
    }
    // обработка ввод
    private void callEnter() {
        UserClass user = null;
        String password;
        boolean askLocalAdmin;
        boolean flAdmin;
        try {
            user = (UserClass) comboBoxUser.getSelectedItem();
            askLocalAdmin = false;
        } catch (ClassCastException e) {
            System.out.println("Local Admin ?");
            askLocalAdmin = true;
        }
        password = fieldPassword.getText();
        if (askLocalAdmin) {
            String surName = (String) comboBoxUser.getSelectedItem();
            String pass = BaseData.Password.encoding(password);
            // проверка на локального админа
            flAdmin = checkIntegratedAdministrator(surName, pass);
            if (!flAdmin) {
                System.out.println("пароль интегрированного админа не совпал");
                return;
            }
        } else {
            if (!user.password.equals(password)) {
                System.out.println("у пользователя из списка не совпал пароль (" + user.password + ")");
                return;
            }
            flAdmin = false; // тут должна быть проверка на администрирование
        }

        if (!flAdmin) {
            // здесь проверка условий запуска и ...
            System.out.println("тут должна быть ");
            fieldPassword.setText("");
            buttonSetPassword.setEnabled(true);
            if (flCheckCommPort && flCheckSql) buttonWork.setEnabled(true);
            return;
        }
        // ==== тут админ ===
        fieldPassword.setText("");
        buttonEnter.setEnabled(false);
        buttonWork.setEnabled(false);
        if (!askLocalAdmin) buttonSetPassword.setEnabled(true);
        buttonTuning.setEnabled(true);
    }
    // обработка новый пароль
    private void callSetNewPassword() {
        String newPassword = fieldPassword.getText();
        if  (newPassword.length() == 0) {
            System.out.println("новый пароль пустой!!!");
            return;
        }
        try {
            UserClass currentUser = (UserClass) comboBoxUser.getSelectedItem();
            // обновление записи в БД
            boolean result = callBack.setUserNewPassword(currentUser, newPassword);
            // обновление текущей записи в comboBox
            currentUser.password = newPassword;
            System.out.println("логин = " + currentUser.name + " новый пароль = " + newPassword + " статус = " + result);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        fieldPassword.setText("");
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
            //flCheckCommPort = callBack.checkCommPort();
            try {
                if (flCheckSql) {
                    //StartFrame.this.loadListUsers_old();
                }
            } catch (Exception e) {
                System.out.println("StartFrame.StartFrameCallBackTuningFrame ошибка чтения списка пользователей: " + e.getMessage());
            }
        }
    }

    // ===========================================================================
}
