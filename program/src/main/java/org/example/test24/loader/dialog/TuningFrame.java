package org.example.test24.loader.dialog;

import org.example.test24.bd.*;
import org.example.test24.RS232.BAUD;
import org.example.test24.RS232.CommPort;
import org.example.test24.bd.usertypes.User;
import org.example.test24.lib.MyUtil;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
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



    //private BaseData1 bdSql = null; // *
    //private Thread threadSkeep = null; // *
    //private boolean threadSkeepOn; // *

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
        try { MyUtil.<String>loadToComboBox(listBaseBD, comboBoxListBd, parametersSql.getDataBase()); } catch (Exception e) {
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
        frameTuning = getFrameTuning("настройка", 640, 480);
        frameTuning.setResizable(false);
        Container container = frameTuning.getContentPane();
        {
            panelCommPort = getPanelTitle("выбор Comm порта",10, 10, 170, 110);
            container.add(panelCommPort);
            //
            panelCommPort.add(getLabel("текщий порт: ", 6, 15, 100, 30));
            //
            labelPortCurrent = getLabel("", 80, 15, 100, 30);
            panelCommPort.add(labelPortCurrent);
            //
            comboBoxCommPort = getComboBoxCommPort(6, 50, 150, 20);
            panelCommPort.add(comboBoxCommPort);
            //
            textCommPortStatus = getTextFieldStatus("", 6, 80, 150, 20);
            panelCommPort.add(textCommPortStatus);
        } // Comm Port
        {
            panelTypeBd = getPanelTitle("выбор Базы данных ", 180, 10, 190, 110);
            container.add(panelTypeBd);

            panelTypeBd.add(getLabel("тип базы данных: ", 10, 10, 140, 30));

            comboBoxTypeBd = getComboBoxTypeBd(6, 50, 150, 20);
            panelTypeBd.add(comboBoxTypeBd);

            textTypeBdStatus = getTextTypeBdStatus("", 6, 80, 150, 20);
            panelTypeBd.add(textTypeBdStatus);
        } // Type Base
        {
            panelParamSQL = getPanelTitle("параметры подключения", 10, 130, 360, 200);
            container.add(panelParamSQL);

            panelParamSQL.add(getLabel("ip адрес сервера: ", 6, 10, 140, 30));
            fieldParamServerIP = getFieldParamServerIP("", 160, 15, 140, 18);
            panelParamSQL.add(fieldParamServerIP);

            panelParamSQL.add(getLabel("порт: ", 6, 30, 140, 30));
            fieldParamServerPort = getFieldParamServerPort("", 160, 36, 140, 18);
            panelParamSQL.add(fieldParamServerPort);

            panelParamSQL.add(getLabel("логин: ", 6, 50, 140, 30));
            fieldParamServerLogin = getFieldParamServerLogin("", 160, 56, 140, 18);
            panelParamSQL.add(fieldParamServerLogin);

            panelParamSQL.add(getLabel("пароль: ", 6, 80, 140, 30));
            fieldParamServerPassword = getFieldParamServerPassword("", 160, 86, 140, 18);
            panelParamSQL.add(fieldParamServerPassword);

            panelParamSQL.add(getLabel("база данных: ", 6, 110, 140, 30));
            comboBoxListBd = getComboBoxListBd(160, 116, 140, 20);
            panelParamSQL.add(comboBoxListBd);

            buttonOk = getButtonOk("Ok", 16, 140, 80, 30);
            panelParamSQL.add(buttonOk);

            buttonSave = getButtonSave("Сохранить", 108, 140, 100, 30);
            panelParamSQL.add(buttonSave);

            buttonTest = getButtonTestBd("Тест", 220, 140, 80, 30);
            panelParamSQL.add(buttonTest);
        } // Parameters Base
        {
            panelSelectEdit = getPanelSelectEdit("редактирование", 10, 340, 360, 80);
            container.add(panelSelectEdit);

            buttonEditUsers = getButtonEditUsers("Пользователи", 16, 30, 140, 30);
            panelSelectEdit.add(buttonEditUsers);

            buttonEditPushers = getButtonEditPushers("Толкатели", 200, 30, 140, 30);
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

    private JPanel getPanelTitle(String title, int x, int y, int width, int height) {
        JPanel panel = new JPanel();
        panel.setBounds(x, y, width, height);
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setLayout(null);
        return panel;
    }
    private JLabel getLabel(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        return label;
    }
    private JComboBox<String> getComboBoxCommPort(int x, int y, int width, int height) {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setBounds(x, y, width, height);
        comboBox.addActionListener(e -> {
            callSelectCommPort(comboBox);
        });
        return comboBox;
    }
    private JTextField getTextFieldStatus(String text, int x, int y, int width, int height) {
        JTextField textField = new JTextField(text);
        textField.setBounds(x, y, width, height);
        textField.setEditable(false);
        return textField;
    }
    private JComboBox<TypeBaseDate> getComboBoxTypeBd(int x, int y, int width, int height) {
        JComboBox<TypeBaseDate> comboBox = new JComboBox<>();
        comboBox.setBounds(x, y, width, height);
        comboBox.addItem(TypeBaseDate.MS_SQL);
        comboBox.addItem(TypeBaseDate.MY_SQL);
        comboBox.addActionListener(e -> {
            callSelectTypeBase(comboBox);
        });
        return comboBox;
    }
    private JTextField getTextTypeBdStatus(String text, int x, int y, int width, int height) {
        JTextField textField = new JTextField(text);
        textField.setBounds(x, y, width, height);
        textField.setEditable(false);
        return textField;
    }
    private JTextField getFieldParamServerIP(String text, int x, int y, int width, int height) {
        JTextField field = new JTextField(text);
        field.setBounds(x, y, width, height);
        ((PlainDocument)field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.equals(".") || string.matches("\\d")) {
                    super.insertString(fb, offset, string, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.length() == 1) {
                    if (!text.equals(".") && !text.matches("\\d")) return;
                }
                super.replace(fb, offset, length, text, attrs);
            }
        });
        field.addActionListener(e -> {
            myLog.log(Level.WARNING, "field server ip", new Exception("action listener"));
        });
        return field;
    }
    private JTextField getFieldParamServerPort(String text, int x, int y, int width, int height) {
        JTextField field = new JTextField(text);
        field.setBounds(x, y, width, height);
        field.addActionListener(e -> {
            myLog.log(Level.WARNING, "field server port", new Exception("action listener"));
        });
        return field;
    }
    private JTextField getFieldParamServerLogin(String text, int x, int y, int width, int height) {
        JTextField field = new JTextField(text);
        field.setBounds(x, y, width, height);
        field.addActionListener(e -> {
            myLog.log(Level.WARNING, "field server user", new Exception("action listener"));
        });
        return field;
    }
    private JTextField getFieldParamServerPassword(String text, int x, int y, int width, int height) {
//        JTextField field = new JPasswordField(text);
        JTextField field = new JTextField(text);
        field.setBounds(x, y, width, height);
        field.addActionListener(e -> {
            myLog.log(Level.WARNING, "field server password", new Exception("action listener"));
        });
        return field;
    }
    private JComboBox<String> getComboBoxListBd(int x, int y, int width, int height) {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setBounds(x, y, width, height);
        comboBox.addItemListener(e -> {
            if (e.getStateChange() == 1) return;
            callSelectBaseData(comboBox);
        });
        return comboBox;
    }
    private JButton getButtonOk(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setEnabled(false);
        button.addActionListener(e -> {
            //pushButtonOk();
            myLog.log(Level.WARNING, "push button ok", new Exception("action listener"));
        });
        return button;
    }
    private JButton getButtonSave(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setEnabled(false);
        button.addActionListener(e -> {
            callPushButtonSave();
        });
        return button;
    }
    private JButton getButtonTestBd(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setEnabled(false);
        button.addActionListener(e -> {
            callPushButtonTest();
        });
        return button;
    }

    private JPanel getPanelSelectEdit(String title, int x, int y, int width, int height) {
        JPanel panel = new JPanel();
        panel.setBounds(x, y, width, height);
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setLayout(null);
        return panel;
    }
    private JButton getButtonEditUsers(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setEnabled(false);
        button.addActionListener(e -> {
            pushButtonEditUsers();
        });
        return button;
    }
    private JButton getButtonEditPushers(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setEnabled(false);
        button.addActionListener(e -> {
            //closeFrame();
            myLog.log(Level.WARNING, "push button edit pushers", new Exception("action listener"));
        });
        return button;
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
        /*if (editUsers != null) {

        }*/
    } // ****************
    // ======
    private class EditUsersCallBack implements EditUsers.CallBack {
        @Override
        public void messageCloseEditUsers(boolean newData) {
            //editUsers = null;
        }

        @Override
        public User getCurrentUser() {
            return null;
        }
/*@Override
        public BaseData1 getBdInterface() {
            if (bdSql == null) {
                String typeBd = (String) comboBoxTypeBd.getSelectedItem();
                // подключение к БД
                //bdSql = BaseData1.init(typeBd, callBack MC.getFilesNameSql());
            }
            return bdSql;
        }*/
    }
    // ==================




    // загрузка параметров SQL
    /*private boolean loadParametersSql(String typeBd) {
        boolean stat = false;
        parametersSql = new ParametersSql2(
                BaseData1.getNameFileParametrsSql(
                        typeBd,
                        callBack MC.getFilesNameSql()
                )
        );
        try {
            //parametersSql.load();
            stat = true;
        } catch (Exception e) {
            System.out.println("ошибка чтения параметров SQL: " + e.getMessage());
        }
        return stat;
    }
    */
    // сохранение параметров SQL
    private void saveParametersSql() {
        /*parametersSql.urlServer = fieldParamServerIP.getText();
        parametersSql.portServer = fieldParamServerPort.getText();
        parametersSql.user = fieldParamServerLogin.getText();
        parametersSql.password = fieldParamServerPassword.getText();
        parametersSql.dataBase = (String) comboBoxListBd.getSelectedItem();
        parametersSql.save();*/
        checkStatusComp();
    }
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
    // разрешение кнопки ок
    protected void onOffButtonOk() {
        //buttonOk.setEnabled((chCheckCommPort == CommPort.PortStat.INITCODE_OK) && flCheckSql);
    }
    protected void onButtonOk() {
        buttonOk.setEnabled(true);
    }
    protected void offButtonOk() {
        buttonOk.setEnabled(false);
    }
    // разрешение кнопки тест
    protected void onOffButtonTest() {
        /*if (flCheckListBd) {
            buttonTest.setEnabled(true);
        } else {
            buttonTest.setEnabled(false);
        }*/
    }
    protected void onButtonTest() {
        buttonTest.setEnabled(true);
    }
    protected void offButtonTest() {
        buttonTest.setEnabled(false);
    }
    // разрешение кнопки save
    protected void onOffButtonSave() {
        /*if (flCheckSql) {
            buttonSave.setEnabled(true);
        } else {
            buttonSave.setEnabled(false);
        }*/
    }
    protected void onButtonSave() {
        buttonSave.setEnabled(true);
    }
    protected void offButtonSave() {
        buttonSave.setEnabled(false);
    }
    // разрешение кнопки редактирование пользователей
    protected void onOffButtonEditUsers() {
        /*if (flCheckSql) {
            buttonEditUsers.setEnabled(true);
        } else {
            buttonEditUsers.setEnabled(false);
        }*/
    }
    protected void onButtonEditUsers() {
        buttonEditUsers.setEnabled(true);
    }
    protected void offButtonEditUsers() {
        buttonEditUsers.setEnabled(false);
    }
    // разрешение кнопки редактирование толкателей
    protected void onOffButtonEditPushers() {
        /*if (flCheckSql) {
            buttonEditPushers.setEnabled(true);
        } else {
            buttonEditPushers.setEnabled(false);
        }*/
    }
    protected void onButtonEditPushers() {
        buttonEditPushers.setEnabled(true);
    }
    protected void offButtonEditPushers() {
        buttonEditPushers.setEnabled(false);
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
    // смена параметров подключения к SQL серверу
    private void selectParametersConnectBd() {
        //if (lockBegin)  return;
        try {
            String currentItem = (String) comboBoxListBd.getSelectedItem();
            //getListBdComp();
            comboBoxListBd.setSelectedItem(currentItem);
        } catch (Exception e) {
            System.out.println("ошибка чтения списка БД: " + e.getMessage());
        } // чтение списка БД
        // разрешение кнопки тест
        onOffButtonTest();
        // запрет кнопки редактирования пользователей
        offButtonEditUsers();
        // запрет кнопки редактирования толкателей
        offButtonEditPushers();
    }
    // нажатие кнопки ок
    private void pushButtonOk() {
        //if (lockBegin)  return;
        closeFrame();
    }
    // нажатие кнопки save
    private void pushButtonSave() {
        //if (lockBegin)  return;
        offButtonSave();
        saveParametersSql();
        // статус основных параметров
        checkStatusComp();
        // выдача статуса основных параметров
        outStatus();
        // разрешение кнопки ок
        onOffButtonOk();
        // разрешение кнопки редактирования пользователей
        onOffButtonEditUsers();
        // разрешение кнопки редактирования толкателей
        onOffButtonEditPushers();
    }
    // нажатие кнопки test
    private void pushButtonTest() {
        //if (lockBegin)  return;
        // статус основных параметров
        checkStatusComp();
        // выдача статуса основных параметров
        outStatus();
        onOffButtonSave();
        offButtonTest();
    }
    // нажатие кнопки редактирование пользователей
    private void pushButtonEditUsers() {
        if (!flagTestBaseData) {
            myLog.log(Level.SEVERE, "не установлен флаг коррекности БД");
            buttonEditUsers.setEnabled(false);
            return;
        }
        SaveEnableComponents saveComponents = new SaveEnableComponents();
        saveComponents.offline();
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                new EditUsers(connBD,
                        new EditUsers.CallBack() {
                            @Override
                            public void messageCloseEditUsers(boolean newData) {
                                if (newData) {
                                    // здесь перезагрузка списка пользователей (новые данные)
                                }
                                saveComponents.restore();
                            }
                            // текущий активный пользователь
                            @Override
                            public User getCurrentUser() {
                                /*User user = new User(
                                        0,
                                        new Date(),
                                        0,
                                        "lockAdmin",
                                        "",
                                        3,
                                        null
                                );*/
                                return null;
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
    private void callSelectCommPort(JComboBox comboBox) {
        if (flagLockActions) return;
        textCommPortStatus.setText("");
        flagTestCommPort = false;
        buttonSave.setEnabled(false);
        myLog.log(Level.SEVERE, "СДЕЛАТЬ !!!!!!!!!!", new Exception("action выбор comm port"));
    }
    private void callSelectTypeBase(JComboBox comboBox) {
        if (flagLockActions) return;
        textTypeBdStatus.setText("");
        comboBoxListBd.removeAllItems();
        flagTestBaseData = false;
        buttonSave.setEnabled(false);
        //============================
        BaseData.Parameters parameters;
        try {
            parameters = loadParametersSql((TypeBaseDate) comboBox.getSelectedItem());
        } catch (BaseDataException e) {
            myLog.log(Level.SEVERE, "выбор типа БД: " + e.getStatus().toString(), e);
            textTypeBdStatus.setText("ошибка!!!!");
            return;
        }
        setComponentBaseData(parameters);
    }
    private void callSelectBaseData(JComboBox comboBox) {
        if (flagLockActions) return;
        textTypeBdStatus.setText("");
        flagTestBaseData = false;
        buttonSave.setEnabled(false);
        myLog.log(Level.SEVERE, "СДЕЛАТЬ !!!!!!!!!!", new Exception("action выбор базы БД"));
    }
    // ========================================================================
    private void callPushButtonTest() {
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
    private void callPushButtonSave() {
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
    class SaveEnableComponents {
        private boolean frameTuning;
        private boolean comboBoxCommPort;
        private boolean comboBoxTypeBd;
        private boolean comboBoxListBd;
        private boolean fieldParamServerIP;
        private boolean fieldParamServerPort;
        private boolean fieldParamServerLogin;
        private boolean fieldParamServerPassword;
        private boolean buttonOk;
        private boolean buttonSave;
        private boolean buttonTest;
        private boolean buttonEditUsers;
        private boolean buttonEditPushers;
        public SaveEnableComponents() {
            save();
        }
        public void save() {
            frameTuning = TuningFrame.this.frameTuning.isEnabled();
            comboBoxCommPort = TuningFrame.this.comboBoxCommPort.isEnabled();
            comboBoxTypeBd = TuningFrame.this.comboBoxTypeBd.isEnabled();
            comboBoxListBd = TuningFrame.this.comboBoxListBd.isEnabled();
            fieldParamServerIP = TuningFrame.this.fieldParamServerIP.isEnabled();
            fieldParamServerPort = TuningFrame.this.fieldParamServerPort.isEnabled();
            fieldParamServerLogin = TuningFrame.this.fieldParamServerLogin.isEnabled();
            fieldParamServerPassword = TuningFrame.this.fieldParamServerPassword.isEnabled();
            buttonOk = TuningFrame.this.buttonOk.isEnabled();
            buttonSave = TuningFrame.this.buttonSave.isEnabled();
            buttonTest = TuningFrame.this.buttonTest.isEnabled();
            buttonEditUsers = TuningFrame.this.buttonEditUsers.isEnabled();
            buttonEditPushers = TuningFrame.this.buttonEditPushers.isEnabled();
        }
        public void restore() {
            TuningFrame.this.frameTuning.setEnabled(frameTuning);
            TuningFrame.this.comboBoxCommPort.setEnabled(comboBoxCommPort);
            TuningFrame.this.comboBoxTypeBd.setEnabled(comboBoxTypeBd);
            TuningFrame.this.comboBoxListBd.setEnabled(comboBoxListBd);
            TuningFrame.this.fieldParamServerIP.setEnabled(fieldParamServerIP);
            TuningFrame.this.fieldParamServerPort.setEnabled(fieldParamServerPort);
            TuningFrame.this.fieldParamServerLogin.setEnabled(fieldParamServerLogin);
            TuningFrame.this.fieldParamServerPassword.setEnabled(fieldParamServerPassword);
            TuningFrame.this.buttonOk.setEnabled(buttonOk);
            TuningFrame.this.buttonSave.setEnabled(buttonSave);
            TuningFrame.this.buttonTest.setEnabled(buttonTest);
            TuningFrame.this.buttonEditUsers.setEnabled(buttonEditUsers);
            TuningFrame.this.buttonEditPushers.setEnabled(buttonEditPushers);
        }
        public void offline() {
            TuningFrame.this.frameTuning.setEnabled(false);
            TuningFrame.this.comboBoxCommPort.setEnabled(false);
            TuningFrame.this.comboBoxTypeBd.setEnabled(false);
            TuningFrame.this.comboBoxListBd.setEnabled(false);
            TuningFrame.this.fieldParamServerIP.setEnabled(false);
            TuningFrame.this.fieldParamServerPort.setEnabled(false);
            TuningFrame.this.fieldParamServerLogin.setEnabled(false);
            TuningFrame.this.fieldParamServerPassword.setEnabled(false);
            TuningFrame.this.buttonOk.setEnabled(false);
            TuningFrame.this.buttonSave.setEnabled(false);
            TuningFrame.this.buttonTest.setEnabled(false);
            TuningFrame.this.buttonEditUsers.setEnabled(false);
            TuningFrame.this.buttonEditPushers.setEnabled(false);
        }
    }
    // ========================================================================
}
