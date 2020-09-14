package org.example.test24.loader.dialog;

import org.example.test24.bd.*;
import org.example.test24.lib.MyUtil;
import org.example.test24.lib.MySwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;

import static org.example.test24.lib.MyLogger.myLog;

public class StartFrame extends StartFrame_Vars {
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
    private TuningFrame tuningFrame;


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
        } catch (InvocationTargetException e) {
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


    private BaseData.Parameters loadBaseData(BaseData.TypeBaseDate typeBaseDate) {
        BaseData.Parameters parameters = BaseData.Parameters.create(typeBaseDate);
        if (typeBaseDate == BaseData.TypeBaseDate.ERROR) {
            myLog.log(Level.SEVERE, "ошибка типа базы данных");
            return parameters;
        }
        BaseData.Status result;
        // загрузка параметров БД
        result = parameters.load();
        return parameters;
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
        ParametersConfig config3;
        BaseData2.TypeBaseData typeBaseData3;
        BaseData2.Status resultBaseData;
        ParametersSql2 parametersSql2 = null;
        //------------------------------
        BaseData.Status result;
        BaseData.TypeBaseDate typeBaseDate;
        BaseData.Parameters parameters;
        // чтение конфигурации
        BaseData.Config config = BaseData.Config.create();
        config.load1();
        // тип БД
        typeBaseDate = config.getTypeBaseData();
        loadBaseData(typeBaseDate);


        int parametersSqlError = 1;
        UserClass[] listUsers = new UserClass[0];
        // ======================================
        // запрос конфигурации
        config3 = callBack.getParametersConfig();
        // тип БД
        typeBaseData3 = null;//config3.getTypeBaseData();
        if (typeBaseData3 == BaseData2.TypeBaseData.ERROR) {
            //throw new Exception("ошибка типа базы данных");
        }
        // загрузка параметров БД
        try {
            parametersSql2 = callBack.requestParametersSql(typeBaseData3);
            parametersSqlError = 0;
        } catch (Exception e) {
            System.out.println("Ошибка загрузки параметров соединения с БД" + e.getMessage());
            parametersSqlError = 1;
        }
        if (parametersSqlError == 0) {
            // установка тестового соединения
            resultBaseData = callBack.createTestConnectBd(
                    parametersSql2.typeBaseData,
                    new BaseData2.Parameters(
                            parametersSql2.urlServer,
                            parametersSql2.portServer,
                            parametersSql2.user,
                            parametersSql2.password,
                            parametersSql2.dataBase
                    )
            );
            if (resultBaseData != BaseData2.Status.OK) {
                System.out.println("ошибка установки тестового соединения");
                flCheckSql = false;
                listUsers = new UserClass[0];
            } else {
                // тестовое соединение проверка структуры БД
                resultBaseData = callBack.checkCheckStructureBd(parametersSql2.dataBase);
                if (resultBaseData == BaseData2.Status.OK) {
                    flCheckSql = true;
                } else {
                    System.out.println("нарушена целостность структуры БД");
                    flCheckSql = false;
                    listUsers = new UserClass[0];
                }
            }
            if (flCheckSql) {
                // создание рабочего соединения
                resultBaseData = callBack.createWorkConnect(
                        parametersSql2.typeBaseData,
                        new BaseData2.Parameters(
                                parametersSql2.urlServer,
                                parametersSql2.portServer,
                                parametersSql2.user,
                                parametersSql2.password,
                                parametersSql2.dataBase
                        )
                );
                if (resultBaseData != BaseData2.Status.OK) {
                    System.out.println("ошибка установки рабочего соединения");
                    listUsers = new UserClass[0];
                } {
                    // чтение списка пользователей
                    try {
                        listUsers = callBack.getListUsers(true);
                    } catch (Exception e) {
                        listUsers = new UserClass[0];
                        System.out.println("Ошибка чтения списка пользователей: " + e.getMessage());
                    }
                }
            }
            // *************************************************************************************
            System.out.println("список пользователей, всего " + listUsers.length + " :");
            Arrays.stream(listUsers).sorted(new Comparator<UserClass>() {
                @Override
                public int compare(UserClass a, UserClass b) {
                    return a.name.compareTo(b.name);
                }
            }).forEach(user -> System.out.println(user.toString()));
        }
        // *************************************************************************************
        // проверка ком порта
        try {
            flCheckCommPort = callBack.isCheckCommPort(statMainWork, config3.getPortName());
        } catch (Exception e) {
            System.out.println("Ошибка поверки ком порта: " + e.getMessage());
            flCheckCommPort = false;
        }
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
        try {
            //SwingUtilities.invokeAndWait(() -> {
                offTitleComponents();
                onInputComponents();
                // загрузка пользователей в комбо бокс
            try {
                MyUtil.<UserClass>loadToComboBox(listUsers, comboBoxUser);
            } catch (Exception e) {
                System.out.println("Ошибка загрузки пользователей в comboboxUser: " + e.getMessage());
            }
            // -------
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
        }

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
            String pass = BaseData2.Password.encoding(password);
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
            // отключить кнопки управления
            buttonSetPassword.setEnabled(false);
            buttonEditUsers.setEnabled(false);
            buttonEditPushers.setEnabled(false);
            // отключить органы проверки пароля
            fieldPassword.setEnabled(false);
            buttonEnter.setEnabled(false);
            MySwingUtil.showMessage(frame, "ошибка", "пароль не верен", 5_000, o-> {
                fieldPassword.setEnabled(true);
                buttonEnter.setEnabled(true);
            });
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
            boolean result = false;
            //result = callBack.setUserNewPassword(currentUser, newPassword);
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
        //callBack.closeFrame();
    }
    // обработка настройка
    private void callTuning() {
        if (statMainWork) {
            // при основной работе нельзя менять параметры БД и порта
            MySwingUtil.showMessage(frame, "Настройка", "при основной работе нельзя менять параметры БД и порта", 10_000);
            buttonTuning.setVisible(false);
            return;
        }
        // отключение управления
        comboBoxUser.setEnabled(false);
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

}
