package org.example.test24.loader.dialog;

import org.example.lib.functioninterface.FunctionException;
import org.example.test24.bd.*;
import org.example.test24.RS232.BAUD;
import org.example.test24.RS232.CommPort;
import org.example.test24.loader.ParametersConfig;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Consumer;

class TuningFrame extends TuningFrameVars {

    private BaseData1 bdSql = null;
    private Thread threadSkeep = null;
    private boolean threadSkeepOn;

    private CommPort.PortStat chCheckCommPort = CommPort.PortStat.INITCODE_NOTEXIST;
    private boolean flCheckParamSql = false;
    private boolean flCheckListBd = false;

    private boolean lockBegin = false;

    // ===== компоненты JFrame =======
    private JFrame frameTuning = null;
    private EditUsers editUsers = null;

    private JPanel panelCommPort = null;
    private JLabel labelPortCurrent = null;
    private JTextField textCommPortStatus = null;
    private JComboBox<String> comboBoxCommPort = null;

    private JPanel panelTypeBd = null;
    private JTextField textTypeBdStatus = null;
    private JComboBox<String> comboBoxTypeBd = null;

    private JPanel panelParamSQL = null;
    private JTextField fieldParamServerIP = null;
    private JTextField fieldParamServerPort = null;
    private JTextField fieldParamServerLogin = null;
    private JTextField fieldParamServerPassword = null;
    private JComboBox<String> comboBoxListBd = null;
    private JButton buttonOk = null;
    private JButton buttonSave = null;
    private JButton buttonTest = null;

    private JPanel panelSelectEdit = null;
    private JButton buttonEditUsers = null;
    private JButton buttonEditPushers = null;


    // ===============================================


    public static void createFrame(FrameCallBack callBack,
                                   boolean statMainWork,
                                   Consumer<TuningFrame> tFrame,
                                   FunctionException<Exception> fException) {
        new Thread(()->{
            TuningFrame[] tuningFrame = new TuningFrame[1];
            // конструктор
            SwingUtilities.invokeLater(()-> {
                tuningFrame[0] = new TuningFrame(callBack, statMainWork);
                tFrame.accept(tuningFrame[0]);
            });
        }, "thread start tunning frame").start();

    }

    protected TuningFrame(FrameCallBack callBack, boolean statMainWork) //throws Exception
    {
        this.callBack = callBack;
        this.statMainWork = statMainWork;
        // загрузка параметров
        loadBeginerParameters();
        // конструктор окна
        frameConstructor();
        // установка компонентов в начальное положение
        setComponentsBegin();
    }
    // загрузка начальных параметров
    private void loadBeginerParameters() //throws Exception
    {
        ParametersConfig config;
        BaseData.Status resultBaseData; // ***********
        ParametersSql parametersSql = null;
        int parametersSqlError = 1;
        //
        // запрос конфигурации
        config = callBack.getParametersConfig();
        // тип БД
        if (config.getTypeBaseData() == BaseData.TypeBaseData.ERROR) {
            System.out.println("ошибка типа базы данных: " + config.getTypeBaseData().toString());
            config.setTypeBaseData(BaseData.TypeBaseData.MY_SQL);
        }
        // загрузка параметров БД
        try {
            parametersSql = callBack.requestParametersSql(config.getTypeBaseData());
            parametersSqlError = 0;
        } catch (Exception e) {
            System.out.println("Ошибка загрузки параметров соединения с БД" + e.getMessage());
            parametersSqlError = 1;
        }
        if (parametersSqlError == 0) {
            // чтение списка пользователей из нового соединения
            listUsers = getListUsersFromNewConnect(parametersSql, i -> {
                switch (i) {
                    case BaseData.CONNECT_ERROR:
                    case BaseData.STRUCTURE_ERROR:
                    case BaseData.QUERY_ERROR:
                        flCheckSql = false;
                        break;
                    case BaseData.OK:
                        flCheckSql = true;
                        break;
                }
            });
        } else {
            this.flCheckSql = false;
        }
        this.parametersSql = parametersSql;
        // проверка ком порта
        try {
            flCheckCommPort = callBack.isCheckCommPort(statMainWork, config.getPortName());
            commPortName = config.getPortName();
            commPortNameList = CommPort.getListPortsName();
        } catch (Exception e) {
            System.out.println("Ошибка поверки ком порта: " + e.getMessage());
            flCheckCommPort = false;
            commPortName = "";
            commPortNameList = new String[0];
        }
        // ---
        try {
            listBaseData = callBack.getListBd();
        } catch (Exception exception) {
            exception.printStackTrace();
            listBaseData = new String[0];
        }
    }

