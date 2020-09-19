package org.example.test24.loader.dialog;

import org.example.test24.RS232.CommPort;
import org.example.test24.bd.*;
import org.example.test24.lib.MyUtil;
import org.example.test24.lib.MySwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import static org.example.test24.lib.MyLogger.myLog;

public class StartFrame {
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
    private JComboBox<UserClass> comboBoxUsers;
    private JComboBox<Pusher> comboBoxPusher;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;

    // редактирование
    private JPanel jPanel1;
    private JButton buttonEditUsers;
    private JButton buttonEditPushers;
    //
    private TuningFrame tuningFrame;

    // ===============================================
    //             флаги
    // основной модуль запущен
    boolean statMainWork;
    // соединение установлено
    boolean flagConnecting = false;
    // целостности структуры БД
    boolean flagStructureIntegrity = false;
    // доступность ком порта
    boolean flagAvailabilityCommPort = false;
    // список пользователей / = [0] for false
    private UserClass[] listUsers = new UserClass[0];
    // список толкателей / = [0] for false
    private Pusher[] listPushers = new Pusher[0];


    FrameCallBack callBack;
    JFrame frame;

    BaseData.TypeBaseDate typeBaseDate;
    BaseData.Parameters parameters;
    BaseData connBD;


    public static StartFrame main(boolean statMainWork, FrameCallBack callBack) throws Exception {
        final StartFrame[] frame = new StartFrame[1];
        try {
            SwingUtilities.invokeAndWait(()->{
                frame[0] = new StartFrame(statMainWork, callBack);
                new Thread(()->{
                    frame[0].start();
                }, "StartFrame start").start();
            });
        } catch (InterruptedException e) {
            myLog.log(Level.SEVERE, "ошибка создания startFrame", e);
            throw new Exception(e);
        }
        return frame[0];
    }

    protected StartFrame(boolean statMainWork, FrameCallBack callBack) {
        // если основная программа работает, то ком порт нельзя проверять !!!!!!!!!!!!!!!!!!!!!!!
        this.statMainWork = statMainWork;
        this.callBack = callBack;
    }


    private BaseData.Parameters getParametersBaseData(BaseData.TypeBaseDate typeBaseDate) throws Exception {
        if (typeBaseDate == BaseData.TypeBaseDate.ERROR) {
            throw new Exception("ошибка типа базы данных");
        }
        BaseData.Parameters parameters;
        try {
            parameters = BaseData.Parameters.create(typeBaseDate);
        } catch (Exception e) {
            throw new Exception(e);
        }
        BaseData.Status result;
        // загрузка параметров БД
        result = parameters.load();
        if (result != BaseData.Status.OK) {
            myLog.log(Level.WARNING, "загрузка параметров соединения с БД поумолчанию");
            parameters.setDefault();
        }
        return parameters;
    }

    private BaseData getConnect(BaseData.Parameters parameters) throws Exception {
        BaseData bd;
        bd = BaseData.create(parameters);
        return bd;
    }

    private void openConnect(BaseData bd, BaseData.Parameters parameters) throws Exception {
        // открытие соединения с БД
        bd.openConnect(parameters);
        // проверка наличия БД
        String pbd = parameters.getDataBase();
        boolean flag = false;
        for (String b: bd.getListBase()) {
            if (pbd.equals(b)) {
                flag = true;
                break;
            }
        }
        if (!flag) throw new Exception("отсутствует БД: " + parameters.getDataBase());
    }

