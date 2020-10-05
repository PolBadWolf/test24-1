package org.example.test24.loader.dialog;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;
import org.example.test24.RS232.CommPort;
import org.example.test24.bd.*;
import org.example.test24.bd.usertypes.Pusher;
import org.example.test24.bd.usertypes.User;
import org.example.test24.lib.swing.*;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

import static org.example.test24.lib.MyLogger.myLog;

public class StartFrame {
    static StartFrame startFrame;
    public interface CallBack {

    }
    // ----------------------------------
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
    private JComboBox<User> comboBoxUsers;
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
    private JTable tableFindPushers;
    private JTable tableFindUsers;

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
    private User[] listUsers = new User[0];
    // список толкателей / = [0] for false
    private Pusher[] listPushers = new Pusher[0];


    CallBack callBack;
    JFrame frame;

    TypeBaseDate typeBaseDate;
    BaseData.Parameters parameters;
    BaseData connBD;
    SaveEnableComponents saveEnableComponentsStartFrame;


    public static StartFrame main(boolean statMainWork, CallBack callBack) throws Exception {
        try {
            SwingUtilities.invokeAndWait(()->{
                startFrame = new StartFrame(statMainWork, callBack);
                new Thread(()->{
                    startFrame.start();
                }, "StartFrame start").start();
            });
        } catch (InterruptedException e) {
            myLog.log(Level.SEVERE, "ошибка создания startFrame", e);
            throw new Exception(e);
        }
        return startFrame;
    }

    protected StartFrame(boolean statMainWork, CallBack callBack) {
        // если основная программа работает, то ком порт нельзя проверять !!!!!!!!!!!!!!!!!!!!!!!
        this.statMainWork = statMainWork;
        //this.callBack = callBack;
    }


