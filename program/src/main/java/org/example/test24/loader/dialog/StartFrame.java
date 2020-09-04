package org.example.test24.loader.dialog;

import org.example.test24.bd.BaseData;
import org.example.lib.MySwingUtil;
import org.example.test24.bd.ParametersSql;
import org.example.test24.bd.UserClass;
import org.example.test24.loader.ParametersConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Consumer;

public class StartFrame extends StartFrameVars {
    // title
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
    private JComboBox<String> comboBoxPusher;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;

    // редактирование
    private JPanel jPanel1;
    private JButton buttonEditUsers;
    private JButton buttonEditPushers;
    //
    //


    public static StartFrame main(boolean statMainWork, FrameCallBack callBack) {
        final StartFrame[] frame = new StartFrame[1];
        frame[0] = null;
        try {
            SwingUtilities.invokeAndWait(() -> {
                frame[0] = new StartFrame(statMainWork, callBack);
            });
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
        }
        new Thread( ()-> {
            frame[0].start();
        }, "start start frame"
        ).start();
        return frame[0];
    }

    private StartFrame(boolean statMainWork, FrameCallBack callBack) {
        // если основная программа работает, то ком порт нельзя проверять !!!!!!!!!!!!!!!!!!!!!!!
        this.statMainWork = statMainWork;
        this.callBack = callBack;
        // =================== загрузка начальных параметров ===================
    }


