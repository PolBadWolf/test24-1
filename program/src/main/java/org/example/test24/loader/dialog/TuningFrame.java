package org.example.test24.loader.dialog;

import org.example.test24.bd.*;
import org.example.test24.RS232.BAUD;
import org.example.test24.RS232.CommPort;
import org.example.test24.bd.usertypes.User;
import org.example.test24.lib.swing.CreateComponents;
import org.example.test24.lib.swing.MyUtil;
import org.example.test24.lib.swing.SaveEnableComponents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;

import static org.example.test24.lib.MyLogger.myLog;

class TuningFrame {

    interface CallBack {
        void messageCloseTuning(boolean newData);
    }

    CallBack callBack;
    BaseData.Config configProg;
    BaseData.Parameters parametersSql;
    BaseData connBD;

    private SaveEnableComponents saveEnableComponents;

    // ************** флаги ************
    // соединение с БД установлено
    boolean flagConnectBD;
    // список доступный баз в БД
    String[] listBaseBD;
    // блокировка работы компонентов управления при начальной установке
    boolean flagLockActions = false;
    boolean flagTestBaseData;
    boolean flagTestCommPort;
    boolean flagNewCorrectData;


    private CommPort.PortStat chCheckCommPort = CommPort.PortStat.INITCODE_NOTEXIST;
    private boolean flCheckParamSql = false;
    //private boolean flCheckListBd = false;

