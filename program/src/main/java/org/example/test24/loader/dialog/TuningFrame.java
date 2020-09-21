package org.example.test24.loader.dialog;

import org.example.test24.bd.*;
import org.example.test24.RS232.BAUD;
import org.example.test24.RS232.CommPort;
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
import java.util.logging.Level;

import static org.example.test24.lib.MyLogger.myLog;

class TuningFrame {

    interface CallBack {
        void messageCloseTuning();
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



    private BaseData1 bdSql = null; // *
    private Thread threadSkeep = null; // *
    private boolean threadSkeepOn; // *

    private CommPort.PortStat chCheckCommPort = CommPort.PortStat.INITCODE_NOTEXIST;
    private boolean flCheckParamSql = false;
    private boolean flCheckListBd = false;

    // =============================================================================================================
    protected TuningFrame(CallBack callBack)
    {
        this.callBack = callBack;
        boolean flInit = true;
        // загрузка параметров
        configProg = loadConfigProg();
        try {
            parametersSql = loadParameters(configProg);
        } catch (Exception e) {
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
        BaseData.Status result = null;
        try {
            result = config.load1();
        } catch (Exception e) {
            myLog.log(Level.WARNING, "ошибка чтения файла конфигурации", e);
            config.setDefault();
        }
        if (result != BaseData.Status.OK) {
            myLog.log(Level.SEVERE, "загрузка параметров конфигурации программы", new Exception(result.toString()));
            config.setDefault();
        }
        return config;
    }
    // =============================================================================================================
    // загрузка параметров подключения к БД
    private BaseData.Parameters loadParametersSql(BaseData.TypeBaseDate typeBaseDate) throws Exception
    {
        if (typeBaseDate == null) throw new Exception("не задан тип БД");
        if (typeBaseDate == BaseData.TypeBaseDate.ERROR)  throw new Exception("ошибочный тип БД");
        //
        BaseData.Parameters parameters = BaseData.Parameters.create(typeBaseDate);
        BaseData.Status result;
        result = parameters.load();
        if (result != BaseData.Status.OK) {
            throw new Exception("загрузка параметров соединения с БД", new Exception(result.toString()));
        }
        //
        return parameters;
    }
    // =============================================================================================================
    // подключение к БД
    private BaseData connectBD(BaseData.Parameters parametersSql) throws Exception {
        BaseData baseData;
        try {
            baseData = BaseData.create(parametersSql);
        } catch (Exception e) {
            throw new Exception("соединение с БД", e);
        }
        //
        try {
            baseData.openConnect(parametersSql);
        } catch (Exception e) {
            throw new Exception("соединение с БД", e);
        }
        return baseData;
    }
    // =============================================================================================================
    //
    private BaseData.Parameters loadParameters(BaseData.Config configProg) throws Exception {
        BaseData.Parameters parametersSql;
        try {
            parametersSql = loadParametersSql(configProg.getTypeBaseData());
        } catch (Exception e) {
            throw new Exception("инициализация соединение с БД", e);
        }
        return parametersSql;
    }
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
        textTypeBdStatus.setText(parametersSql.getTypeBaseDate().toString());
//        // список БД
        try { MyUtil.<String>loadToComboBox(listBaseBD, comboBoxListBd, parametersSql.getDataBase()); } catch (Exception e) {
            myLog.log(Level.WARNING, "начальная инициализация компонентов", e);
        }
//        //
//        // установка начального состояния кнопок по основным параметрам
//        setButtonBegin();
        flagLockActions = false;
    }
    private void setComponentCommPort(String[] listCommPort, String defaultCommPort) {
        comboBoxCommPort.removeAllItems();
        Arrays.stream(listCommPort).sorted((a, b) -> a.compareTo(b)).forEach(s -> comboBoxCommPort.addItem(s));
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
            panelCommPort = getPanelTitle("выбор Comm порта",10, 10, 130, 110);
            container.add(panelCommPort);
            //
            panelCommPort.add(getLabel("текщий порт: ", 6, 15, 100, 30));
            //
            labelPortCurrent = getLabel("", 80, 15, 100, 30);
            panelCommPort.add(labelPortCurrent);
            //
            comboBoxCommPort = getComboBoxCommPort(6, 50, 110, 20);
            panelCommPort.add(comboBoxCommPort);
            //
            textCommPortStatus = getTextFieldStatus("", 6, 80, 110, 20);
            panelCommPort.add(textCommPortStatus);
        } // Comm Port
        {
            panelTypeBd = getPanelTitle("выбор Базы данных ", 140, 10, 230, 110);
            container.add(panelTypeBd);

            panelTypeBd.add(getLabel("тип базы данных: ", 10, 10, 140, 30));

            comboBoxTypeBd = getComboBoxTypeBd(6, 50, 110, 20);
            panelTypeBd.add(comboBoxTypeBd);

            textTypeBdStatus = getTextTypeBdStatus("", 6, 80, 110, 20);
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
            //selectCommPort(comboBox);
        });
        return comboBox;
    }
    private JTextField getTextFieldStatus(String text, int x, int y, int width, int height) {
        JTextField textField = new JTextField(text);
        textField.setBounds(x, y, width, height);
        textField.setEditable(false);
        return textField;
    }

    private JComboBox<BaseData.TypeBaseDate> getComboBoxTypeBd(int x, int y, int width, int height) {
        JComboBox<BaseData.TypeBaseDate> comboBox = new JComboBox<>();
        comboBox.setBounds(x, y, width, height);
        comboBox.addItem(BaseData.TypeBaseDate.MS_SQL);
        comboBox.addItem(BaseData.TypeBaseDate.MY_SQL);
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
                    threadSkeepOn = false;
                    if (!text.equals(".") && !text.matches("\\d")) return;
                }
                super.replace(fb, offset, length, text, attrs);
            }
        });
        field.addActionListener(e -> {
            //selectParametersConnectBd();
        });
        return field;
    }
    private JTextField getFieldParamServerPort(String text, int x, int y, int width, int height) {
        JTextField field = new JTextField(text);
        field.setBounds(x, y, width, height);
        field.addActionListener(e -> {
            //selectParametersConnectBd();
        });
        return field;
    }
    private JTextField getFieldParamServerLogin(String text, int x, int y, int width, int height) {
        JTextField field = new JTextField(text);
        field.setBounds(x, y, width, height);
        field.addActionListener(e -> {
            //selectParametersConnectBd();
        });
        return field;
    }
    private JTextField getFieldParamServerPassword(String text, int x, int y, int width, int height) {
//        JTextField field = new JPasswordField(text);
        JTextField field = new JTextField(text);
        field.setBounds(x, y, width, height);
        field.addActionListener(e -> {
            //selectParametersConnectBd();
        });
        return field;
    }
    private JComboBox<String> getComboBoxListBd(int x, int y, int width, int height) {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setBounds(x, y, width, height);
        comboBox.addItemListener(e -> {
            //if (e.getStateChange() == 1) return;
            //selectParametersConnectBd();
        });
        return comboBox;
    }
    private JButton getButtonOk(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setEnabled(false);
        button.addActionListener(e -> pushButtonOk());
        return button;
    }
    private JButton getButtonSave(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setEnabled(false);
        button.addActionListener(e -> pushButtonSave());
        return button;
    }
    private JButton getButtonTestBd(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setEnabled(false);
        button.addActionListener(e -> pushButtonTest());
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
        button.addActionListener(e -> pushButtonEditUsers());
        return button;
    }
    private JButton getButtonEditPushers(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setEnabled(false);
        button.addActionListener(e -> {
            //closeFrame();
        });
        return button;
    }


    private void closeFrame() {
        threadSkeepOn = false;
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
            callBack.messageCloseTuning();
        }
        if (editUsers != null) {

        }
    } // ****************
    // ======
    private class EditUsersCallBack implements EditUsers.CallBack {
        @Override
        public void messageCloseEditUsers(boolean newData) {
            editUsers = null;
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
            String typeBd = "";
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
        threadSkeepOn = false;
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
        threadSkeepOn = false;
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
        threadSkeepOn = false;
        // статус основных параметров
        checkStatusComp();
        // выдача статуса основных параметров
        outStatus();
        onOffButtonSave();
        offButtonTest();
    }
    // нажатие кнопки редактирование пользователей
    private void pushButtonEditUsers() {
        if (editUsers == null) {
            editUsers = new EditUsers(null, new EditUsersCallBack());
        }
    }
    // ========================================================================
    // ===== компоненты JFrame =======
    protected JFrame frameTuning = null;
    protected EditUsers editUsers = null;

    protected JPanel panelCommPort = null;
    protected JLabel labelPortCurrent = null;
    protected JTextField textCommPortStatus = null;
    protected JComboBox<String> comboBoxCommPort = null;

    protected JPanel panelTypeBd = null;
    protected JTextField textTypeBdStatus = null;
    protected JComboBox<BaseData.TypeBaseDate> comboBoxTypeBd = null;

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
    private void callSelectTypeBase(JComboBox comboBox) {
        if (flagLockActions) return;
        myLog.log(Level.SEVERE, "СДЕЛАТЬ !!!!!!!!!!", new Exception("action выбор типа БД"));
    }
    // ========================================================================
}