    private void start() {
        boolean res;
        // загрузка компонентов и вывод загаловка
        SwingUtilities.invokeLater(() -> {
            initComponents();
            onTitleComponents();
            frame.setResizable(false);
            frame.setLayout(null);
            frame.setVisible(true);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    e.getWindow().removeAll();
                    System.exit(2);
                }
            });
        });
        // получения списка пользователей параметры из конфига
        res =  getListUserFromConfig();
        if (res) {
            flCheckSql = true;
        } else {
            flCheckSql = false;
        }
        // задержка на пока начального экрана
        try {
            Thread.sleep(2_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // чтение порта из конфига
        //commPortName = callBack.getCommPortNameFromConfig();
        if (!callBack.requestCommPortNameFromConfig(portName -> {
            commPortName = portName;
        })) {
            System.out.println("ошибка получения имени comm port");
            commPortName = "";
        }
        // проверка Comm port
        flCheckCommPort = callBack.checkCommPort(commPortName);
        try {
            //SwingUtilities.invokeAndWait(() -> {
                offTitleComponents();
                onInputComponents();
                // загрузка пользователей в комбо бокс
                loadUsersToComboBox();
            //});
            // --------
            /*TuningFrame tuningFrame;
            tuningFrame = callBack.getTuningFrame();
            tuningFrame.frameConfig(callBack.getParameters(), new TuningFrameCallBack_old());*/
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


    private void initComponents() {
        frame = new JFrame();
        frame.setPreferredSize(new Dimension(640, 480));
        {
            label1 = new JLabel();
            label2 = new JLabel();
            label3 = new JLabel();
            label4 = new JLabel();
            label5 = new JLabel();
            jLabel1 = new JLabel();
            jLabel2 = new JLabel();
            jLabel3 = new JLabel();
        } // подписи, надписи
        {
            label1.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 57));
            label1.setText("Стенд");
            frame.add(label1);
            label1.setBounds(220, 130, 148, 66);
            label1.setVisible(false);

            label2.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 36)); // ******************
            label2.setText("испытания");
            frame.add(label2);
            label2.setBounds(180, 180, 227, 42);
            label2.setVisible(false);

            label3.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 36));
            label3.setText("гидротолкателей");
            frame.add(label3);
            label3.setBounds(170, 210, 258, 42);
            label3.setVisible(false);

            label4.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 11));
            label4.setText("Гумеров М.Н.");
            frame.add(label4);
            label4.setBounds(380, 400, 68, 20);
            label4.setVisible(false);

            label5.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 16));
            label5.setText("ЦЗЛАМ ЛА");
            frame.add(label5);
            label5.setBounds(460, 400, 90, 19);
            label5.setVisible(false);

            jLabel1.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 14));
            jLabel1.setText("Пользователь : ");
            jLabel1.setBounds(100, 200, 90, 16);
            frame.add(jLabel1);
            jLabel1.setVisible(false);

            jLabel2.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 14));
            jLabel2.setText("Пароль :");
            jLabel2.setBounds(100, 240, 90, 16);
            frame.add(jLabel2);
            jLabel2.setVisible(false);

            jLabel3.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 14));
            jLabel3.setText("Толкатель :");
            jLabel3.setBounds(100, 270, 90, 16);
            frame.add(jLabel3);
            jLabel3.setVisible(false);
        } // подписи, надписи
        {
            comboBoxUser = getComboBoxUser("Times New Roman", Font.PLAIN, 14, 190, 190, 350, 24);
            frame.add(comboBoxUser);
            comboBoxUser.setVisible(false);

            comboBoxPusher = getComboBoxPusher("Times New Roman", Font.PLAIN, 14, 190, 270, 350, 24);
            frame.add(comboBoxPusher);
            comboBoxPusher.setVisible(false);
        } // селекторы
        fieldPassword = getFieldPassword("Times New Roman", Font.PLAIN, 14, 190, 230, 120, 24);
        frame.add(fieldPassword);
        fieldPassword.setVisible(false);
        {
            buttonEnter = getButtonEnter("проверка", "Times New Roman", Font.PLAIN, 14, 320, 230, 90, 24);
            frame.add(buttonEnter);
            buttonEnter.setVisible(false);

            buttonWork = getButtonWork("работа", "Times New Roman", Font.PLAIN, 14, 200, 330, 90, 24);
            frame.add(buttonWork);
            buttonWork.setVisible(false);

            buttonTuning = getButtonTuning("настройка", "Times New Roman", Font.PLAIN, 14, 190, 370, 116, 24);
            frame.add(buttonTuning);
            buttonTuning.setVisible(false);

            buttonSetPassword = getButtonSetPassword("новый пароль", "Times New Roman", Font.PLAIN, 14, 420, 230, 116, 24);
            frame.add(buttonSetPassword);
            buttonSetPassword.setVisible(false);
        } // кнопки
        {
            jPanel1 = new JPanel();
            jPanel1.setLayout(null);
            jPanel1.setBounds(380, 310, 160, 90);
            jPanel1.setBorder(BorderFactory.createTitledBorder("редактирование"));
            // кнопка редактирования пользователей
            buttonEditUsers = getButtonEditUsers("Пользователей", "Times New Roman", Font.PLAIN, 14, 20, 20, 120, 24);
            jPanel1.add(buttonEditUsers);
            // кнопка редактирования толкателей
            buttonEditPushers = getButtonEditPushers("Толкателей", "Times New Roman", Font.PLAIN, 14, 20, 55, 120, 24);
            jPanel1.add(buttonEditPushers);

            frame.add(jPanel1);
            jPanel1.setVisible(false);
        } // панель редактирование

        frame.pack();
    }

    private void onTitleComponents() {
        label1.setVisible(true);
        label2.setVisible(true);
        label3.setVisible(true);
        label4.setVisible(true);
        label5.setVisible(true);
    }
    private void offTitleComponents() {
        label1.setBounds(250, 20, 150, 66);
        label2.setBounds(240, 70, 180, 42);
        label3.setBounds(200, 100, 260, 42);
        label4.setVisible(false);
        label5.setVisible(false);
    }
    private void onInputComponents() {
        jLabel1.setVisible(true);
        jLabel2.setVisible(true);
        jLabel3.setVisible(true);
        comboBoxUser.setVisible(true);
        comboBoxPusher.setVisible(true);
        fieldPassword.setVisible(true);
        buttonEnter.setVisible(true);
        buttonWork.setVisible(true);
        buttonSetPassword.setVisible(true);
        jPanel1.setVisible(true);
        buttonTuning.setVisible(false);
        //
        buttonTuning.setEnabled(true);
        buttonSetPassword.setEnabled(false);
        buttonEditUsers.setEnabled(false);
        buttonEditPushers.setEnabled(false);
        if (statMainWork) {
            buttonWork.setEnabled(true);
            comboBoxPusher.setEnabled(true);
            // здесь установка последнего пользователя текущим !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        } else {
            buttonWork.setEnabled(false);
            comboBoxPusher.setEnabled(false);
        }
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
        button.addActionListener(e ->callReturnToWork());
        return button;
    }
    private JButton getButtonTuning(String text, String fontName, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JButton button = new JButton();
        button.setFont(new java.awt.Font(fontName, fontStyle, fontSize));
        button.setText(text);
        button.setBounds(x, y, width, height);
        button.addActionListener(e -> callTuning());
        return button;
    }
    private JButton getButtonSetPassword(String text, String fontName, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JButton button = new JButton();
        button.setFont(new java.awt.Font(fontName, fontStyle, fontSize));
        button.setText(text);
        button.setBounds(x, y, width, height);
        button.addActionListener(e -> callSetNewPassword());
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
            callSelectUser();
        });
        return comboBox;
    }
    private JComboBox<String> getComboBoxPusher(String fontName, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setFont(new java.awt.Font(fontName, fontStyle, fontSize));
        comboBox.setBounds(x, y, width, height);
        comboBox.setEditable(true);
        comboBox.addActionListener(e -> {

        });
        comboBox.addItemListener(e -> {
            if (e.getStateChange() == 1) return;
        });
        return comboBox;
    }
    private JButton getButtonEditUsers(String text, String fontName, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JButton button = new JButton();
        button.setFont(new java.awt.Font(fontName, fontStyle, fontSize));
        button.setText(text);
        button.setBounds(x, y, width, height);
        button.addActionListener(e ->callEditUsers());
        return button;
    }
    private JButton getButtonEditPushers(String text, String fontName, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JButton button = new JButton();
        button.setFont(new java.awt.Font(fontName, fontStyle, fontSize));
        button.setText(text);
        button.setBounds(x, y, width, height);
        button.addActionListener(e -> callEditPushers());
        return button;
    }

    // проверка встроенного администратор
    private boolean checkIntegratedAdministrator(String surName, String password) {
//        return  surName.equals("Doc") && password.equals("aUxPMjIzNjA=");
        return  surName.equals("a") && password.equals("");
    }
    // разрешение кнопки работа
    private boolean permissionButtonWork() {
        // флаг целостности структуры БД
        if (!flCheckSql) {
            buttonWork.setEnabled(false);
            return false;
        }
        // флаг работы программы ( ком порт занят)
        if (!statMainWork) {
            // проверка ком порта
            if (!flCheckCommPort) {
                buttonWork.setEnabled(false);
                return false;
            }
        }
        buttonWork.setEnabled(true);
        return true;
    }
    // ======================================================
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
                buttonEnter.setEnabled(false);
                MySwingUtil.showMessage(frame, "ошибка", "пароль не верен", 5_000, o-> buttonEnter.setEnabled(true));
                return;
            }
            fieldPassword.setText("");
            // тут разрешение настройки
            buttonTuning.setVisible(true);
            return;
        }
        // спрятать кнопку настройка
        buttonTuning.setVisible(false);
        // проверка пароля у пользователя из списка (БД)
        if (!user.password.equals(password)) {
            System.out.println("у пользователя из списка не совпал пароль (" + user.password + ")");
            buttonSetPassword.setEnabled(false);
            buttonEditUsers.setEnabled(false);
            buttonEditPushers.setEnabled(false);
            MySwingUtil.showMessage(frame, "ошибка", "пароль не верен", 5_000, o-> buttonEnter.setEnabled(true));
            return;
        }
        // разрешение смены пароля
        fieldPassword.setText("");
        buttonSetPassword.setEnabled(true);
        // разрешение на редактирование пользователей
        buttonEditUsers.setEnabled((user.rank & (1 << 0)) != 0);
        // разрешение на редактирование толкателей
        buttonEditPushers.setEnabled((user.rank & (1 << 1)) != 0);
        // разрешение кнопки работа
        if (!
            permissionButtonWork()
        ) {
            MySwingUtil.showMessage(frame, "ошибка", "нет готовности системы", 5_000);
            return;
        }
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
    // обработка выбора пользователя
    private void callSelectUser() {
        fieldPassword.setText("");
        fieldPassword.setEnabled(true);
        buttonEnter.setEnabled(true);
        buttonWork.setEnabled(false);
        buttonEditUsers.setEnabled(false);
        buttonEditPushers.setEnabled(false);
        //
        buttonSetPassword.setVisible(false);
        buttonTuning.setVisible(false);
    }
    // обработка "работа"
    private void callReturnToWork() {
        frame.removeAll();
        frame.dispose();
        callBack.closeFrame();
    }
    // обработка настройка
    private void callTuning() {
        // отключение управления
        comboBoxUser.setEnabled(false);
        fieldPassword.setEnabled(false);
        buttonEnter.setEnabled(false);
        buttonTuning.setVisible(false);
        // вызов окна
        TuningFrame.createFrame(new TuningFrameCallBack(), statMainWork);

/*
        TuningFrame tuningFrame;
        tuningFrame = callBack.getTuningFrame();
//            tuningFrame.frameConfig(callBack.getParameters(), new TuningFrameCallBack_old());
        tuningFrame.frameConfig(null, new TuningFrameCallBack_old());
        */
    }
    // обработка редактирование пользователей
    private void callEditUsers() {

    }
    // обработка редактирование толкателей
    private void callEditPushers() {

    }

    // callBack из TuningFrame
    /*private class TuningFrameCallBack_old implements TuningFrame.CallBackToStartFrame {
        @Override
        public void messageCloseTuningFrame() {
            StartFrame startFrame = StartFrame.this;
            startFrame.fieldPassword.setText("");
            startFrame.fieldPassword.setEnabled(true);
            startFrame.buttonEnter.setEnabled(true);
            //flCheckSql = callBack.checkSqlFile();
            //flCheckCommPort = callBack.checkCommPort();
            try {
                if (flCheckSql) {
                    //StartFrame.this.loadListUsers_old();
                }
            } catch (Exception e) {
                System.out.println("StartFrame.StartFrameCallBackTuningFrame ошибка чтения списка пользователей: " + e.getMessage());
            }
        }
    }*/

    private class TuningFrameCallBack implements FrameCallBack {
        // чтение параметров из конфига
        @Override
        public ParametersConfig getParametersConfig() throws Exception {
            if (workParametersConfig == null) throw new Exception("ошибка получения параметров из конфига");
            return workParametersConfig;
        }
        // ================================== работа с БД ====================================
        // чтение типа БД из конфига
        @Override
        public BaseData.TypeBaseData getTypeBaseDataFromConfig() {
            return callBack. getTypeBaseDataFromConfig();
        }
        // чтение параметров из конфига
        @Override
        public ParametersSql getParametersSql(BaseData.TypeBaseData typeBaseData)  {
            return callBack.getParametersSql(typeBaseData);
        }
        // создание тестого соединения
        @Override
        public int createTestConnectBd(BaseData.TypeBaseData typeBaseData, BaseData.Parameters parameters) {
            return callBack.createTestConnectBd(typeBaseData, parameters);
        }
        // список доступных БД из тестового соединения
        @Override
        public boolean requestListBdFromTestConnect(Consumer<String[]> list) {
            return callBack.requestListBdFromTestConnect(list);
        }
        // проверка структуры БД
        @Override
        public int testConnectCheckStructure(String base) {
            return callBack.testConnectCheckStructure(base);
        }
        // создание рабочего соединения
        @Override
        public int createWorkConnect(BaseData.TypeBaseData typeBaseData, BaseData.Parameters parameters) {
            return callBack.createWorkConnect(typeBaseData, parameters);
        }
        // прочитать список пользователей
        @Override
        public UserClass[] getListUsers(boolean actual) {
            return callBack.getListUsers(actual);
        }
        // установка нового пароля пользователя
        @Override
        public boolean setUserNewPassword(UserClass user, String newPassword) {
            int a = 1/0;
            return false;
        }
        // ==================================== работа к ком портом ====================================
        // чтение comm port из конфига
        @Override
        public boolean requestCommPortNameFromConfig(Consumer<String> portName) {
            return callBack.requestCommPortNameFromConfig(portName);
        }
        // проверка Comm Port на валидность
        @Override
        public boolean checkCommPort(String portName) {
            int a = 1/0;
            return false;
        }
        // загрузка списка ком портов в системе
        @Override
        public String[] getComPortNameList() {
            return callBack.getComPortNameList();
        }

        @Override
        public void closeFrame() {
            // включение управления
            comboBoxUser.setEnabled(true);
            fieldPassword.setEnabled(true);
            buttonEnter.setEnabled(true);
        }
    }
    // ===========================================================================

}