    private void setComponentCommPort(String[] listCommPort, String defaultCommPort) {
        comboBoxCommPort.removeAllItems();
        Arrays.stream(listCommPort).sorted((a, b) -> a.compareTo(b)).forEach(s -> comboBoxCommPort.addItem(s));
        comboBoxCommPort.setSelectedItem(defaultCommPort);
    }

    private void setComponentBaseData(ParametersSql parametersSql) {
        parametersSql.urlServer = "255.255.255";
        // тип БД
        comboBoxTypeBd.setSelectedItem(parametersSql.typeBaseData.toString());
        // параметры подключения
        fieldParamServerIP.setText(parametersSql.urlServer);
        fieldParamServerPort.setText(parametersSql.portServer);
        fieldParamServerLogin.setText(parametersSql.user);
        fieldParamServerPassword.setText(parametersSql.password);
    }



    // установка компонентов в начальное положение
    private void setComponentsBegin() {
        // ком порт
        setComponentCommPort(commPortNameList, commPortName);
        labelPortCurrent.setText(commPortName);
        textCommPortStatus.setText("");
        // БД
        setComponentBaseData(parametersSql);
        textTypeBdStatus.setText(parametersSql.typeBaseData.toString());
        loadListToCombobox(listBaseData, comboBoxListBd);
        //
        if (flCheckCommPort && flCheckListBd && flCheckSql) {

        }
    }