    private BaseData.Parameters getParametersBaseData(TypeBaseDate typeBaseDate) throws ParametersSqlException {
        if (typeBaseDate == null) { throw new ParametersSqlException("ошибка типа базы данных", Status.BASE_TYPE_NO_SELECT, null); }
        if (typeBaseDate == TypeBaseDate.ERROR) { throw new ParametersSqlException("ошибка типа базы данных", Status.BASE_TYPE_ERROR, null); }
        BaseData.Parameters parameters;
        try { parameters = BaseData.Parameters.create(typeBaseDate);
        } catch (Exception e) { throw new ParametersSqlException(e, ((BaseDataException) e).getStatus(), null);
        }
        Status result;
        // загрузка параметров БД
        try {
            result = parameters.load();
            if (result != Status.OK) { throw new ParametersSqlException(
                        "ошибка загрузка параметров соединения с БД: ",
                        result,
                        parameters);
            }
        } catch (Exception e) { throw (ParametersSqlException) e;
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

    private void initBaseData(TypeBaseDate typeBaseDate) {
        // здесь сбросить флаги с БД
        flagConnecting = false;
        flagStructureIntegrity = false;
        listUsers = new User[0];
        listPushers = new Pusher[0];
        // ----
        // загрузить параметры
        parameters = null;
        try { parameters = getParametersBaseData(typeBaseDate);
        } catch (Exception e) {
            myLog.log(Level.WARNING, "ошибка получения параметров подключения к БД", e);
            parameters = ((ParametersSqlException) e).getParameters();
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
        try { flagStructureIntegrity = connBD.checkStructureBd(parameters.getDataBase());
        } catch (Exception e) {
            myLog.log(Level.WARNING, "ошибка соединения с БД", e);
            return;
        }
        if (!flagStructureIntegrity) {
            myLog.log(Level.WARNING, "ошибка структуры БД");
            return;
        }
        // чтение списка пользователей
        try { listUsers = connBD.getListUsers(true);
        } catch (Exception e) { myLog.log(Level.WARNING, "ошибка чтение списка пользователей с БД", e);
        }
        // чтение списка толкателей
        try { listPushers = connBD.getListPushers(true);
        } catch (Exception e) { myLog.log(Level.WARNING, "ошибка чтение списка толкателей с БД", e);
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
        //
        saveEnableComponentsStartFrame = new SaveEnableComponents(new Component[]{
                buttonEnter,
                buttonSetPassword,
                buttonWork,
                buttonTuning,
                buttonEditUsers,
                buttonEditPushers,
                comboBoxUsers,
                comboBoxPusher,
                fieldPassword,
                frame
        });
        //
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
        loadAndSetBeginParameters();
        new SelectComboBox2Table<Pusher>(comboBoxPusher, tableFindPushers, listPushers);
        new SelectComboBox2Table<User>(comboBoxUsers, tableFindUsers, listUsers);
        // ===================================================================================================
        // задержка для title
        if (!statMainWork) {
            try { Thread.sleep(2_000);
            } catch (InterruptedException e) { e.printStackTrace();
            }
        }
        // открытие основного экрана
        offTitleComponents();
        if (!flagStructureIntegrity) {
            MySwingUtil.showMessage(frame,
                    "запуск начального экрана",
                    "структура БД нарушена - требуется вмешательство администратора",
                    30_000,
                    o -> {
                        onInputComponents();
                        frame.requestFocus();
                    }
            );
            return;
        } else { onInputComponents();
        }
        loadAndSetBeginParameters2();
        // ********************
        /*try {
            Date date = new Date();
            connBD.writeNewTypePusher(
                    0,
                    "BE-2",
                    120,
                    40,
                    10
             );
        } catch (BaseDataException e) {
            e.printStackTrace();
        }*/
        /*try {
            TypePusher[] typePushers = connBD.getListTypePushers(false);
            int a = 5;
        } catch (BaseDataException e) {
            e.printStackTrace();
        }*/
        /*new Thread(()->{
            SwingUtilities.invokeLater(()->{
                new EditTypePushers(
                        new EditTypePushers.CallBack() {
                            @Override
                            public long getCurrentId_loggerUser() {
                                return 0L;
                            }
                        },
                        connBD
                );
            });
        }).start();*/
        /*new Thread(()->{
            SwingUtilities.invokeLater(()->{
                new EditPushers(
                        new EditPushers.CallBack() {
                            @Override
                            public long getCurrentId_loggerUser() {
                                return 0;
                            }
                        },
                        connBD,
                        0L
                );
            });
        }).start();*/
        // ********************
    }
    private void loadAndSetBeginParameters() {
        // загрузка параметров соединения с БД
        //------------------------------
        // чтение конфигурации
        BaseData.Config config = BaseData.Config.create();
        try { config.load1();
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
    }
    private void loadAndSetBeginParameters2() {
        // загрузка пользователей в комбо бокс
        try { MyUtil.loadToComboBox(listUsers, comboBoxUsers, null);
        } catch (Exception e) { myLog.log(Level.SEVERE, "Ошибка загрузки пользователей в comboboxUser", e);
        }
        // загрузка толкателей в комбо бокс
        try { MyUtil.loadToComboBox(listPushers, comboBoxPusher, null);
        } catch (Exception e) { myLog.log(Level.SEVERE, "Ошибка загрузки толкателей в comboboxUser", e);
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
            label1 = CreateComponents.getjLabel("Стенд", new Font("Times New Roman", Font.PLAIN, 57), 220, 130, 148, 66, false, true);
            label2 = CreateComponents.getjLabel("испытания", new Font("Times New Roman", Font.PLAIN, 36), 210, 180, 227, 42, false, true);
            label3 = CreateComponents.getjLabel("гидротолкателей", new Font("Times New Roman", Font.PLAIN, 36), 170, 210, 258, 42, false, true);
            label4 = CreateComponents.getjLabel("Гумеров М.Н.", new Font("Times New Roman", Font.PLAIN, 11), 380, 400, 68, 20, false, true);
            label5 = CreateComponents.getjLabel("ЦЗЛАМ ЛА", new Font("Times New Roman", Font.PLAIN, 16), 460, 400, 90, 19, false, true);
            frame.add(label1);
            frame.add(label2);
            frame.add(label3);
            frame.add(label4);
            frame.add(label5);
            //
            jLabel1 = CreateComponents.getjLabel("Пользователь : ", new Font("Times New Roman", Font.PLAIN, 14), 100, 195, 90, 16, false, true);
            jLabel2 = CreateComponents.getjLabel("Пароль :", new Font("Times New Roman", Font.PLAIN, 14), 100, 235, 90, 16, false, true);
            jLabel3 = CreateComponents.getjLabel("Толкатель :", new Font("Times New Roman", Font.PLAIN, 14), 100, 270, 90, 16, false, true);
            frame.add(jLabel1);
            frame.add(jLabel2);
            frame.add(jLabel3);
        } // подписи, надписи
        {
            comboBoxUsers = CreateComponents.<User>getComboBox(new Font("Times New Roman", Font.PLAIN, 14),
                    190, 190, 350, 24, true,
                    null,
                    this::callSelectUser,
                    false, true);
            comboBoxPusher = CreateComponents.<Pusher>getComboBox(new Font("Times New Roman", Font.PLAIN, 14),
                    190, 270, 350, 24, true,
                    null,
                    null,
                    false, true);
            tableFindPushers = CreateComponents.getTable(200,
                    null,
                    new CreateComponents.ModelTableNameWidth[]{
                            new CreateComponents.ModelTableNameWidth("Толкатель", -1)
                    },
                    null,
                    null,
                    false,
                    true
            );
            tableFindUsers = CreateComponents.getTable(200,
                    null,
                    null,
                    null, //controlTableFindPusher.getFilterTable(),
                    null, //controlTableFindPusher::callTableFindPushers,
                    false,
                    true
            );

            //
            tableFindPushers.setBounds(190, 300, 350, 30);
            tableFindUsers.setBounds(190, 220, 350, 30);

            frame.add(comboBoxUsers);
            frame.add(comboBoxPusher);
            frame.add(tableFindPushers);
            frame.add(tableFindUsers);
            tableFindPushers.updateUI();
        } // селекторы
        {
            buttonEnter = CreateComponents.getButton("проверка", new Font("Times New Roman", Font.PLAIN, 14), 320, 230, 90, 24, this::callEnter, false, true);
            buttonWork = CreateComponents.getButton("работа", new Font("Times New Roman", Font.PLAIN, 14), 200, 330, 90, 24, this::callReturnToWork, false, true);
            buttonTuning = CreateComponents.getButton("настройка", new Font("Times New Roman", Font.PLAIN, 14), 190, 370, 116, 24, this::callTuning, false, true);
            buttonSetPassword = CreateComponents.getButton("новый пароль", new Font("Times New Roman", Font.PLAIN, 14), 420, 230, 116, 24, this::callSetNewPassword, false, true);
            frame.add(buttonEnter);
            frame.add(buttonWork);
            frame.add(buttonTuning);
            frame.add(buttonSetPassword);
        } // кнопки
        {
            jPanel1 = CreateComponents.getPanel(null, new Font("Times New Roman", Font.PLAIN, 12), "редактирование", 380, 310, 160, 90,true, true );
            // кнопка редактирования пользователей
            // кнопка редактирования толкателей
            buttonEditUsers = CreateComponents.getButton("Пользователей", new Font("Times New Roman", Font.PLAIN, 14), 20, 20, 120, 24, this::callEditUsers, true, true);
            buttonEditPushers = CreateComponents.getButton("Толкателей", new Font("Times New Roman", Font.PLAIN, 14), 20, 55, 120, 24, this::callEditPushers, true, true);
            jPanel1.add(buttonEditUsers);
            jPanel1.add(buttonEditPushers);

            frame.add(jPanel1);
            jPanel1.setVisible(false);
        } // панель редактирование
        fieldPassword = CreateComponents.getTextField(CreateComponents.PASSWORDFIELD, new Font("Times New Roman", Font.PLAIN, 14), 190, 230,120, 24, null, null, false, true);
        frame.add(fieldPassword);

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
    private void callEnter(ActionEvent e) {
        User user = null;
        String password;
        boolean askLocalAdmin;
        try {
            user = (User) comboBoxUsers.getSelectedItem();
            askLocalAdmin = false;
        } catch (ClassCastException e2) {
            askLocalAdmin = true;
        }
        password = fieldPassword.getText();
        if (askLocalAdmin) {
            String surName = (String) comboBoxUsers.getSelectedItem();
            String pass = BaseData.Password.encoding(password);
            // проверка на локального админа
            if (!checkIntegratedAdministrator(surName, pass)) {
                buttonEnter.setEnabled(false);
                buttonTuning.setVisible(false);
                MySwingUtil.showMessage(frame, "ошибка", "пароль не верен", 5_000, o-> {
                    buttonEnter.setEnabled(true);
                    frame.requestFocus();
                });
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
        if (!user.userPassword.equals(password)) {
            System.out.println("у пользователя из списка не совпал пароль (" + user.userPassword + ")");
            // отключить кнопки управления
            saveEnableComponentsStartFrame.save();
            saveEnableComponentsStartFrame.offline();
            myLog.log(Level.INFO, "ошибка ввода пароля: " + user.surName + "/" + password);
            MySwingUtil.showMessage(frame, "ошибка", "пароль не верен", 5_000, o-> {
                saveEnableComponentsStartFrame.restore();
                frame.requestFocus();
            });
            return;
        }
        myLog.log(Level.INFO, "вход пользователем " + user.surName + " с привелегиями " + user.rang);
        // разрешение смены пароля
        fieldPassword.setText("");
        buttonSetPassword.setEnabled(true);
        // разрешение на редактирование пользователей
        buttonEditUsers.setEnabled((user.rang & (1 << User.RANG_USERS)) != 0);
        // разрешение на редактирование толкателей
        buttonEditPushers.setEnabled((user.rang & (1 << User.RANG_PUSHERS)) != 0);
        // разрешение кнопки работа
        buttonWork.setEnabled(true);
        // разрешение выбора толкателей
        comboBoxPusher.setEnabled(true);
    }
    // обработка новый пароль
    private void callSetNewPassword(ActionEvent f) {
        User currentUser = (User) comboBoxUsers.getSelectedItem();
        String newPassword = fieldPassword.getText();
        if  (newPassword.length() == 0) {
            MySwingUtil.showMessage(frame, "установка нового пароля", "новый пароль пустой !!!", 5_000, o -> {
                buttonSetPassword.setEnabled(true);
                frame.requestFocus();
            });
            buttonSetPassword.setEnabled(false);
            myLog.log(Level.WARNING, "попытка установки пустово пароля пользователем " + currentUser.surName );
            return;
        }
        try {
            connBD.setNewUserPassword(currentUser, newPassword);
            currentUser.userPassword = newPassword;
            if (!newPassword.equals(((User) comboBoxUsers.getSelectedItem()).userPassword)) {
                myLog.log(Level.SEVERE, "ПАРОЛЬ НЕ ПЕРЕШЕЛ !!!!", new Exception("пароль не перешел"));
            }
        } catch (Exception e) {
            MySwingUtil.showMessage(frame, "установка нового пароля", "ошибка записи в БД", 5_000, o -> {
                buttonSetPassword.setEnabled(true);
                frame.requestFocus();
            });
            buttonSetPassword.setEnabled(false);
            myLog.log(Level.SEVERE, "ошибка сохранения нового пароля", e);
        }
        fieldPassword.setText("");
    }
    // обработка выбора пользователя
    private void callSelectUser(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) return;
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
        buttonSetPassword.setEnabled(false);
        // отключение выбора толкателя
        comboBoxPusher.setEnabled(false);
        // отключение настройки
        buttonTuning.setVisible(false);
    }
    // обработка "работа"
    private void callReturnToWork(ActionEvent e) {
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
    private void callTuning(ActionEvent e) {
        /*if (1 == 1) {
            myLog.log(Level.SEVERE, "СДЕЛАТЬ !!!", new Exception("не реализовано запуск настройки"));
            return;
        }*/
        if (statMainWork) {
            // при основной работе нельзя менять параметры БД и порта
            MySwingUtil.showMessage(frame, "Настройка", "при основной работе нельзя менять параметры БД и порта", 10_000);
            buttonTuning.setVisible(false);
            return;
        }
        // отключение управления
        saveEnableComponentsStartFrame.save();
        saveEnableComponentsStartFrame.offline();
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                new TuningFrame(new TuningFrame.CallBack() {
                    @Override
                    public void messageCloseTuning(boolean newData) {
                        saveEnableComponentsStartFrame.restore();
                        loadAndSetBeginParameters();
                        loadAndSetBeginParameters2();
                    }
                });
            });
        }, "create tuning").start();
    }
    // обработка редактирование пользователей
    private void callEditUsers(ActionEvent e) {
        saveEnableComponentsStartFrame.save();
        saveEnableComponentsStartFrame.offline();
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                new EditUsers(connBD,
                        new EditUsers.CallBack() {
                            @Override
                            public void messageCloseEditUsers(boolean newData) {
                                if (newData) {
                                    // **** здесь перезагрузка списка пользователей
                                    // чтение списка пользователей
                                    try {
                                        listUsers = connBD.getListUsers(true);
                                    } catch (Exception e) {
                                        myLog.log(Level.WARNING, "ошибка чтение списка пользователей с БД", e);
                                        listUsers = new User[0];
                                        MySwingUtil.showMessage(
                                                frame,
                                                "обновление списка пользователей",
                                                "ошибка обновления - требуется вмешательство администратора",
                                                60_000
                                        );
                                        saveEnableComponentsStartFrame.restore();
                                        frame.requestFocus();
                                        return;
                                    }
                                    // загрузить обновленный список
                                    try { MyUtil.loadToComboBox(listUsers, comboBoxUsers, null);
                                    } catch (Exception e) {
                                        myLog.log(Level.SEVERE, "Ошибка загрузки пользователей в comboboxUser", e);
                                        MySwingUtil.showMessage(
                                                frame,
                                                "обновление списка пользователей",
                                                "ошибка обновления - требуется вмешательство администратора",
                                                10_000
                                        );
                                    }
                                }
                                saveEnableComponentsStartFrame.restore();
                                frame.requestFocus();
                            }

                            @Override
                            public User getCurrentUser() {
                                return (User) comboBoxUsers.getSelectedItem();
                            }
                        });
            });
        }, "create edit users").start();
    }
    // обработка редактирование толкателей
    private void callEditPushers(ActionEvent e) {
        //myLog.log(Level.SEVERE, "СДЕЛАТЬ !!!", new Exception("редактирование толкателей"));
        saveEnableComponentsStartFrame.save();
        saveEnableComponentsStartFrame.offline();
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                new EditPushers(
                        new EditPushers.CallBack() {
                            @Override
                            public long getCurrentId_loggerUser() {
                                return 0;
                            }
                        },
                        connBD,
                        ((User) comboBoxUsers.getSelectedItem()).id_loggerUser
                );
            });
        }, "create edit pushers").start();
    }
    // ===========================================================================
}