    private void initBaseData(BaseData.TypeBaseDate typeBaseDate) {
        // здесь сбросить флаги с БД
        flagConnecting = false;
        flagStructureIntegrity = false;
        listUsers = new UserClass[0];
        listPushers = new Pusher[0];
        // ----
        // загрузить параметры
        parameters = null;
        try {
            parameters = getParametersBaseData(typeBaseDate);
        } catch (Exception e) {
            myLog.log(Level.WARNING, "ошибка получения параметров подключения к БД", e);
            return;
        }
        // создание соединения
        connBD = null;
        try {
            connBD = getConnect(parameters);
            flagConnecting = true;
            // открытие БД
            openConnect(connBD, parameters);
        } catch (Exception e) {
            myLog.log(Level.WARNING, "ошибка соединения с БД", e);
            return;
        }
        // проверка структуры БД
        try {
            flagStructureIntegrity = connBD.checkCheckStructureBd(parameters.getDataBase());
        } catch (Exception e) {
            myLog.log(Level.WARNING, "ошибка соединения с БД", e);
            return;
        }
        if (!flagStructureIntegrity) {
            myLog.log(Level.WARNING, "ошибка структуры БД");
            return;
        }
        // чтение списка пользователей
        try {
            listUsers = connBD.getListUsers(true);
        } catch (Exception e) {
            myLog.log(Level.WARNING, "ошибка чтение списка пользователей с БД", e);
        }
        // чтение списка толкателей
        try {
            listPushers = connBD.getListPushers(true);
        } catch (Exception e) {
            myLog.log(Level.WARNING, "ошибка чтение списка толкателей с БД", e);
        }
    }

    // проверка ком порта
    private boolean isCheckCommPort(String portName) {
        boolean flag;
        if (statMainWork) {
            // если программа работает, то comm port занят - не чего его проверять
            flag = true;
        } else {
            try {
                flag = CommPort.isCheckCommPort(portName);
                if (!flag) myLog.log(Level.INFO, "port \"" + portName + "\" не доступен или занят");
            } catch (Exception e) {
                myLog.log(Level.SEVERE, "ошибка проверки comm port", e);
                flag = false;
            }
        }
        return flag;
    }

