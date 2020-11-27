package ru.yandex.fixcolor.tests.spc.loader.dialog;

import ru.yandex.fixcolor.tests.spc.loader.MainClass;
import ru.yandex.fixcolor.tests.spc.loader.archive.ViewArchive;
import ru.yandex.fixcolor.tests.spc.loader.calibration.Calibration;
import ru.yandex.fixcolor.tests.spc.rs232.CommPort;
import ru.yandex.fixcolor.tests.spc.bd.usertypes.*;
import ru.yandex.fixcolor.tests.spc.bd.*;
import ru.yandex.fixcolor.tests.spc.lib.swing.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;

import static ru.yandex.fixcolor.tests.spc.lib.MyLogger.myLog;

public class StartFrame {
    static StartFrame startFrame;
    public interface CallBack {
        void messageCloseStartFrame(BaseData conn, String commPortName) throws Exception;
        void stopSystem();

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
    private JButton buttonShowArchive;
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
    private JButton buttonCalibration;
    //
    private JTable tableFindPushers;
    private JTable tableFindUsers;
    //
    private JLabel viewNameTypePusher;
    private JLabel viewForce;
    private JLabel viewMove;
    private JLabel viewUnclenching;
    private JLabel viewWeightNominal;
    //
    private JLabel viewLabelNameTypePusher;
    private JLabel viewLabelForce;
    private JLabel viewLabelMove;
    private JLabel viewLabelUnclenching;
    private JLabel viewLabelWeightNominal;

    private JButton buttonExit;

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
    // выбранный пользователь
    private User selectUser;
    // выбранный толкатель
    private Pusher selectPusher;

    CallBack callBack;
    public JFrame frame;
    private SelectComboBox2Table_Top<User> userSelectComboBox2Table;
    private SelectComboBox2Table_Top<Pusher> pusherSelectComboBox2Table;

    TypeBaseDate typeBaseDate;
    BaseData.Parameters parameters;
    BaseData connBD;
    SaveEnableComponents saveEnableComponentsStartFrame;
    // ==================================================================
    //                    comm port
    private CommPort commPort;
    // ==================================================================


    public static StartFrame main(boolean statMainWork, CallBack callBack, CommPort commPort) throws Exception {
        if (startFrame != null) throw new Exception("Повторное создание Start Frame");
        try {
            SwingUtilities.invokeAndWait(()->{
                startFrame = new StartFrame(statMainWork, callBack, commPort);
                new Thread(()-> startFrame.start(), "StartFrame start").start();
            });
        } catch (InterruptedException e) {
            myLog.log(Level.SEVERE, "ошибка создания startFrame", e);
            throw new Exception(e);
        }
        return startFrame;
    }

    protected StartFrame(boolean statMainWork, CallBack callBack, CommPort commPort) {
        // если основная программа работает, то ком порт нельзя проверять !!!!!!!!!!!!!!!!!!!!!!!
        this.statMainWork = statMainWork;
        this.callBack = callBack;
        this.commPort = commPort;
        commPort.ReciveStop();
    }


