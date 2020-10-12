package org.example.test24.loader.dialog;

import org.example.test24.bd.*;
import org.example.test24.RS232.BAUD;
import org.example.test24.RS232.CommPort;
import org.example.test24.bd.usertypes.User;
import org.example.test24.lib.swing.CreateComponents;
import org.example.test24.lib.swing.MySwingUtil;
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

    private final SaveEnableComponents saveEnableComponents;

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
        } catch (BaseDataException e) {
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
        // конструктор окна
        frameConstructor();

        // установка компонентов в начальное положение
        setComponentsBegin();
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
        try { result = config.load();
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
    private BaseData.Parameters loadParametersSql(TypeBaseDate typeBaseDate) throws ParametersSqlException
    {
        if (typeBaseDate == null) throw new ParametersSqlException("не задан тип БД", Status.BASE_TYPE_NO_SELECT, null);
        if (typeBaseDate == TypeBaseDate.ERROR)  throw new ParametersSqlException("ошибочный тип БД", Status.BASE_TYPE_ERROR, null);
        //
        BaseData.Parameters parameters;
        try { parameters = BaseData.Parameters.create(typeBaseDate);
        } catch (BaseDataException e) { throw new ParametersSqlException("получение параметров БД", e, Status.PARAMETERS_LOAD_ERROR, null);
        }
        Status result;
        try { result = parameters.load();
        } catch (BaseDataException e) { throw new ParametersSqlException("получение параметров БД", e, Status.PARAMETERS_LOAD_ERROR, parameters);
        }
        if (result != Status.OK) { throw new ParametersSqlException("получение параметров БД", Status.PARAMETERS_LOAD_ERROR, parameters);
        }
        //
        return parameters;
    }
    // =============================================================================================================
    // подключение к БД
    private BaseData connectBD(BaseData.Parameters parametersSql) throws BaseDataException {
        BaseData baseData;
        try { baseData = BaseData.create(parametersSql);
        } catch (BaseDataException e) { throw new BaseDataException("соединение с БД", e, e.getStatus());
        }
        //
        try { baseData.openConnect(parametersSql);
        } catch (BaseDataException e) { throw new BaseDataException("соединение с БД", e, e.getStatus());
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
        textCommPortStatus.setText("");
//        // БД
        setComponentBaseData(parametersSql);
//        // список БД
        try { MyUtil.loadToComboBox(listBaseBD, comboBoxListBd, false, parametersSql.getDataBase()); } catch (Exception e) {
            myLog.log(Level.WARNING, "начальная инициализация компонентов", e);
        }
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

            comboBoxTypeBd = CreateComponents.getComboBox(new Font("Dialog", Font.BOLD, 11),
                    6, 50, 150, 20, false, null, this::callSelectTypeBase, true, true);
            flagLockActions = true;
            comboBoxTypeBd.addItem(TypeBaseDate.MS_SQL);
            comboBoxTypeBd.addItem(TypeBaseDate.MY_SQL);
            flagLockActions = false;
            panelTypeBd.add(comboBoxTypeBd);

            textTypeBdStatus = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Dialog", Font.BOLD, 11),
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
                    this::callPushButtonEditPushers,
                    true,
                    false);
            panelSelectEdit.add(buttonEditPushers);
        } // Select Edit
        frameTuning.pack();
        frameTuning.setResizable(false);
        frameTuning.setVisible(true);
    }
    // ======
    // >>>>>>>>>>>>>>>>>>>>>>
    protected void buttonEditEnable(boolean enabled) {
        buttonEditUsers.setEnabled(enabled);
        buttonEditPushers.setEnabled(enabled);
    }
    // <<<<<<<<<<<<<<<<<<<<<<
    // нажатие кнопки редактирование пользователей
    private void callPushButtonEditUsers(ActionEvent actionEvent) {
        if (!flagTestBaseData) {
            myLog.log(Level.SEVERE, "не установлен флаг коррекности БД");
            buttonEditUsers.setEnabled(false);
            return;
        }
        saveEnableComponents.save();
        saveEnableComponents.offline();
        new Thread(() -> SwingUtilities.invokeLater(() -> new EditUsers(
                connBD.cloneNewBase((String) comboBoxListBd.getSelectedItem()),
                new EditUsers.CallBack() {
                    @Override
                    public void messageCloseEditUsers(boolean newData) {
                        saveEnableComponents.restore();
                        frameTuning.requestFocus();
                    }
                    // текущий активный пользователь
                    @Override
                    public User getCurrentUser() {
                        return new User(
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
                    }
                })), "create edit users").start();
    }
    private void callPushButtonEditPushers(ActionEvent actionEvent) {
        if (!flagTestBaseData) {
            myLog.log(Level.SEVERE, "не установлен флаг коррекности БД");
            buttonEditUsers.setEnabled(false);
            return;
        }
        saveEnableComponents.save();
        saveEnableComponents.offline();
        new Thread(()-> SwingUtilities.invokeLater(()->{
            try {
                new EditPushers(
                        newData -> {
                            saveEnableComponents.restore();
                            frameTuning.requestFocus();
                        },
                        connBD.cloneNewBase((String) comboBoxListBd.getSelectedItem()),
                        0
                );
            } catch (BaseDataException bde) {
                myLog.log(Level.SEVERE, "ошибка редактирования толкателей", bde);
                MySwingUtil.showMessage(frameTuning, "редактор толкателей", "ошибка редактирования толкателей",
                        5_000, o -> {
                            saveEnableComponents.restore();
                            frameTuning.requestFocus();
                        }
                );
            }
        }),"create edit pushers").start();
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
            try { MyUtil.loadToComboBox(
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
        flagTestCommPort = stat.getCodePortStat() == CommPort.INITCODE_OK;
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