    private void start() {
        // загрузка компонентов и вывод загаловка
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
        // =================== загрузка начальных параметров ===================
        // загрузка параметров соединения с БД
        //------------------------------
        // чтение конфигурации
        BaseData.Config config = BaseData.Config.create();
        try {
            config.load1();
        } catch (Exception e) {
            myLog.log(Level.WARNING, "ошибка чтения файла конфигурации", e);
            config.setDefault();
        }
        // тип БД
        typeBaseDate = config.getTypeBaseData();
        // инициализация работы с БД и данных с ней связанных
        initBaseData(typeBaseDate);
        // *************************************************************************************
        // проверка ком порта
        flagAvailabilityCommPort = isCheckCommPort(config.getPortName());
        // ===================================================================================================
        // задержка для title
        if (!statMainWork) {
            try {
                Thread.sleep(2_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // открытие основного экрана
        offTitleComponents();
        if (!flagStructureIntegrity) {
            MySwingUtil.showMessage(frame,
                    "запуск начального экрана",
                    "структура БД нарушена - требуется вмешательство администратора",
                    30_000,
                    o -> onInputComponents()
            );
            return;
        } else {
            onInputComponents();
        }
        // загрузка пользователей в комбо бокс
        try {
            MyUtil.loadToComboBox(listUsers, comboBoxUsers);
        } catch (Exception e) {
            myLog.log(Level.SEVERE, "Ошибка загрузки пользователей в comboboxUser", e);
        }
        // загрузка толкателей в комбо бокс
        try {
            MyUtil.loadToComboBox(listPushers, comboBoxPusher);
        } catch (Exception e) {
            myLog.log(Level.SEVERE, "Ошибка загрузки толкателей в comboboxUser", e);
        }
        if (statMainWork) {
            // здесь загрузка текущего пользователя и толкателя, если потребуется
        }
        // -------

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
            comboBoxUsers = getComboBoxUser("Times New Roman", Font.PLAIN, 14, 190, 190, 350, 24);
            frame.add(comboBoxUsers);
            comboBoxUsers.setVisible(false);

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
        comboBoxUsers.setVisible(true);
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
        comboBoxUsers.setVisible(false);
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
        comboBox.addItemListener(e -> {
            if (e.getStateChange() == 1) return;
            callSelectUser();
        });
        return comboBox;
    }
    private JComboBox<Pusher> getComboBoxPusher(String fontName, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JComboBox<Pusher> comboBox = new JComboBox<>();
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
    private boolean permissionWork() {
        // флаг целостности структуры БД
        if (!flagStructureIntegrity) return false;
        // проверка доступности ком порта
        if (!flagAvailabilityCommPort) return false;
        // список пользователей / = [0] for false
        if (listUsers.length == 0) return false;
        // список толкателей / = [0] for false
        if (listPushers.length == 0) return false;
        return true;
    }
    // ======================================================
    // обработка ввод
    private void callEnter() {
        UserClass user = null;
        String password;
        boolean askLocalAdmin;
        try {
            user = (UserClass) comboBoxUsers.getSelectedItem();
            askLocalAdmin = false;
        } catch (ClassCastException e) {
            askLocalAdmin = true;
        }
        password = fieldPassword.getText();
        if (askLocalAdmin) {
            String surName = (String) comboBoxUsers.getSelectedItem();
            String pass = BaseData2.Password.encoding(password);
            // проверка на локального админа
            if (!checkIntegratedAdministrator(surName, pass)) {
                buttonEnter.setEnabled(false);
                buttonTuning.setVisible(false);
                MySwingUtil.showMessage(frame, "ошибка", "пароль не верен", 5_000, o-> buttonEnter.setEnabled(true));
                myLog.log(Level.WARNING, "попытка входа локальным админом: " + surName + "/" + password);
                return;
            }
            fieldPassword.setText("");
            // тут разрешение настройки
            buttonTuning.setVisible(true);
            // отключение толкателей
            comboBoxPusher.setEnabled(false);
            myLog.log(Level.INFO, "вход локальным админом");
            return;
        }
        // спрятать кнопку настройка
        buttonTuning.setVisible(false);
        // проверка пароля у пользователя из списка (БД)
        if (!user.password.equals(password)) {
            System.out.println("у пользователя из списка не совпал пароль (" + user.password + ")");
            // отключить кнопки управления
            buttonSetPassword.setEnabled(false);
            buttonEditUsers.setEnabled(false);
            buttonEditPushers.setEnabled(false);
            comboBoxPusher.setEnabled(false);
            // отключить органы проверки пароля
            fieldPassword.setEnabled(false);
            buttonEnter.setEnabled(false);
            myLog.log(Level.INFO, "ошибка ввода пароля: " + user.name + "/" + password);
            MySwingUtil.showMessage(frame, "ошибка", "пароль не верен", 5_000, o-> {
                fieldPassword.setEnabled(true);
                buttonEnter.setEnabled(true);
            });
            return;
        }
        myLog.log(Level.INFO, "вход пользователем " + user.name + " с привелегиями " + user.rang);
        // разрешение смены пароля
        fieldPassword.setText("");
        buttonSetPassword.setEnabled(true);
        // разрешение на редактирование пользователей
        buttonEditUsers.setEnabled((user.rang & (1 << UserClass.RANG_USERS)) != 0);
        // разрешение на редактирование толкателей
        buttonEditPushers.setEnabled((user.rang & (1 << UserClass.RANG_PUSHERS)) != 0);
        // разрешение кнопки работа
        buttonWork.setEnabled(true);
        // разрешение выбора толкателей
        comboBoxPusher.setEnabled(true);
    }
    // обработка новый пароль
    private void callSetNewPassword() {
        UserClass currentUser = (UserClass) comboBoxUsers.getSelectedItem();
        String newPassword = fieldPassword.getText();
        if  (newPassword.length() == 0) {
            MySwingUtil.showMessage(frame, "установка нового пароля", "новый пароль пустой !!!", 5_000, o -> buttonSetPassword.setEnabled(true));
            buttonSetPassword.setEnabled(false);
            myLog.log(Level.WARNING, "попытка установки пустово пароля пользователем " + currentUser.name );
            return;
        }
        try {
            connBD.setNewUserPassword(currentUser, newPassword);
            currentUser.password = newPassword;
            if (!newPassword.equals(((UserClass) comboBoxUsers.getSelectedItem()).password)) {
                myLog.log(Level.SEVERE, "ПАРОЛЬ НЕ ПЕРЕШЕЛ !!!!", new Exception("пароль не перешел"));
            }
        } catch (Exception e) {
            MySwingUtil.showMessage(frame, "установка нового пароля", "ошибка записи в БД", 5_000, o -> buttonSetPassword.setEnabled(true));
            buttonSetPassword.setEnabled(false);
            myLog.log(Level.SEVERE, "ошибка сохранения нового пароля", e);
        }
        fieldPassword.setText("");
    }
    // обработка выбора пользователя
    private void callSelectUser() {
        // разрешение ввода пароля
        fieldPassword.setText("");
        fieldPassword.setEnabled(true);
        // разрешение ввод
        buttonEnter.setEnabled(true);
        // отключение работа
        buttonWork.setEnabled(false);
        // отключение редактирование
        buttonEditUsers.setEnabled(false);
        buttonEditPushers.setEnabled(false);
        // отключение установки нового пароля
        buttonSetPassword.setVisible(false);
        // отключение выбора толкателя
        comboBoxPusher.setEnabled(false);
        // отключение настройки
        buttonTuning.setVisible(false);
    }
    // обработка "работа"
    private void callReturnToWork() {
        if (!permissionWork()) {
            MySwingUtil.showMessage(frame, "ошибка", "нет готовности системы", 5_000);
            myLog.log(Level.INFO, "нет готовности системы");
            return;
        }
        // ------------
        myLog.log(Level.SEVERE, "НАДО СДЕЛАТЬ !!!", new Exception("не реализован выход на главную программу"));
        //frame.removeAll();
        //frame.dispose();
        //callBack.closeFrame();
    }
    // обработка настройка
    private void callTuning() {
        if (1 == 1) {
            myLog.log(Level.SEVERE, "СДЕЛАТЬ !!!", new Exception("не реализовано запуск настройки"));
            return;
        }
        if (statMainWork) {
            // при основной работе нельзя менять параметры БД и порта
            MySwingUtil.showMessage(frame, "Настройка", "при основной работе нельзя менять параметры БД и порта", 10_000);
            buttonTuning.setVisible(false);
            return;
        }
        // отключение управления
        comboBoxUsers.setEnabled(false);
        fieldPassword.setEnabled(false);
        buttonEnter.setEnabled(false);
        buttonTuning.setVisible(false);
        // вызов окна
        Thread thread = new Thread(()->{
            try {
                tuningFrame = TuningFrame.createFrame(
                        new TuningFrameCallBack(),
                        statMainWork
                );
            } catch (InterruptedException e) {
                System.out.println("Ошибка вызова окна \"настройка\": " + e.getMessage());
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                System.out.println("Ошибка вызова окна \"настройка\": " + e.getMessage());
                e.printStackTrace();
            }
        }, "thread for start tunnig frame");
        thread.start();
    }
    // обработка редактирование пользователей
    private void callEditUsers() {
        SaveEnableComponents saveComponents = new SaveEnableComponents();
        saveComponents.offline();
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                new EditUsers(connBD,
                        new EditUsers.CallBack() {
                            @Override
                            public void messageCloseEditUsers(boolean newData) {
                                if (newData) {
                                    // здесь перезагрузка списка пользователей
                                    myLog.log(Level.SEVERE, "СДЕЛАТЬ !!!!!!!", new Exception("не реализована перезагрузка списка пользователей после редактирования"));
                                }
                                saveComponents.restore();
                            }

                            @Override
                            public UserClass getCurrentUser() {
                                return (UserClass) comboBoxUsers.getSelectedItem();
                            }
                        });
            });
        }, "create edit users").start();
    }
    // обработка редактирование толкателей
    private void callEditPushers() {

    }