    private BaseData.Parameters getParametersBaseData(TypeBaseDate typeBaseDate) throws ParametersSqlException {
        if (typeBaseDate == null) { throw new ParametersSqlException("ошибка типа базы данных", Status.BASE_TYPE_NO_SELECT, null); }
        if (typeBaseDate == TypeBaseDate.ERROR) { throw new ParametersSqlException("ошибка типа базы данных", Status.BASE_TYPE_ERROR, null); }
        BaseData.Parameters parameters;
        try { parameters = BaseData.Parameters.create(typeBaseDate);
        } catch (BaseDataException e) { throw new ParametersSqlException(e, e.getStatus(), null);
        }
        Status result;
        // загрузка параметров БД
        try { result = parameters.load();
        } catch (BaseDataException e) { result = Status.ERROR;
        }
        //
        if (result != Status.OK) { throw new ParametersSqlException(
                "ошибка загрузка параметров соединения с БД: ",
                result,
                parameters);
        }
        //
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
        } catch (ParametersSqlException e) {
            myLog.log(Level.WARNING, "ошибка получения параметров подключения к БД", e);
            parameters = e.getParameters();
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
                buttonShowArchive,
                buttonCalibration,
                buttonExit,
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
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!statMainWork) {
                    callBack.stopSystem();
                    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    super.windowClosing(e);
                    System.exit(2);
                }
//                callBack.messageSetNewData();
                try {
                    callBack.messageCloseStartFrame(null, MainClass.getPortNameFromConfig());
                } catch (Exception exception) {
                    myLog.log(Level.SEVERE, "закрытие окна start frame", exception);
                }
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                frame.removeAll();
                frame.dispose();
                startFrame = null;
            }
        });
        // =================== загрузка начальных параметров ===================
        loadAndSetBeginParameters();
        pusherSelectComboBox2Table = new SelectComboBox2Table_Top<>(comboBoxPusher, tableFindPushers, listPushers, 7, null);
        userSelectComboBox2Table = new SelectComboBox2Table_Top<>(comboBoxUsers, tableFindUsers, listUsers, 7, "a");
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
                    () -> {
                        onInputComponents();
                        frame.requestFocus();
                    }
            );
            return;
        } else { onInputComponents();
        }
        loadAndSetBeginParameters2();
        userSelectComboBox2Table.setLock(false);
        pusherSelectComboBox2Table.setLock(false);
        // ********************
    }
    private void loadAndSetBeginParameters() {
        // загрузка параметров соединения с БД
        //------------------------------
        // чтение конфигурации
        BaseData.Config config = BaseData.Config.create();
        try { config.load();
        } catch (BaseDataException e) {
            myLog.log(Level.WARNING, "ошибка чтения файла конфигурации", e);
            config.setDefault();
        }
        // тип БД
        typeBaseDate = config.getTypeBaseData();
        // инициализация работы с БД и данных с ней связанных
        initBaseData(typeBaseDate);
        // *************************************************************************************
        // проверка ком порта
        flagAvailabilityCommPort = isCheckCommPort(MainClass.getPortNameFromConfig());
    }
    private void loadAndSetBeginParameters2() {
        // загрузка пользователей в комбо бокс
        try { MyUtil.loadToComboBox(listUsers, comboBoxUsers,false, null);
        } catch (Exception e) { myLog.log(Level.SEVERE, "Ошибка загрузки пользователей в comboboxUser", e);
        }
        // загрузка толкателей в комбо бокс
        try { MyUtil.loadToComboBox(listPushers, comboBoxPusher,false, null);
        } catch (Exception e) { myLog.log(Level.SEVERE, "Ошибка загрузки толкателей в comboboxUser", e);
        }
        /*if (statMainWork) {
            // здесь загрузка текущего пользователя и толкателя, если потребуется
        }*/
        // -------
    }

    private void initComponents() {
        frame = new JFrame();
        frame.setPreferredSize(new Dimension(640, 480));
        {
            label1 = CreateComponents.getLabel("Стенд", new Font("Times New Roman", Font.PLAIN, 57), 220, 130, 148, 66, false, true);
            label2 = CreateComponents.getLabel("испытания", new Font("Times New Roman", Font.PLAIN, 36), 210, 180, 227, 42, false, true);
            label3 = CreateComponents.getLabel("гидротолкателей", new Font("Times New Roman", Font.PLAIN, 36), 170, 210, 258, 42, false, true);
            label4 = CreateComponents.getLabel("Гумеров М.Н.", new Font("Times New Roman", Font.PLAIN, 11), 380, 400, 68, 20, false, true);
            label5 = CreateComponents.getLabel("ЦЗЛАМ ЛА", new Font("Times New Roman", Font.PLAIN, 16), 460, 400, 90, 19, false, true);
            frame.add(label1);
            frame.add(label2);
            frame.add(label3);
            frame.add(label4);
            frame.add(label5);
            //
            jLabel1 = CreateComponents.getLabel(frame, "Пользователь :", new Font("Times New Roman", Font.PLAIN, 14), 185, 154, false, true, MLabel.POS_RIGHT);
            jLabel2 = CreateComponents.getLabel(frame, "Пароль :", new Font("Times New Roman", Font.PLAIN, 14), 185, 194, false, true, MLabel.POS_RIGHT);
            jLabel3 = CreateComponents.getLabel(frame,"Толкатель :", new Font("Times New Roman", Font.PLAIN, 14), 185, 233,  false, true, MLabel.POS_RIGHT);
        } // подписи, надписи
        {
            comboBoxUsers = CreateComponents.getComboBox(new Font("Times New Roman", Font.PLAIN, 14),
                    190, 150, 350, 24, true,
                    null,
                    this::callSelectUser,
                    false, true);
            tableFindUsers = CreateComponents.getTable(200,
                    null,
                    null,
                    null,
                    null,
                    false,
                    true);
            tableFindUsers.setBounds(190, comboBoxUsers.getY() + comboBoxUsers.getHeight() + 2, 350, 30);
            //
            comboBoxPusher = CreateComponents.getComboBox(new Font("Times New Roman", Font.PLAIN, 14),
                    190, 230, 350, 24, true,
                    null,
                    this::callSelectPusher,
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
            //
            tableFindPushers.setBounds(190, comboBoxPusher.getY() + comboBoxPusher.getHeight() + 2, 350, 30);

            frame.add(comboBoxUsers);
            frame.add(tableFindUsers);
            frame.add(comboBoxPusher);
            frame.add(tableFindPushers);
            tableFindPushers.updateUI();
        } // селекторы
        {
            buttonEnter = CreateComponents.getButton(frame, "Проверка", new Font("Times New Roman", Font.PLAIN, 14), 320, 190, 90, 24, this::callEnter, false, true);
            buttonSetPassword = CreateComponents.getButton(frame, "Новый пароль", new Font("Times New Roman", Font.PLAIN, 14), 420, 190, 116, 24, this::callSetNewPassword, false, true);
            buttonWork = CreateComponents.getButton(frame, "Измерения", new Font("Times New Roman", Font.PLAIN, 14), 195, 330, 110, 24, this::callReturnToWork, false, true);
            buttonCalibration = CreateComponents.getButton(frame, "Калибровка", new Font("Times New Roman", Font.PLAIN, 14), 195, 370, 110, 24, this::callCalibration, false, false);
            buttonTuning = CreateComponents.getButton(frame, "настройка", new Font("Times New Roman", Font.PLAIN, 14), 195, 370, 110, 24, this::callTuning, false, true);
            buttonShowArchive = CreateComponents.getButton(frame, "Архив", new Font("Times New Roman", Font.PLAIN, 14), 80, 370, 90, 24, this::callShowArchive, false, false);
            buttonWork.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "WorkButtonEnter");
            buttonWork.getActionMap().put("WorkButtonEnter", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    callReturnToWork(e);
                }
            });

            JPanel panel = new JPanel();
            panel.setBounds(50, 50, 300, 300);

            //canvas = new Canvas();
        } // кнопки
        {
            jPanel1 = CreateComponents.getPanel(null, new Font("Times New Roman", Font.PLAIN, 12), "редактирование", 380, 320, 160, 90,true, true );
            // кнопка редактирования пользователей
            // кнопка редактирования толкателей
            buttonEditUsers = CreateComponents.getButton(jPanel1, "Пользователей", new Font("Times New Roman", Font.PLAIN, 14), 20, 20, 120, 24, this::callEditUsers, true, true);
            buttonEditPushers = CreateComponents.getButton(jPanel1, "Толкателей", new Font("Times New Roman", Font.PLAIN, 14), 20, 55, 120, 24, this::callEditPushers, true, true);

            frame.add(jPanel1);
            jPanel1.setVisible(false);
        } // панель редактирование
        {
            viewLabelNameTypePusher = CreateComponents.getLabel(frame, "Тип толкателя",
                    new Font("Time New Roman", Font.PLAIN, 14),
                    80, 260, false, true, MLabel.POS_CENTER);
            viewLabelForce = CreateComponents.getLabel(frame, "Ном.усилие(кг)",
                    new Font("Time New Roman", Font.PLAIN, 14),
                    208, 260, false, true, MLabel.POS_CENTER);
            viewLabelMove = CreateComponents.getLabel(frame, "Ном.ход (мм)",
                    new Font("Time New Roman", Font.PLAIN, 14),
                    330, 260, false, true, MLabel.POS_CENTER);
            viewLabelUnclenching = CreateComponents.getLabel(frame, "Время разж.(мс)",
                    new Font("Time New Roman", Font.PLAIN, 14),
                    460, 260, false, true, MLabel.POS_CENTER);
            viewLabelWeightNominal = CreateComponents.getLabel(frame, "Вес (кг)",
                    new Font("Time New Roman", Font.PLAIN, 14),
                    565, 260, false, true, MLabel.POS_CENTER);
            //
            viewNameTypePusher = CreateComponents.getLabel(frame, "",
                    new Font("Time New Roman", Font.PLAIN, 14),
                    80, 286, false, true, MLabel.POS_CENTER);
            viewForce = CreateComponents.getLabel(frame, "",
                    new Font("Time New Roman", Font.PLAIN, 14),
                    208, 286, false, true, MLabel.POS_CENTER);
            viewMove = CreateComponents.getLabel(frame, "",
                    new Font("Time New Roman", Font.PLAIN, 14),
                    330, 286, false, true, MLabel.POS_CENTER);
            viewUnclenching = CreateComponents.getLabel(frame, "",
                    new Font("Time New Roman", Font.PLAIN, 14),
                    460, 286, false, true, MLabel.POS_CENTER);
            viewWeightNominal = CreateComponents.getLabel(frame, "",
                    new Font("Time New Roman", Font.PLAIN, 14),
                    565, 286, false, true, MLabel.POS_CENTER);
            //
        }
        fieldPassword = CreateComponents.getTextField(CreateComponents.PASSWORDFIELD, new Font("Times New Roman", Font.PLAIN, 14), 190, 190,120, 24, null, this::callEnter, false, true);
        frame.add(fieldPassword);

        buttonExit = CreateComponents.getButton(frame, "Выход", new Font("Times New Roman", Font.PLAIN, 14),
                80, 330, 90, 24, this::callExit, false, true);

        frame.pack();
    }

    private void callExit(ActionEvent event) {
        callBack.stopSystem();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.removeAll();
        frame.dispose();
        System.exit(3);
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
        buttonExit.setVisible(true);
        buttonShowArchive.setVisible(true);
        //
        buttonCalibration.setVisible(true);
        buttonTuning.setEnabled(true);
        buttonSetPassword.setEnabled(false);
        buttonEditUsers.setEnabled(false);
        buttonEditPushers.setEnabled(false);
        //
        viewNameTypePusher.setVisible(true);
        viewLabelNameTypePusher.setVisible(true);
        viewForce.setVisible(true);
        viewLabelForce.setVisible(true);
        viewMove.setVisible(true);
        viewLabelMove.setVisible(true);
        viewUnclenching.setVisible(true);
        viewWeightNominal.setVisible(true);
        viewLabelUnclenching.setVisible(true);
        viewLabelWeightNominal.setVisible(true);
        //
        if (statMainWork) {
            buttonWork.setEnabled(true);
            comboBoxPusher.setEnabled(true);
        } else {
            buttonWork.setEnabled(false);
            comboBoxPusher.setEnabled(false);
        }
    }
    // проверка встроенного администратор
    private boolean checkIntegratedAdministrator(String surName, String password) {
//        return  surName.equals("Doc") && password.equals("aUxPMjIzNjA=");
        return  surName.equals("a") && password.equals("");
    }
    // разрешение кнопки работа
    private boolean permissionWork() {
        // флаг целостности структуры БД
        if (!flagStructureIntegrity) return true;
        // проверка доступности ком порта
        if (!flagAvailabilityCommPort) return true;
        // список пользователей / = [0] for false
        if (listUsers.length == 0) return true;
        // список толкателей / = [0] for false
        if (listPushers.length == 0) return true;
        return false;
    }
    // ======================================================
    // обработка ввод
    private void callEnter(ActionEvent e) {
        //User selectUser;
        String surName;
        String password;
        try {
            selectUser = (User) comboBoxUsers.getSelectedItem();
        } catch (ClassCastException e2) {
            selectUser = null;
        }
        password = fieldPassword.getText();
        if (selectUser == null) {
            surName = (String) comboBoxUsers.getSelectedItem();
            if (surName == null) {
                MySwingUtil.showMessage(frame, "ошибка", "пользователь не назначен", 5_000, ()-> {
                    buttonEnter.setEnabled(true);
                    frame.requestFocus();
                });
                myLog.log(Level.WARNING, "попытка входа локальным админом");
                return;
            }
            String pass = BaseData.Password.encoding(password);
            // проверка на локального админа
            if (!checkIntegratedAdministrator(surName, pass)) {
                buttonEnter.setEnabled(false);
                buttonOffForErrorPass();
                MySwingUtil.showMessage(frame, "ошибка", "пароль не верен", 5_000, ()-> {
                    buttonEnter.setEnabled(true);
                    frame.requestFocus();
                });
                myLog.log(Level.FINE, "попытка входа локальным админом: " + surName + "/" + password);
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
        if (!selectUser.userPassword.equals(password)) {
            myLog.log(Level.FINE, " у пользователя из списка не совпал пароль"); //(" + selectUser.userPassword + ")");
            // отключить кнопки управления
            buttonOffForErrorPass();
            saveEnableComponentsStartFrame.save();
            saveEnableComponentsStartFrame.offline();
            myLog.log(Level.INFO, "ошибка ввода пароля: " + selectUser.surName + "/" + password);
            MySwingUtil.showMessage(frame, "ошибка", "пароль не верен", 5_000, ()-> {
                saveEnableComponentsStartFrame.restore();
                frame.requestFocus();
            });
            return;
        }
        myLog.log(Level.INFO, "вход пользователем " + selectUser.surName + " с привелегиями " + selectUser.rang);
        // разрешение смены пароля
        fieldPassword.setText("");
        buttonSetPassword.setEnabled(true);
        // разрешение на редактирование пользователей
        buttonEditUsers.setEnabled((selectUser.rang & (1 << User.RANG_USERS)) != 0);
        // разрешение на редактирование толкателей
        buttonEditPushers.setEnabled((selectUser.rang & (1 << User.RANG_PUSHERS)) != 0);
        buttonCalibration.setEnabled((selectUser.rang & (1 << User.RANG_PUSHERS)) != 0);
        // разрешение кнопки работа
        buttonWork.setEnabled(true);
        // разрешение выбора толкателей
        comboBoxPusher.setEnabled(true);
        //
        buttonShowArchive.setEnabled(true);
        buttonWork.requestFocus();
    }
    private void buttonOffForErrorPass() {
        buttonTuning.setVisible(false);
        buttonWork.setEnabled(false);
        buttonEditPushers.setEnabled(false);
        buttonEditUsers.setEnabled(false);
        buttonCalibration.setEnabled(false);
        buttonSetPassword.setEnabled(false);
        buttonShowArchive.setEnabled(false);
    }
    // обработка новый пароль
    private void callSetNewPassword(ActionEvent f) {
        User currentUser = (User) comboBoxUsers.getSelectedItem();
        if (currentUser == null) {
            MySwingUtil.showMessage(frame, "установка нового пароля", "пользователь не выбран", 5_000, () -> {
                buttonSetPassword.setEnabled(true);
                frame.requestFocus();
            });
            buttonSetPassword.setEnabled(false);
            myLog.log(Level.WARNING, "попытка установки пароля пустым пользователем ");
            return;
        }
        String newPassword = fieldPassword.getText();
        if  (newPassword.length() == 0) {
            MySwingUtil.showMessage(frame, "установка нового пароля", "новый пароль пустой !!!", 5_000, () -> {
                buttonSetPassword.setEnabled(true);
                frame.requestFocus();
            });
            buttonSetPassword.setEnabled(false);
            myLog.log(Level.WARNING, "попытка установки пустово пароля пользователем " + currentUser.surName );
            return;
        }
        try {
            connBD.setNewUserPassword(currentUser.id_loggerUser, currentUser, newPassword);
            currentUser.userPassword = newPassword;
        } catch (Exception e) {
            MySwingUtil.showMessage(frame, "установка нового пароля", "ошибка установки нового пароля", 5_000, () -> {
                buttonSetPassword.setEnabled(true);
                frame.requestFocus();
            });
            buttonSetPassword.setEnabled(false);
            myLog.log(Level.SEVERE, "ошибка установки нового пароля", e);
        }
        fieldPassword.setText("");
    }
    // обработка выбора пользователя
    private void callSelectUser(ActionEvent actionEvent) {
        //if (e.getStateChange() == ItemEvent.SELECTED) return;
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
        if (permissionWork()) {
            MySwingUtil.showMessage(frame, "ошибка", "нет готовности системы", 5_000);
            myLog.log(Level.INFO, "нет готовности системы");
            return;
        }
        // ------------
        if (selectUser == null) {
            MySwingUtil.showMessage(frame, "ошибка", "не выбран пользователь", 5_000);
            myLog.log(Level.INFO, "не выбран пользователь");
            return;
        }
        if (selectPusher == null) {
            MySwingUtil.showMessage(frame, "ошибка", "не выбран толкатель", 5_000);
            myLog.log(Level.INFO, "не выбран толкатель");
            return;
        }
        long id_user = selectUser.id_user;
        long id_pusher = selectPusher.id_pusher;
        DataSpec dataSpec;
        try { dataSpec = connBD.getLastDataSpec();
        } catch (BaseDataException b) {
            MySwingUtil.showMessage(frame, "параметры работы", "ошибка доступа к БД", 5_000);
            myLog.log(Level.SEVERE, "ошибка доступа к БД", b);
            return;
        }
        if (dataSpec == null || dataSpec.id_user != id_user || dataSpec.id_pusher != id_pusher) {
            try {
                connBD.writeDataSpec(id_user, id_pusher);
            } catch (BaseDataException b) {
                MySwingUtil.showMessage(frame, "параметры работы", "ошибка доступа к БД", 5_000);
                myLog.log(Level.SEVERE, "ошибка доступа к БД", b);
                return;
            }
        }
        frame.removeAll();
        frame.dispose();
        try {
            if (statMainWork) {
//            callBack.messageSetNewData();
                callBack.messageCloseStartFrame(null, MainClass.getPortNameFromConfig());
            } else {
                callBack.messageCloseStartFrame(connBD, MainClass.getPortNameFromConfig());
            }
        } catch (Exception exception) {
            myLog.log(Level.SEVERE, "переход в режим работа", exception );
        }
        startFrame = null;
    }
    // просмотр архива
    private void callShowArchive(ActionEvent e) {
        saveEnableComponentsStartFrame.save();
        saveEnableComponentsStartFrame.offline();
        new Thread(()->{
            SwingUtilities.invokeLater(()->{
                new ViewArchive(new ViewArchive.CallBack() {
                    @Override
                    public void closeArchive() {
                        saveEnableComponentsStartFrame.restore();
                    }
                }, connBD);
            });
        }, "Show Archive").start();
    }
    // обработка настройка
    private void callTuning(ActionEvent e) {
        if (statMainWork) {
            // при основной работе нельзя менять параметры БД и порта
            MySwingUtil.showMessage(frame, "Настройка", "при основной работе нельзя менять параметры БД и порта", 10_000);
            buttonTuning.setVisible(false);
            return;
        }
        // отключение управления
        saveEnableComponentsStartFrame.save();
        saveEnableComponentsStartFrame.offline();
        userSelectComboBox2Table.setLock(true);
        new Thread(() -> SwingUtilities.invokeLater(() -> new TuningFrame(newData -> {
            saveEnableComponentsStartFrame.restore();
            loadAndSetBeginParameters();
            loadAndSetBeginParameters2();
            userSelectComboBox2Table.setLock(false);
            frame.requestFocus();
        })), "create tuning").start();
    }
    // обработка калибровка
    private void callCalibration(ActionEvent e) {
        // отключение управления
        saveEnableComponentsStartFrame.save();
        saveEnableComponentsStartFrame.offline();
        userSelectComboBox2Table.setLock(true);
        new Thread(() -> {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    Calibration.init(new Calibration.CallBack() {
                        @Override
                        public void messageClose() {
                            saveEnableComponentsStartFrame.restore();
                            frame.requestFocus();
                        }
                    }, commPort
                            );
                }
            });
        }, "create calibration").start();
    }
    // обработка редактирование пользователей
    private void callEditUsers(ActionEvent e) {
        saveEnableComponentsStartFrame.save();
        saveEnableComponentsStartFrame.offline();
        new Thread(() -> SwingUtilities.invokeLater(() -> new EditUsers(connBD,
                new EditUsers.CallBack() {
                    @Override
                    public void messageCloseEditUsers(boolean newData) {
                        if (newData) {
                            // **** здесь перезагрузка списка пользователей
                            // чтение списка пользователей
                            try {
                                listUsers = connBD.getListUsers(true);
                                //userSelectComboBox2Table = new SelectComboBox2Table_Top<>(comboBoxUsers, tableFindUsers, listUsers, 7, "a");
                                userSelectComboBox2Table.setCollections(listUsers);
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
                            try {
                                userSelectComboBox2Table.setLock(true);
                                MyUtil.loadToComboBox(listUsers, comboBoxUsers,false, null);
                            } catch (Exception e) {
                                myLog.log(Level.SEVERE, "Ошибка загрузки пользователей в comboboxUser", e);
                                MySwingUtil.showMessage(
                                        frame,
                                        "обновление списка пользователей",
                                        "ошибка обновления - требуется вмешательство администратора",
                                        10_000
                                );
                            } finally {
                                userSelectComboBox2Table.setLock(false);
                            }
                        }
                        saveEnableComponentsStartFrame.restore();
                        frame.requestFocus();
                    }

                    @Override
                    public User getCurrentUser() {
                        return (User) comboBoxUsers.getSelectedItem();
                    }
                })), "create edit users").start();
    }
    // обработка редактирование толкателей
    private void callEditPushers(ActionEvent e) {
        saveEnableComponentsStartFrame.save();
        saveEnableComponentsStartFrame.offline();
        pusherSelectComboBox2Table.setLock(true);
        long curPusher;
        try {
            curPusher = ((Pusher) comboBoxPusher.getSelectedItem()).id_pusher;
        } catch (Exception x) {
            curPusher = 0;
        }
        long finalCurPusher = curPusher;
        new Thread(() -> SwingUtilities.invokeLater(() -> {
            try {
                EditPushers editPushers = new EditPushers(
                        newData -> {
                            if (newData) {
                                // чтение списка толкателей
                                try {
                                    listPushers = connBD.getListPushers(true);
                                    pusherSelectComboBox2Table.setLock(true);
                                    MyUtil.loadToComboBox(listPushers, comboBoxPusher, false, null);
                                    selectIdPusher(comboBoxPusher, finalCurPusher);
                                    pusherSelectComboBox2Table.setLock(false);
                                    callSelectPusher(null);
                                } catch (Exception e1) { myLog.log(Level.WARNING, "ошибка чтение списка толкателей с БД", e1);
                                }
                            }
                            saveEnableComponentsStartFrame.restore();
                            frame.requestFocus();
                            pusherSelectComboBox2Table.setLock(false);
                        },
                        connBD,
                        ((User) comboBoxUsers.getSelectedItem()).id_loggerUser
                );
            } catch (BaseDataException bde) {
                myLog.log(Level.SEVERE, "ошибка редактирования толкателей", bde);
                MySwingUtil.showMessage(frame, "редактор толкателей", "ошибка редактирования толкателей",
                        5_000, () -> {
                            saveEnableComponentsStartFrame.restore();
                            frame.requestFocus();
                            pusherSelectComboBox2Table.setLock(false);
                        }
                );
            }
        }), "create edit pushers").start();
    }
    //
    private void callSelectPusher(ActionEvent actionEvent) {
        selectPusher = (Pusher) comboBoxPusher.getSelectedItem();
        if (selectPusher == null) return;
        TypePusher typePusher = selectPusher.loggerPusher.typePusher;
        if (typePusher == null) return;
        viewNameTypePusher.setText(typePusher.loggerTypePusher.nameType);
        viewForce.setText(String.valueOf(typePusher.loggerTypePusher.forceNominal));
        viewMove.setText(String.valueOf(typePusher.loggerTypePusher.moveNominal));
        viewUnclenching.setText(String.valueOf(typePusher.loggerTypePusher.unclenchingTime));
        viewWeightNominal.setText(String.valueOf(typePusher.loggerTypePusher.weightNominal));
    }
    private void selectIdPusher(JComboBox<Pusher> comboBox, final long id) {
        Pusher pusher;
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            pusher = comboBox.getItemAt(i);
            if (pusher.id_pusher == id) {
                comboBox.setSelectedItem(pusher);
                break;
            }
        }
    }
    // ===========================================================================
}