    private void frameConstructor() {
        frameTuning = getFrameTuning("настройка", 640, 480);
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

    private JComboBox<String> getComboBoxTypeBd(int x, int y, int width, int height) {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setBounds(x, y, width, height);
        comboBox.addItem("MS_SQL");
        comboBox.addItem("MY_SQL");
        comboBox.addActionListener(e -> {
            //selectTypeBase(comboBox);
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

    /*private boolean getListBdFile(String typeBd) {
        // определения файла конфигурации
        String fileNameSql = null;
        try {
            fileNameSql = ""; //callBack MC.getFileNameSql(typeBd);
        } catch (Exception e) {
            System.out.println("getListBdFile: " + e.getMessage());
        }
        if (fileNameSql == null) {
            return false;
        }
        // чтение параметров из файла конфигурации
        ParametersSql parametersSqlLocal = null;// = new ParametersSql(fileNameSql);
        try {
            parametersSqlLocal.load();
        } catch (Exception e) {
            System.out.println("getListBdFile: " + e.getMessage());
            return false;
        }
        // чтение списка БД
        String[] listBd;
        try {
            listBd = BaseData1.getConnectListBd(
                        typeBd,
                        parametersSqlLocal.urlServer,
                        parametersSqlLocal.portServer,
                        parametersSqlLocal.user,
                        parametersSqlLocal.password
                );
        } catch (Exception e) {
            System.out.println("getListBdFile: " + e.getMessage());
            return false;
        }
        // загрузка списка в компонент
        comboBoxListBd.removeAllItems();
        for (int i = 0; i < listBd.length; i++) {
            comboBoxListBd.addItem(listBd[i]);
            flCheckListBd = true;
        }
        return true;
    }*/
    /*private void getListBdComp() throws Exception {
        try {
            String typeBd = (String) comboBoxTypeBd.getSelectedItem();
            // подключение к БД
            bdSql = BaseData1.init(typeBd, callBack MC.getFilesNameSql());
            String[] listBd = bdSql.getConnectListBd(
                    fieldParamServerIP.getText(),
                    fieldParamServerPort.getText(),
                    fieldParamServerLogin.getText(),
                    fieldParamServerPassword.getText()
            );
            comboBoxListBd.removeAllItems();
            for (int i = 0; i < listBd.length; i++) {
                comboBoxListBd.addItem(listBd[i]);
                flCheckListBd = true;
            }
        } catch (SQLException e) {
            flCheckListBd = false;
            throw new Exception("ошибка инициация подключения к BD");
        } catch (Exception e) {
            flCheckListBd = false;
            throw new Exception("ошибка получения списка баз данных");
        }
    }*/

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
            //callBackTF.messageCloseTuningFrame();
            //callBack.closeFrame();
        }
        if (editUsers != null) {

        }
    } // ****************
    // ======
    private class EditUsersCallBack implements EditUsers.CallBack {
        @Override
        public void messageCloseEditUsers() {
            editUsers = null;
        }

        @Override
        public BaseData1 getBdInterface() {
            if (bdSql == null) {
                String typeBd = (String) comboBoxTypeBd.getSelectedItem();
                // подключение к БД
                //bdSql = BaseData1.init(typeBd, callBack MC.getFilesNameSql());
            }
            return bdSql;
        }
    }
    // ==================
    // загрузка в combobox списка БД
    private boolean loadListToCombobox(String[] list, JComboBox<String> comboBox) {
        if (list == null || comboBox == null) {
            return false;
        }
        comboBox.removeAllItems();
        for (int i = 0; i < list.length; i++) {
            comboBox.addItem(list[i]);
        }
        return true;
    }




    // загрузка параметров SQL
    private boolean loadParametersSql(String typeBd) {
        boolean stat = false;
        /*parametersSql = new ParametersSql(
                BaseData1.getNameFileParametrsSql(
                        typeBd,
                        callBack MC.getFilesNameSql()
                )
        );*/
        try {
            parametersSql.load();
            stat = true;
        } catch (Exception e) {
            System.out.println("ошибка чтения параметров SQL: " + e.getMessage());
        }
        return stat;
    }
    // сохранение параметров SQL
    private void saveParametersSql() {
        parametersSql.urlServer = fieldParamServerIP.getText();
        parametersSql.portServer = fieldParamServerPort.getText();
        parametersSql.user = fieldParamServerLogin.getText();
        parametersSql.password = fieldParamServerPassword.getText();
        parametersSql.dataBase = (String) comboBoxListBd.getSelectedItem();
        parametersSql.save();
        checkStatusComp();
    }
    // статус основных параметров
    private void checkStatusComp() {
        {
            String portName = (String) comboBoxCommPort.getSelectedItem();
            CommPort commPort = null; //callBack MC.getCommPort();
            CommPort.PortStat ch = commPort.Open(null, portName, BAUD.baud57600);
            if (ch == CommPort.PortStat.INITCODE_OK) {
                commPort.Close();
            }
            chCheckCommPort = ch;
        }   // статус ком порта
        {
            // тип текущей БД
            String typeBd = (String) comboBoxTypeBd.getSelectedItem();
            //загрузка параметров с выбранным типом
            /*parametersSql = new ParametersSql(
                    BaseData1.getNameFileParametrsSql(
                            typeBd,
                            callBack MC.getFilesNameSql()
                    )
            );*/
            try {
                parametersSql.load();
                // параметры БД удачно прочитались
                flCheckParamSql = true;
            } catch (Exception e) {
                System.out.println("ошибка чтения параметров SQL: " + e.getMessage());
                // параметры БД плохие
                flCheckParamSql = false;
                // структура плохая
                flCheckSql = false;
            }
            // доступ к БД
            try {
                flCheckSql = BaseData1.testStuctBase(
                        (String) comboBoxTypeBd.getSelectedItem(),
                        fieldParamServerIP.getText(),
                        fieldParamServerPort.getText(),
                        fieldParamServerLogin.getText(),
                        fieldParamServerPassword.getText(),
                        (String) comboBoxListBd.getSelectedItem()
                );
            } catch (java.lang.Throwable e) {
                System.out.println("ошибка параметров SQL: " + e.getMessage());
                flCheckSql = false;
            }
        }   // структура БД
    }
    private boolean checkStatusFile() {
        {
            String portName = (String) comboBoxCommPort.getSelectedItem();
            CommPort commPort = null; //callBack MC.getCommPort();
            CommPort.PortStat ch = commPort.Open(null, portName, BAUD.baud57600);
            if (ch == CommPort.PortStat.INITCODE_OK) {
                commPort.Close();
            }
            chCheckCommPort = ch;
        }   // статус ком порта
        {
            // тип текущей БД
            String typeBd = "";
//            String typeBd = parameters[0];
            //String typeBd = callBack MC.loadConfigTypeBaseData().getTypeBaseDataString();
            //загрузка параметров с выбранным типом
            /*parametersSql = new ParametersSql(
                    BaseData1.getNameFileParametrsSql(
                            typeBd,
                            callBack MC.getFilesNameSql()
                    )
            );*/
            boolean flCheckParamSql;
            try {
                parametersSql.load();
                // параметры БД удачно прочитались
                flCheckParamSql = true;
            } catch (Exception e) {
                System.out.println("ошибка чтения параметров SQL: " + e.getMessage());
                // параметры БД плохие
                flCheckParamSql = false;
                // структура плохая
                flCheckSql = false;
            }
            // доступ к БД
            if (flCheckParamSql) {
                try {
                    flCheckSql = BaseData1.testStuctBase(
                            (String) comboBoxTypeBd.getSelectedItem(),
                            fieldParamServerIP.getText(),
                            fieldParamServerPort.getText(),
                            fieldParamServerLogin.getText(),
                            fieldParamServerPassword.getText(),
                            (String) comboBoxListBd.getSelectedItem()
                    );
                } catch (java.lang.Throwable e) {
                    System.out.println("ошибка параметров SQL: " + e.getMessage());
                    flCheckSql = false;
                }
            } else {
                flCheckSql = false;
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
            if (flCheckSql) {
                textTypeBdStatus.setText("BD ok");
            } else {
                textTypeBdStatus.setText("Error structure BD");
            }
        }   // выдача статуса БД
    }
    // установка начального состояния кнопок по основным параметрам
    private void setButtonBegin() {
        buttonSave.setEnabled(false);
        // если БД в порядке
        if (flCheckSql) {
            onButtonEditUsers();
            onButtonEditPushers();
        } else {
            offButtonEditUsers();
            offButtonEditPushers();
        }
        // разрешение кнопки тест
        onOffButtonTest();
        // разрешение кнопки ок
        onOffButtonOk();
        // запрет кнопки save
        offButtonSave();
    }
    // >>>>>>>>>>>>>>>>>>>>>>
    // разрешение кнопки ок
    private void onOffButtonOk() {
        buttonOk.setEnabled((chCheckCommPort == CommPort.PortStat.INITCODE_OK) && flCheckSql);
    }
    private void onButtonOk() {
        buttonOk.setEnabled(true);
    }
    private void offButtonOk() {
        buttonOk.setEnabled(false);
    }
    // разрешение кнопки тест
    private void onOffButtonTest() {
        if (flCheckListBd) {
            buttonTest.setEnabled(true);
        } else {
            buttonTest.setEnabled(false);
        }
    }
    private void onButtonTest() {
        buttonTest.setEnabled(true);
    }
    private void offButtonTest() {
        buttonTest.setEnabled(false);
    }
    // разрешение кнопки save
    private void onOffButtonSave() {
        if (flCheckSql) {
            buttonSave.setEnabled(true);
        } else {
            buttonSave.setEnabled(false);
        }
    }
    private void onButtonSave() {
        buttonSave.setEnabled(true);
    }
    private void offButtonSave() {
        buttonSave.setEnabled(false);
    }
    // разрешение кнопки редактирование пользователей
    private void onOffButtonEditUsers() {
        if (flCheckSql) {
            buttonEditUsers.setEnabled(true);
        } else {
            buttonEditUsers.setEnabled(false);
        }
    }
    private void onButtonEditUsers() {
        buttonEditUsers.setEnabled(true);
    }
    private void offButtonEditUsers() {
        buttonEditUsers.setEnabled(false);
    }
    // разрешение кнопки редактирование толкателей
    private void onOffButtonEditPushers() {
        if (flCheckSql) {
            buttonEditPushers.setEnabled(true);
        } else {
            buttonEditPushers.setEnabled(false);
        }
    }
    private void onButtonEditPushers() {
        buttonEditPushers.setEnabled(true);
    }
    private void offButtonEditPushers() {
        buttonEditPushers.setEnabled(false);
    }
    // <<<<<<<<<<<<<<<<<<<<<<
    // выбран comm port
    private void selectCommPort(JComboBox comboBox) {
        if (lockBegin)  return;
        threadSkeepOn = false;
        checkStatusComp();
        outStatus();
        // разрешение кнопки ок
        onOffButtonOk();
        // сохранить
        if (chCheckCommPort == CommPort.PortStat.INITCODE_OK) {
            // callBack MC.saveConfigCommPort((String) comboBoxCommPort.getSelectedItem());
        }
    }
    // выбран тип БД
    private void selectTypeBase(JComboBox comboBox) {
        if (lockBegin)  return;
        threadSkeepOn = false;
        checkStatusComp();
        outStatus();
        //сохранить
        if (flCheckParamSql) {
            //callBack MC.saveConfigTypeBaseData(BaseData.typeBaseDataCode((String) comboBox.getSelectedItem()));
        }
    }
    // смена параметров подключения к SQL серверу
    private void selectParametersConnectBd() {
        if (lockBegin)  return;
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
        if (lockBegin)  return;
        closeFrame();
    }
    // нажатие кнопки save
    private void pushButtonSave() {
        if (lockBegin)  return;
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
        if (lockBegin)  return;
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
            editUsers = new EditUsers(new EditUsersCallBack());
        }
    }
}