    // callBack из TuningFrame
    private class TuningFrameCallBack implements FrameCallBack {
        // =================================
        // чтение параметров из конфига
        @Override
        public ParametersConfig getParametersConfig() {
            return callBack.getParametersConfig();
        }
        // создание объекта параметров соединения с БД
        @Override
        public ParametersSql2 createParametersSql(BaseData2.TypeBaseData typeBaseData) throws Exception {
            return callBack.createParametersSql(typeBaseData);
        }

        // запрос параметров соединения с БД
        @Override
        public ParametersSql2 requestParametersSql(BaseData2.TypeBaseData typeBaseData) throws Exception {
            return callBack.requestParametersSql(typeBaseData);
        }
        // -----------------------------------------------------------
        // создание тестого соединения
        @Override
        public BaseData2.Status createTestConnectBd(BaseData2.TypeBaseData typeBaseData, BaseData2.Parameters parameters) {
            return callBack.createTestConnectBd(typeBaseData, parameters);
        }
        // тестовое соединение проверка структуры БД
        @Override
        public BaseData2.Status checkCheckStructureBd(String base) {
            return callBack.checkCheckStructureBd(base);
        }
        // -----------------------------------------------------------
        // создание рабочего соединения
        @Override
        public BaseData2.Status createWorkConnect(BaseData2.TypeBaseData typeBaseData, BaseData2.Parameters parameters) {
            return callBack.createWorkConnect(typeBaseData, parameters);
        }
        // чтение списка пользователей
        @Override
        public UserClass[] getListUsers(boolean actual) throws Exception {
            return callBack.getListUsers(actual);
        }