    // =============================================================================================================
    protected TuningFrame(CallBack callBack)
    {
        this.callBack = callBack;
        boolean flInit = true;
        flagTestBaseData = false;
        flagTestCommPort = false;
        flagNewCorrectData = false;
        // загрузка параметров
        configProg = loadConfigProg();
        try {
            parametersSql = loadParametersSql(configProg.getTypeBaseData());
        } catch (Exception e) {
            parametersSql = ((ParametersSqlException) e).getParameters();
            myLog.log(Level.WARNING, "загрузка параметров", e);
            flInit = false;
        }
        resetFlagsConnect();
        // установка соединения
        if (flInit) {
            try {
                connBD = initConnect(parametersSql);
                // флаг установки соединения
                flagConnectBD = true;
            } catch (Exception e) {
                myLog.log(Level.WARNING, "загрузка списка баз", e);
                flagConnectBD = false;
            }
        } else {
            flagConnectBD = false;
        }
        // загрузка списка баз
        if (flagConnectBD) {
            try {
                listBaseBD = connBD.getListBase();
            } catch (Exception e) {
                myLog.log(Level.WARNING, "загрузка списка баз", e);
                listBaseBD = new String[0];
            }
        } else {
            listBaseBD = new String[0];
        }
        //lockBegin = true;
        // конструктор окна
        frameConstructor();

        // установка компонентов в начальное положение
        setComponentsBegin();
        //lockBegin = false;
        //
        saveEnableComponents = new SaveEnableComponents(new Component[]{
                frameTuning,
                comboBoxCommPort,
                comboBoxTypeBd,
                comboBoxListBd,
                fieldParamServerIP,
                fieldParamServerPort,
                fieldParamServerLogin,
                fieldParamServerPassword,
                buttonOk,
                buttonSave,
                buttonTest,
                buttonEditUsers,
                buttonEditPushers
        });
    }
    // =============================================================================================================
    // загрузка параметров конфигурации программы
    private BaseData.Config loadConfigProg() {
        BaseData.Config config = BaseData.Config.create();
        Status result;
        try {
            result = config.load1();
        } catch (Exception e) {
            myLog.log(Level.WARNING, "ошибка чтения файла конфигурации", e);
            config.setDefault();
            result = Status.CONFIG_LOAD_ERROR;
        }
        if (result != Status.OK) {
            myLog.log(Level.SEVERE, "загрузка параметров конфигурации программы", new Exception(result.toString()));
            config.setDefault();
        }
        return config;
    }
    // =============================================================================================================
    // загрузка параметров подключения к БД
    private BaseData.Parameters loadParametersSql(TypeBaseDate typeBaseDate) throws BaseDataException
    {
        if (typeBaseDate == null) throw new BaseDataException("не задан тип БД", Status.BASE_TYPE_NO_SELECT);
        if (typeBaseDate == TypeBaseDate.ERROR)  throw new BaseDataException("ошибочный тип БД", Status.BASE_TYPE_ERROR);
        //
        BaseData.Parameters parameters = BaseData.Parameters.create(typeBaseDate);
        Status result;
        result = parameters.load();
        if (result != Status.OK) {
            throw new BaseDataException("загрузка параметров соединения с БД", new Exception(result.toString()), Status.PARAMETERS_ERROR);
        }
        //
        return parameters;
    }
    // =============================================================================================================
    // подключение к БД
    private BaseData connectBD(BaseData.Parameters parametersSql) throws BaseDataException {
        BaseData baseData;
        try {
            baseData = BaseData.create(parametersSql);
        } catch (BaseDataException e) {
            throw new BaseDataException("соединение с БД", e, e.getStatus());
        }
        //
        try {
            baseData.openConnect(parametersSql);
        } catch (BaseDataException e) {
            throw new BaseDataException("соединение с БД", e, e.getStatus());
        }
        return baseData;
    }
    // =============================================================================================================
    //
    private void resetFlagsConnect() {
        // флаг соединения с БД
        flagConnectBD = false;
        // список доступных баз
        listBaseBD = new String[0];
    }
    //
    private BaseData initConnect(BaseData.Parameters parametersSql) throws Exception {
        BaseData baseData;
        try {
            baseData = connectBD(parametersSql);
        } catch (Exception e) {
            throw new Exception("инициализация соединение с БД", e);
        }
        return baseData;
    }
    // =============================================================================================================
    // установка компонентов в начальное положение
    private void setComponentsBegin() {
        flagLockActions = true;
        // ком порт
        setComponentCommPort(CommPort.getListPortsName(), configProg.getPortName());
        //labelPortCurrent.setText(commPortName);
        textCommPortStatus.setText("");
//        // БД
        setComponentBaseData(parametersSql);
        //textTypeBdStatus.setText(parametersSql.getTypeBaseDate().toString());
//        // список БД
        try { MyUtil.loadToComboBox(listBaseBD, comboBoxListBd, false, parametersSql.getDataBase()); } catch (Exception e) {
            myLog.log(Level.WARNING, "начальная инициализация компонентов", e);
        }
//        //
//        // установка начального состояния кнопок по основным параметрам
        buttonTest.setEnabled(true);
        flagLockActions = false;
    }
    private void setComponentCommPort(String[] listCommPort, String defaultCommPort) {
        comboBoxCommPort.removeAllItems();
        Arrays.stream(listCommPort).sorted(String::compareTo).forEach(s -> comboBoxCommPort.addItem(s));
        comboBoxCommPort.setSelectedItem(defaultCommPort);
    }
    private void setComponentBaseData(BaseData.Parameters parametersSql) {
        // тип БД
        comboBoxTypeBd.setSelectedItem(parametersSql.getTypeBaseDate());
//        // параметры подключения
        fieldParamServerIP.setText(parametersSql.getIpServer());
        fieldParamServerPort.setText(parametersSql.getPortServer());
        fieldParamServerLogin.setText(parametersSql.getUser());
        fieldParamServerPassword.setText(parametersSql.getPassword());
    }
    private void frameConstructor() {
        /*frameTuning = getFrameTuning("настройка", 640, 480);
        frameTuning.setResizable(false);*/
        frameTuning = CreateComponents.getFrame("настройка", 640, 480, false, null, new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                callBack.messageCloseTuning(true);
            }
        });
        Container container = frameTuning.getContentPane();
        {
            panelCommPort = CreateComponents.getPanel(null, new Font("Times New Roman", Font.BOLD, 14),
                    "выбор Comm порта", 10, 10, 170, 110, true, true);
            container.add(panelCommPort);
            //
            panelCommPort.add(CreateComponents.getLabel("текщий порт: ", new Font("Tahoma", Font.BOLD, 12),
                    6, 10, 100, 30, true, true));
            //
            labelPortCurrent = CreateComponents.getLabel("", new Font("Times New Roman", Font.PLAIN, 14),
                    80, 15, 100, 30, true, true);
            panelCommPort.add(labelPortCurrent);
            //
            comboBoxCommPort = CreateComponents.getComboBox(new Font("Dialog", Font.BOLD, 12),
                    6, 50, 150, 20, false,
                    null,
                    this::callSelectCommPort,
                    true,
                    true
            );
            panelCommPort.add(comboBoxCommPort);
            //
            textCommPortStatus = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Dialog", Font.BOLD, 12),
                    6, 80, 150, 20,
                    null,
                    null,
                    true,
                    true,
                    false);
            panelCommPort.add(textCommPortStatus);
        } // Comm Port
        {
            panelTypeBd = CreateComponents.getPanel(null, new Font("Tahoma", Font.BOLD, 12),
                    "выбор Базы данных ", 180, 10, 190, 110, true, true);
            container.add(panelTypeBd);

            panelTypeBd.add(CreateComponents.getLabel("тип базы данных: ", new Font("Tahoma", Font.BOLD, 12),
                    10,10, 140, 30, true, true));

            //comboBoxTypeBd = getComboBoxTypeBd(6, 50, 150, 20); // callSelectTypeBase(comboBox);
            comboBoxTypeBd = CreateComponents.getComboBox(new Font("Dialog", Font.BOLD, 11),
                    6, 50, 150, 20, false, null, this::callSelectTypeBase, true, true);
            flagLockActions = true;
            comboBoxTypeBd.addItem(TypeBaseDate.MS_SQL);
            comboBoxTypeBd.addItem(TypeBaseDate.MY_SQL);
            flagLockActions = false;
            panelTypeBd.add(comboBoxTypeBd);

            textTypeBdStatus = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Dialog", Font.BOLD, 12),
                    6, 80, 150, 20,
                    null,
                    null,
                    true,
                    true,
                    false);
            panelTypeBd.add(textTypeBdStatus);
        } // Type Base
        {
            panelParamSQL = CreateComponents.getPanel(null, new Font("Tahoma", Font.BOLD, 12),
                    "параметры подключения", 10, 130, 360, 200, true, true);
            container.add(panelParamSQL);

            panelParamSQL.add(CreateComponents.getLabel("ip адрес сервера: ", new Font("Tahoma", Font.BOLD, 13),
                    6, 10, 140, 30, true, true));
            fieldParamServerIP = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Dialog", Font.PLAIN, 13),
                    160, 15, 140, 20, null, null, true, true);
            panelParamSQL.add(fieldParamServerIP);

            panelParamSQL.add(CreateComponents.getLabel("порт: ", new Font("Tahoma", Font.BOLD, 12),
                    6, 33, 140, 30, true, true));
            fieldParamServerPort = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Dialog", Font.PLAIN, 13),
                    160, 39, 140, 20, null, null, true, true);
            panelParamSQL.add(fieldParamServerPort);

            panelParamSQL.add(CreateComponents.getLabel("логин: ", new Font("Tahoma", Font.BOLD, 12),
                    6, 57, 140, 30, true, true));
            fieldParamServerLogin = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Dialog", Font.PLAIN, 13),
                    160, 63, 140, 20, null, null, true, true);
            panelParamSQL.add(fieldParamServerLogin);

            panelParamSQL.add(CreateComponents.getLabel("пароль: ", new Font("Tahoma", Font.BOLD, 12),
                    6, 82, 140, 30, true, true));
            fieldParamServerPassword = CreateComponents.getTextField(CreateComponents.PASSWORDFIELD, new Font("Dialog", Font.PLAIN, 13),
                    160, 88, 140, 20, null, null, true, true);
            panelParamSQL.add(fieldParamServerPassword);

            panelParamSQL.add(CreateComponents.getLabel("база даных: ", new Font("Tahoma", Font.BOLD, 12),
            6, 108, 140, 30, true, true));
            comboBoxListBd = CreateComponents.getComboBox(new Font("Dialog", Font.PLAIN, 12), 160, 112, 140, 20, true,
                    null, this::callSelectBaseData, true, true);
            panelParamSQL.add(comboBoxListBd);

            buttonOk = CreateComponents.getButton("Ok", new Font("Dialog", Font.BOLD, 12),
                    16, 140, 80, 30,
                    null,
                    true,
                    false);
            panelParamSQL.add(buttonOk);

            buttonSave = CreateComponents.getButton("Сохранить", new Font("Dialog", Font.BOLD, 12),
                    103, 140, 110, 30,
                    this::callPushButtonSave,
                    true,
                    false);
            panelParamSQL.add(buttonSave);

            buttonTest = CreateComponents.getButton("Тест", new Font("Dialog", Font.BOLD, 12),
                    220, 140, 80, 30,
                    this::callPushButtonTest,
                    true,
                    false);
            panelParamSQL.add(buttonTest);
        } // Parameters Base
        {
            panelSelectEdit = CreateComponents.getPanel(null, new Font("Times New Roman", Font.BOLD, 14),
                    "редактирование", 10, 340, 360, 80, true, true);
            container.add(panelSelectEdit);

            buttonEditUsers = CreateComponents.getButton("Пользователи", new Font("Dialog", Font.BOLD, 12),
                    16, 30, 140, 30,
                    this::callPushButtonEditUsers,
                    true,
                    false);
            panelSelectEdit.add(buttonEditUsers);

            buttonEditPushers = CreateComponents.getButton("Толкатели", new Font("Dialog", Font.BOLD, 12),
                    200, 30, 140, 30,
                    null,
                    true,
                    false);
            panelSelectEdit.add(buttonEditPushers);
        } // Select Edit
        frameTuning.pack();
        frameTuning.setResizable(false);
        frameTuning.setVisible(true);
    }
    private JFrame getFrameTuning(String title, int width, int height) {
        JFrame frame = new JFrame(title);
        Dimension size = new Dimension(width, height);
        frame.setSize(size);
        frame.setPreferredSize(size);
        frame.setLayout(null);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
/*                if (editUsers != null) {
                    editUsers.closeFromParent();
                }*/
                e.getWindow().removeAll();
                closeFrame();
            }
        });
        return frame;
    }

    private JComboBox<TypeBaseDate> getComboBoxTypeBd(int x, int y, int width, int height) {
        JComboBox<TypeBaseDate> comboBox = new JComboBox<>();
        comboBox.setBounds(x, y, width, height);
        comboBox.addItem(TypeBaseDate.MS_SQL);
        comboBox.addItem(TypeBaseDate.MY_SQL);
        comboBox.addActionListener(e -> {
            callSelectTypeBase(e);
        });
        return comboBox;
    }
    // закрытие окна
    private void closeFrame() {
        if (frameTuning != null) {
            try {
                frameTuning.getContentPane().removeAll();
                frameTuning.removeAll();
                frameTuning.setVisible(false);
                frameTuning.dispose();
                frameTuning = null;
            } catch (java.lang.Throwable e) {
                System.out.println(e.getMessage());
            }
            callBack.messageCloseTuning(flagNewCorrectData);
        }
    } // ****************
    // ======
    // статус основных параметров
    private void checkStatusComp() {
        {
            String portName = (String) comboBoxCommPort.getSelectedItem();
            CommPort commPort = null; //callBack MC.getCommPort();
            CommPort.PortStat ch = commPort.open(null, portName, BAUD.baud57600);
            if (ch == CommPort.PortStat.INITCODE_OK) {
                commPort.close();
            }
            chCheckCommPort = ch;
        }   // статус ком порта
        {
            // тип текущей БД
            String typeBd = (String) comboBoxTypeBd.getSelectedItem();
            //загрузка параметров с выбранным типом
            /*parametersSql = new ParametersSql2(
                    BaseData1.getNameFileParametrsSql(
                            typeBd,
                            callBack MC.getFilesNameSql()
                    )
            );*/
            try {
                //parametersSql.load();
                // параметры БД удачно прочитались
                flCheckParamSql = true;
            } catch (Exception e) {
                System.out.println("ошибка чтения параметров SQL: " + e.getMessage());
                // параметры БД плохие
                flCheckParamSql = false;
                // структура плохая
                //flCheckSql = false;
            }
            // доступ к БД
            try {
                /*flCheckSql = BaseData1.testStuctBase(
                        (String) comboBoxTypeBd.getSelectedItem(),
                        fieldParamServerIP.getText(),
                        fieldParamServerPort.getText(),
                        fieldParamServerLogin.getText(),
                        fieldParamServerPassword.getText(),
                        (String) comboBoxListBd.getSelectedItem()
                );*/
            } catch (java.lang.Throwable e) {
                System.out.println("ошибка параметров SQL: " + e.getMessage());
                //flCheckSql = false;
            }
        }   // структура БД
    }
    private boolean checkStatusFile() {
        {
            String portName = (String) comboBoxCommPort.getSelectedItem();
            CommPort commPort = null; //callBack MC.getCommPort();
            CommPort.PortStat ch = commPort.open(null, portName, BAUD.baud57600);
            if (ch == CommPort.PortStat.INITCODE_OK) {
                commPort.close();
            }
            chCheckCommPort = ch;
        }   // статус ком порта
        {
            // тип текущей БД
            //String typeBd = "";
//            String typeBd = parameters[0];
            //String typeBd = callBack MC.loadConfigTypeBaseData().getTypeBaseDataString();
            //загрузка параметров с выбранным типом
            /*parametersSql = new ParametersSql2(
                    BaseData1.getNameFileParametrsSql(
                            typeBd,
                            callBack MC.getFilesNameSql()
                    )
            );*/
            boolean flCheckParamSql;
            try {
                //parametersSql.load();
                // параметры БД удачно прочитались
                flCheckParamSql = true;
            } catch (Exception e) {
                System.out.println("ошибка чтения параметров SQL: " + e.getMessage());
                // параметры БД плохие
                flCheckParamSql = false;
                // структура плохая
                //flCheckSql = false;
            }
            // доступ к БД
            if (flCheckParamSql) {
                try {
                    /*flCheckSql = BaseData1.testStuctBase(
                            (String) comboBoxTypeBd.getSelectedItem(),
                            fieldParamServerIP.getText(),
                            fieldParamServerPort.getText(),
                            fieldParamServerLogin.getText(),
                            fieldParamServerPassword.getText(),
                            (String) comboBoxListBd.getSelectedItem()
                    );*/
                } catch (java.lang.Throwable e) {
                    System.out.println("ошибка параметров SQL: " + e.getMessage());
                    //flCheckSql = false;
                }
            } else {
                //flCheckSql = false;
            }
        }   // структура БД
        return false;
    }
    // выдача статуса основных параметров
    private void outStatus() {
        {
            String portName, statusText;
            switch (chCheckCommPort.getCodePortStat()) {
                case CommPort.INITCODE_OK:
                    portName = (String) comboBoxCommPort.getSelectedItem();
                    statusText = "ok";
                    break;
                case CommPort.INITCODE_NOTEXIST:
//                    portName = parameters[1];
                    portName = ""; //callBack MC.loadConfigCommPort();
                    statusText = "порт не найден";
                    break;
                case CommPort.INITCODE_ERROROPEN:
//                    portName = parameters[1];
                    portName = ""; //callBack MC.loadConfigCommPort();
                    statusText = "ошибка открытия";
                    break;
                default:
                    portName = "";
                    statusText = "";
            }
            labelPortCurrent.setText(portName);
            textCommPortStatus.setText(statusText);
        }   // выдача статуса comm port
        {
            if (!flCheckParamSql) {
                textTypeBdStatus.setText("Error parametrs BD");
                return;
            }
            /*if (flCheckSql) {
                textTypeBdStatus.setText("BD ok");
            } else {
                textTypeBdStatus.setText("Error structure BD");
            }*/
        }   // выдача статуса БД
    }
    // >>>>>>>>>>>>>>>>>>>>>>
    // разрешение кнопки тест
    protected void buttonTestEnable(boolean enabled) {
        buttonTest.setEnabled(enabled);
    }
    protected void buttonEditEnable(boolean enabled) {
        buttonEditUsers.setEnabled(enabled);
        buttonEditPushers.setEnabled(enabled);
    }
    // <<<<<<<<<<<<<<<<<<<<<<
    // выбран тип БД
    private void selectTypeBase(JComboBox comboBox) {
        //if (lockBegin)  return;
        checkStatusComp();
        outStatus();
        //сохранить
        if (flCheckParamSql) {
            //callBack MC.saveConfigTypeBaseData(BaseData2.typeBaseDataCode((String) comboBox.getSelectedItem()));
        }
    }
    // нажатие кнопки редактирование пользователей
    private void callPushButtonEditUsers(ActionEvent actionEvent) {
        if (!flagTestBaseData) {
            myLog.log(Level.SEVERE, "не установлен флаг коррекности БД");
            buttonEditUsers.setEnabled(false);
            return;
        }
        saveEnableComponents.save();
        saveEnableComponents.offline();
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                new EditUsers(
                        connBD.cloneNewBase((String) comboBoxListBd.getSelectedItem()),
                        new EditUsers.CallBack() {
                            @Override
                            public void messageCloseEditUsers(boolean newData) {
                                if (newData) {
                                    // здесь перезагрузка списка пользователей (новые данные)
                                }
                                saveEnableComponents.restore();
                                frameTuning.requestFocus();
                            }
                            // текущий активный пользователь
                            @Override
                            public User getCurrentUser() {
                                User user = new User(
                                        0,
                                        new Date(),
                                        0,
                                        new Date(),
                                        0,
                                        "lockAdmin",
                                        "",
                                        3,
                                        null
                                );
                                return user;
                            }
                        });
            });
        }, "create edit users").start();
    }
    // ========================================================================
    // ===== компоненты JFrame =======
    protected JFrame frameTuning = null;

    protected JPanel panelCommPort = null;
    protected JLabel labelPortCurrent = null;
    protected JTextField textCommPortStatus = null;
    protected JComboBox<String> comboBoxCommPort = null;

    protected JPanel panelTypeBd = null;
    protected JTextField textTypeBdStatus = null;
    protected JComboBox<TypeBaseDate> comboBoxTypeBd = null;

    protected JPanel panelParamSQL = null;
    protected JTextField fieldParamServerIP = null;
    protected JTextField fieldParamServerPort = null;
    protected JTextField fieldParamServerLogin = null;
    protected JTextField fieldParamServerPassword = null;
    protected JComboBox<String> comboBoxListBd = null;
    protected JButton buttonOk = null;
    protected JButton buttonSave = null;
    protected JButton buttonTest = null;

    protected JPanel panelSelectEdit = null;
    protected JButton buttonEditUsers = null;
    protected JButton buttonEditPushers = null;
    // ========================================================================
    // ********************* Actions ******************************************
    private void callSelectCommPort(ActionEvent actionEvent) {
        if (flagLockActions) return;
        textCommPortStatus.setText("");
        flagTestCommPort = false;
        buttonSave.setEnabled(false);
    }
    private void callSelectTypeBase(ActionEvent actionEvent) {
        if (flagLockActions) return;
        textTypeBdStatus.setText("");
        comboBoxListBd.removeAllItems();
        flagTestBaseData = false;
        buttonSave.setEnabled(false);
        //============================
        BaseData.Parameters parameters;
        try {
            parameters = loadParametersSql((TypeBaseDate) comboBoxTypeBd.getSelectedItem());
        } catch (BaseDataException e) {
            myLog.log(Level.SEVERE, "выбор типа БД: " + e.getStatus().toString(), e);
            textTypeBdStatus.setText("ошибка!!!!");
            return;
        }
        setComponentBaseData(parameters);
    }
    private void callSelectBaseData(ActionEvent e) {
        if (flagLockActions) return;
        textTypeBdStatus.setText("");
        flagTestBaseData = false;
        buttonSave.setEnabled(false);
        buttonEditEnable(false);
    }
    // ========================================================================
    private void callPushButtonTest(ActionEvent actionEvent) {
        callPushButtonTestBaseData();
        callPushButtonTestCommPort();
        buttonSave.setEnabled(true);
    }
    private void callPushButtonTestBaseData() {
        BaseData.Parameters parameters;
        BaseData conn;
        flagTestBaseData = false;
        buttonEditUsers.setEnabled(false);
        try {
            parameters = BaseData.Parameters.create((TypeBaseDate) comboBoxTypeBd.getSelectedItem());
            parameters.setIpServer(fieldParamServerIP.getText());
            parameters.setPortServer(fieldParamServerPort.getText());
            parameters.setUser(fieldParamServerLogin.getText());
            parameters.setPassword(fieldParamServerPassword.getText());
            parameters.setDataBase((String) comboBoxListBd.getSelectedItem());
            conn = connectBD(parameters);
        } catch (BaseDataException e) {
            myLog.log(Level.SEVERE, "нажатие кнопки тест: " + e.getStatus().toString(), e);
            switch (e.getStatus()) {
                case CONNECT_BASE_TYPE_ERROR:
                    textTypeBdStatus.setText("bad parameters");
                    break;
                case CONNECT_ERROR:
                    textTypeBdStatus.setText("ошибка соединения");
                    break;
                case CONNECT_PASS_ERROR:
                    textTypeBdStatus.setText("ошибка пароля");
                    break;
                default:
                    textTypeBdStatus.setText("не известная ошибка");
            }
            return;
        }
        if (comboBoxListBd.getItemCount() == 0) {
            //MySwingUtil.showMessage(frameTuning, "тест соединения с БД", "выберите базу", 8_000);
            textTypeBdStatus.setText("выберите базу");
            flagLockActions = true;
            try { MyUtil.<String>loadToComboBox(
                    conn.getListBase(),
                    comboBoxListBd,
                    false,
                    parametersSql.getDataBase()
            ); } catch (Exception e) {
                myLog.log(Level.WARNING, "нажатие кнопки тест", e);
                comboBoxListBd.removeAllItems();
            }
            flagLockActions = false;
            return;
        }
        // проверка структуры
        try {
            if (!conn.checkStructureBd((String) comboBoxListBd.getSelectedItem())) {
                textTypeBdStatus.setText("ошибка базы");
                throw new Exception("структура БД нарушена");
            }
        } catch (Exception e) {
            textTypeBdStatus.setText("ошибка базы");
            myLog.log(Level.WARNING, "нажатие кнопки тест", new Exception("тест структуры БД", e));
            return;
        }
        textTypeBdStatus.setText("соединение установлено");
        flagTestBaseData = true;
        //
        buttonEditUsers.setEnabled(true);
        buttonEditPushers.setEnabled(true);
    }
    private void callPushButtonTestCommPort() {
        CommPort port;
        CommPort.PortStat stat;
        flagTestCommPort = false;
        port = CommPort.main();
        stat = port.open(null, (String) comboBoxCommPort.getSelectedItem(), BAUD.baud9600);
        port.close();
        switch (stat) {
            case INITCODE_OK:
                textCommPortStatus.setText("порт открыт");
                break;
            case INITCODE_NOTEXIST:
                textCommPortStatus.setText("порт не обнаружен");
                break;
            case INITCODE_ERROROPEN:
                textCommPortStatus.setText("ошибка открытия");
                break;
            default:
                textCommPortStatus.setText("неизвестная ошибка");
        }
        if (stat.getCodePortStat() == CommPort.INITCODE_OK) {
            flagTestCommPort = true;
        } else {
            flagTestCommPort = false;
        }
    }
    // ========================================================================
    private void callPushButtonSave(ActionEvent actionEvent) {
        buttonSave.setEnabled(false);
        if (!flagTestBaseData || !flagTestCommPort) {
            int result;
            String textMess;
            if (!flagTestBaseData && !flagTestCommPort) textMess = "ошибки порта и БД - сохранить ?";
            else if (!flagTestBaseData) textMess = "ошибка БД - сохранить ?";
            else textMess = "ошибка порта - сохранить ?";
            result = javax.swing.JOptionPane.showConfirmDialog(null, textMess, "сохранение параметров", JOptionPane.OK_CANCEL_OPTION);
            if (result != 0) return;
        }
        Status result;
        // сохранения конфига
        BaseData.Config config;
        try {
            config = BaseData.Config.create();
            config.setPortName((String) comboBoxCommPort.getSelectedItem());
            config.setTypeBaseData((TypeBaseDate) comboBoxTypeBd.getSelectedItem());
            result = config.save();
            if (result != Status.OK) {
                throw new BaseDataException("ошибка сохранения конфигурации", result);
            }
            configProg = config;
        } catch (BaseDataException e) {
            myLog.log(Level.WARNING, "сохранение конфигурации", e.getStatus());
        }
        // сохранение параметров БД
        BaseData.Parameters parameters;
        try {
            parameters = BaseData.Parameters.create((TypeBaseDate) comboBoxTypeBd.getSelectedItem());
            parameters.setIpServer(fieldParamServerIP.getText());
            parameters.setPortServer(fieldParamServerPort.getText());
            parameters.setUser(fieldParamServerLogin.getText());
            parameters.setPassword(fieldParamServerPassword.getText());
            if (comboBoxListBd.getItemCount() > 0) parameters.setDataBase((String) comboBoxListBd.getSelectedItem());
            parameters.save();
            parametersSql = parameters;
            flagNewCorrectData = true;
        } catch (BaseDataException e) {
            myLog.log(Level.WARNING, "сохранение параметров соединения", e);
        }
    }
    // ===========================================================================
}