        @Override
        public String[] getListBd() throws Exception {
            return callBack.getListBd();
        }

        // -----------------------------------------------------------
        // проверка ком порта
        @Override
        public boolean isCheckCommPort(boolean statMainWork, String portName) throws Exception {
            return callBack.isCheckCommPort(statMainWork, portName);
        }
    }

    // ===========================================================================
    class SaveEnableComponents {
        private boolean buttonEnter;
        private boolean buttonSetPassword;
        private boolean buttonWork;
        private boolean buttonTuning;
        private boolean buttonEditUsers;
        private boolean buttonEditPushers;
        private boolean comboBoxUsers;
        private boolean comboBoxPusher;
        private boolean fieldPassword;
        private boolean frame;
        public SaveEnableComponents() {
            save();
        }
        public void save() {
            buttonEnter = StartFrame.this.buttonEnter.isEnabled();
            buttonSetPassword = StartFrame.this.buttonSetPassword.isEnabled();
            buttonWork = StartFrame.this.buttonWork.isEnabled();
            buttonTuning = StartFrame.this.buttonTuning.isEnabled();
            buttonEditUsers = StartFrame.this.buttonEditUsers.isEnabled();
            buttonEditPushers = StartFrame.this.buttonEditPushers.isEnabled();
            comboBoxUsers = StartFrame.this.comboBoxUsers.isEnabled();
            comboBoxPusher = StartFrame.this.comboBoxPusher.isEnabled();
            fieldPassword = StartFrame.this.fieldPassword.isEnabled();
            frame = StartFrame.this.frame.isEnabled();
        }
        public void restore() {
            StartFrame.this.buttonEnter.setEnabled(buttonEnter);
            StartFrame.this.buttonSetPassword.setEnabled(buttonSetPassword);
            StartFrame.this.buttonWork.setEnabled(buttonWork);
            StartFrame.this.buttonTuning.setEnabled(buttonTuning);
            StartFrame.this.buttonEditUsers.setEnabled(buttonEditUsers);
            StartFrame.this.buttonEditPushers.setEnabled(buttonEditPushers);
            StartFrame.this.comboBoxUsers.setEnabled(comboBoxUsers);
            StartFrame.this.comboBoxPusher.setEnabled(comboBoxPusher);
            StartFrame.this.fieldPassword.setEnabled(fieldPassword);
            StartFrame.this.frame.setEnabled(frame);
        }
        public void offline() {
            StartFrame.this.buttonEnter.setEnabled(false);
            StartFrame.this.buttonSetPassword.setEnabled(false);
            StartFrame.this.buttonWork.setEnabled(false);
            StartFrame.this.buttonTuning.setEnabled(false);
            StartFrame.this.buttonEditUsers.setEnabled(false);
            StartFrame.this.buttonEditPushers.setEnabled(false);
            StartFrame.this.comboBoxUsers.setEnabled(false);
            StartFrame.this.comboBoxPusher.setEnabled(false);
            StartFrame.this.fieldPassword.setEnabled(false);
            StartFrame.this.frame.setEnabled(false);
        }
    }
}
