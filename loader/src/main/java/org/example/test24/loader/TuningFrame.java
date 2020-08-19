package org.example.test24.loader;

import org.example.bd.DataBase;
import org.example.bd.ParametersSql;
import org.example.bd.SqlWork_interface;
import org.example.test24.RS232.BAUD;
import org.example.test24.RS232.CommPort;
import org.example.test24.loader.editUsers.EditUserCallBackParent;
import org.example.test24.loader.editUsers.EditUserLogicInterface;
import org.example.test24.loader.editUsers.EditUserLogic;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;
import java.util.Arrays;

class TuningFrame {
    private MainClassCallBackTuningFrame callBackMC = null;
    private StartFrameCallBackTuningFrame callBackTF = null;

    private String[] parametrs = null;
    private ParametersSql parametersSql = null;
    private SqlWork_interface bdSql = null;
    private Thread threadSkeep = null;
    private boolean threadSkeepOn;

    private boolean flCheckCommPort = false;
    private boolean flCheckParamSql = false;
    private boolean flCheckSql = false;

    private JFrame frameTuning = null;
    private EditUserLogicInterface editUserLogic = null;

    private JPanel panelCommPort = null;
    private JLabel labelPortCurrent = null;
    private JTextField textPortStatus = null;
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

    public TuningFrame(MainClassCallBackTuningFrame callBackMC) {
        this.callBackMC = callBackMC;
    }

    void frameConfig(String[] parametrs, StartFrameCallBackTuningFrame callBackTF) {
        this.parametrs = parametrs;
        this.callBackTF = callBackTF;
        frameConstructor();
    }

    private void frameConstructor() {
        frameTuning = getFrameTuning("настройка", new Dimension(640, 480));
        Container container = frameTuning.getContentPane();
        {
            panelCommPort = getPanelTitle("выбор Comm порта", new Rectangle(10, 10, 130, 110));
            container.add(panelCommPort);
            //
            panelCommPort.add(getLabel("текщий порт: ", new Rectangle(6, 15, 100, 30)));
            //
            labelPortCurrent = getLabel(parametrs[1].toUpperCase(), new Rectangle(80, 15, 100, 30));
            panelCommPort.add(labelPortCurrent);
            //
            comboBoxCommPort = getComboBoxCommPort(parametrs[1], new Rectangle(6, 50, 110, 20));
            panelCommPort.add(comboBoxCommPort);
            //
            textPortStatus = getTextFieldStatus("sel", new Rectangle(6, 80, 110, 20));
            panelCommPort.add(textPortStatus);
        } // Comm Port
        {
            panelTypeBd = getPanelTitle("выбор Базы данных ", new Rectangle(140, 10, 230, 110));
            container.add(panelTypeBd);

            panelTypeBd.add(getLabel("тип базы данных: ", new Rectangle(10, 10, 140, 30)));

            comboBoxTypeBd = getComboBoxTypeBd(parametrs[0], new Rectangle(6, 50, 110, 20));
            panelTypeBd.add(comboBoxTypeBd);

            textTypeBdStatus = getTextTypeBdStatus("unknow", new Rectangle(6, 80, 110, 20));
            panelTypeBd.add(textTypeBdStatus);
        } // Type Base
        {
            panelParamSQL = getPanelTitle("параметры подключения", new Rectangle(10, 130, 360, 200));
            container.add(panelParamSQL);

            panelParamSQL.add(getLabel("ip адрес сервера: ", new Rectangle(6, 10, 140, 30)));
            fieldParamServerIP = getFieldParamServerIP(new Rectangle(160, 15, 140, 18));
            panelParamSQL.add(fieldParamServerIP);

            panelParamSQL.add(getLabel("порт: ", new Rectangle(6, 30, 140, 30)));
            fieldParamServerPort = getFieldParamServerPort("123", new Rectangle(160, 36, 140, 18));
            panelParamSQL.add(fieldParamServerPort);

            panelParamSQL.add(getLabel("логин: ", new Rectangle(6, 50, 140, 30)));
            fieldParamServerLogin = getFieldParamServerLogin("login", new Rectangle(160, 56, 140, 18));
            panelParamSQL.add(fieldParamServerLogin);

            panelParamSQL.add(getLabel("пароль: ", new Rectangle(6, 80, 140, 30)));
            fieldParamServerPassword = getFieldParamServerPassword("password", new Rectangle(160, 86, 140, 18));
            panelParamSQL.add(fieldParamServerPassword);

            panelParamSQL.add(getLabel("база данных: ", new Rectangle(6, 110, 140, 30)));
            comboBoxListBd = getComboBoxListBd(new Rectangle(160, 116, 140, 20));
            panelParamSQL.add(comboBoxListBd);

            buttonOk = getButtonOk("Ok", new Rectangle(16, 140, 80, 30));
            panelParamSQL.add(buttonOk);

            buttonSave = getButtonSave("Сохранить", new Rectangle(108, 140, 100, 30));
            panelParamSQL.add(buttonSave);

            buttonTest = getButtonTestBd("Тест", new Rectangle(220, 140, 80, 30));
            panelParamSQL.add(buttonTest);
        } // Parameters Base
        {
            panelSelectEdit = getPanelSelectEdit("редактирование", new Rectangle(10, 340, 360, 80));
            container.add(panelSelectEdit);

            buttonEditUsers = getButtonEditUsers("Пользователи", new Rectangle(16, 30, 140, 30));
            panelSelectEdit.add(buttonEditUsers);

            buttonEditPushers = getButtonEditPushers("Толкатели", new Rectangle(200, 30, 140, 30));
            panelSelectEdit.add(buttonEditPushers);
        } // Select Edit
        flCheckCommPort = checkCommPort();
        flCheckParamSql = getParamSql();
        if (flCheckParamSql) {
            flCheckSql = DataBase.testStuctBase(
                    (String) comboBoxTypeBd.getSelectedItem(),
                    fieldParamServerIP.getText(),
                    fieldParamServerPort.getText(),
                    fieldParamServerLogin.getText(),
                    fieldParamServerPassword.getText(),
                    (String) comboBoxListBd.getSelectedItem()

            );
        } else {
            flCheckSql = false;
        }
        frameTuning.pack();
        frameTuning.setResizable(false);
        frameTuning.setVisible(true);

        if (flCheckCommPort && flCheckParamSql && flCheckSql) {
            buttonOk.setEnabled(true);
            /*
            threadSkeep = new Thread(new Runnable() {
                @Override
                public void run() {
                    int count = 15 * 10;
                    threadSkeepOn = true;
                    try {
                        while (threadSkeepOn) {
                            count--;
                            buttonOk.setText(String.valueOf(count / 10));
                            Thread.sleep(100);
                            if (count == 0) break;
                        }
                        if (count == 0) {
                            System.out.println("skeep");
                            closeFrame();
                        } else {
                            buttonOk.setText("Ok");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            threadSkeep.start();
            */
        }

    }
    private JFrame getFrameTuning(String title, Dimension size) {
        JFrame frame = new JFrame(title);
        frame.setSize(size);
        frame.setPreferredSize(size);
        frame.setLayout(null);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                if (editUserLogic != null) {
                    editUserLogic.closeFromParent();
                }
                e.getWindow().removeAll();
                closeFrame();
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        return frame;
    }

    private JPanel getPanelTitle(String title, Rectangle positionSize) {
        JPanel panel = new JPanel();
        panel.setBounds(positionSize);
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setLayout(null);
        return panel;
    }
    private JLabel getLabel(String text, Rectangle positionSize) {
        JLabel label = new JLabel(text);
        label.setBounds(positionSize);
        return label;
    }
    private JComboBox getComboBoxCommPort(String itemDefault, Rectangle positionSize) {
        String[] listCommPortName = callBackMC.getCommPort().getListPortsName();
        // sort
        Arrays.sort(listCommPortName);
        JComboBox<String> comboBox = new JComboBox<>(listCommPortName);
        comboBox.setSelectedItem(itemDefault);
        comboBox.setBounds(positionSize);
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                threadSkeepOn = false;
                if (checkCommPort()) {
                    parametrs[1] = (String) comboBoxCommPort.getSelectedItem();
                    callBackMC.saveConfig(parametrs);
                    if (flCheckSql) {
                        buttonOk.setEnabled(true);
                    }

                } else {
                    buttonOk.setEnabled(false);
                }
            }
        });
        return comboBox;
    }
    private JTextField getTextFieldStatus(String text, Rectangle positionSize) {
        JTextField textField = new JTextField(text);
        textField.setBounds(positionSize);
        textField.setEditable(false);
        return textField;
    }

    private JComboBox getComboBoxTypeBd(String itemDefault, Rectangle positionSize) {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setBounds(positionSize);
        comboBox.addItem("MS_SQL");
        comboBox.addItem("MY_SQL");
        comboBox.setSelectedItem(itemDefault);
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                threadSkeepOn = false;
                flCheckParamSql = getParamSql();
                if (flCheckParamSql) {
                    parametrs[0] = (String) comboBox.getSelectedItem();
                    callBackMC.saveConfig(parametrs);
                } else {
                    flCheckSql = false;
                }
            }
        });
        return comboBox;
    }
    private JTextField getTextTypeBdStatus(String text, Rectangle positionSize) {
        JTextField textField = new JTextField(text);
        textField.setBounds(positionSize);
        textField.setEditable(false);
        return textField;
    }
    private JTextField getFieldParamServerIP(Rectangle positionSize) {
        JTextField field = new JTextField("127.0.0.1");
        field.setBounds(positionSize);
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
        field.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    getListBd();
                } catch (Exception ex) {
                    System.out.println(ex.getLocalizedMessage());
                }
            }
        });
        return field;
    }
    private JTextField getFieldParamServerPort(String text, Rectangle positionSize) {
        JTextField field = new JTextField(text);
        field.setBounds(positionSize);
        field.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                threadSkeepOn = false;
                try {
                    getListBd();
                } catch (Exception ex) {
                    System.out.println(ex.getLocalizedMessage());
                }
            }
        });
        return field;
    }
    private JTextField getFieldParamServerLogin(String text, Rectangle positionSize) {
        JTextField field = new JTextField(text);
        field.setBounds(positionSize);
        field.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                threadSkeepOn = false;
                try {
                    getListBd();
                } catch (Exception ex) {
                    System.out.println(ex.getLocalizedMessage());
                }
            }
        });
        return field;
    }
    private JTextField getFieldParamServerPassword(String text, Rectangle positionSize) {
        JTextField field = new JPasswordField(text);
        field.setBounds(positionSize);
        field.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                threadSkeepOn = false;
                try {
                    getListBd();
                } catch (Exception ex) {
                    System.out.println(ex.getLocalizedMessage());
                }
            }
        });
        return field;
    }
    private JComboBox getComboBoxListBd(Rectangle positionSize) {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setBounds(positionSize);
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                threadSkeepOn = false;
            }
        });
        return comboBox;
    }
    private JButton getButtonOk(String text, Rectangle positionSize) {
        JButton button = new JButton(text);
        button.setBounds(positionSize);
        button.setEnabled(false);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeFrame();
            }
        });
        return button;
    }
    private JButton getButtonSave(String text, Rectangle positionSize) {
        JButton button = new JButton(text);
        button.setBounds(positionSize);
        button.setEnabled(false);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.setEnabled(false);
                parametersSql.urlServer = fieldParamServerIP.getText();
                parametersSql.portServer = fieldParamServerPort.getText();
                parametersSql.user = fieldParamServerLogin.getText();
                parametersSql.password = fieldParamServerPassword.getText();
                parametersSql.dataBase = (String) comboBoxListBd.getSelectedItem();
                parametersSql.save();
                flCheckParamSql = getParamSql();
                if (flCheckParamSql) {
                    parametrs[0] = (String) comboBoxTypeBd.getSelectedItem();
                    callBackMC.saveConfig(parametrs);
                } else {
                    flCheckSql = false;
                }
                buttonOk.setEnabled(true);
            }
        });
        return button;
    }
    private JButton getButtonTestBd(String text, Rectangle positionSize) {
        JButton button = new JButton(text);
        button.setBounds(positionSize);
        button.setEnabled(false);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                threadSkeepOn = false;
                buttonOk.setEnabled(false);
                flCheckSql = bdSql.testStuctBase(
                        fieldParamServerIP.getText(),
                        fieldParamServerPort.getText(),
                        fieldParamServerLogin.getText(),
                        fieldParamServerPassword.getText(),
                        (String) comboBoxListBd.getSelectedItem()
                );
                buttonSave.setEnabled(flCheckSql);
            }
        });
        return button;
    }

    private JPanel getPanelSelectEdit(String title, Rectangle positionSize) {
        JPanel panel = new JPanel();
        panel.setBounds(positionSize);
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setLayout(null);
        return panel;
    }
    private JButton getButtonEditUsers(String text, Rectangle positionSize) {
        JButton button = new JButton(text);
        button.setBounds(positionSize);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editUserLogic == null) {
                    editUserLogic = new EditUserLogic(getEditUserCallBackParent());
                }
            }
        });
        return button;
    }
    private JButton getButtonEditPushers(String text, Rectangle positionSize) {
        JButton button = new JButton(text);
        button.setBounds(positionSize);
        button.setEnabled(false);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //closeFrame();
            }
        });
        return button;
    }

    private boolean getParamSql() {
        boolean stat = true;
        buttonOk.setEnabled(false);
        buttonSave.setEnabled(false);
        String typeBd = (String) comboBoxTypeBd.getSelectedItem();
        parametersSql = new ParametersSql(
                DataBase.getNameFileParametrsSql(
                        typeBd,
                        callBackMC.getFileNameSql()
                ),
                typeBd
        );
        try {
            parametersSql.load();
            fieldParamServerIP.setText(parametersSql.urlServer);
            fieldParamServerPort.setText(parametersSql.portServer);
            fieldParamServerLogin.setText(parametersSql.user);
            fieldParamServerPassword.setText(parametersSql.password);
            getListBd();
            comboBoxListBd.setSelectedItem(parametersSql.dataBase);
            if (!parametersSql.dataBase.equals((String) comboBoxListBd.getSelectedItem())) {
                stat = false;
                System.out.println("нет указанной BD");
                textTypeBdStatus.setText("not exist BD");
            } else {
                textTypeBdStatus.setText("ok");
            }
        } catch (Exception e) {
            stat = false;
            System.out.println(e.getLocalizedMessage());
            textTypeBdStatus.setText("bad");
        }
        return stat;
    }
    private void getListBd() throws Exception {
        buttonOk.setEnabled(false);
        buttonTest.setEnabled(false);
        buttonSave.setEnabled(false);
        try {
            comboBoxListBd.removeAllItems();
            String typeBD = (String)  comboBoxTypeBd.getSelectedItem();
            bdSql = DataBase.init(typeBD, callBackMC.getFileNameSql());
            String[] listBd = bdSql.getConnectListBd(
                    fieldParamServerIP.getText(),
                    fieldParamServerPort.getText(),
                    fieldParamServerLogin.getText(),
                    fieldParamServerPassword.getText()
            );
            for (int i = 0; i < listBd.length; i++) {
                comboBoxListBd.addItem(listBd[i]);
            }
            buttonTest.setEnabled(true);
        } catch (SQLException e) {
            throw new Exception("ошибка инициация подключения к BD");
        } catch (Exception e) {
            throw new Exception("ошибка получения списка баз данных");
        }
    }

    private boolean checkCommPort() {
        String portName = (String) comboBoxCommPort.getSelectedItem();
        CommPort commPort = callBackMC.getCommPort();
        int ch =  commPort.Open(null, portName, BAUD.baud57600);
        switch (ch) {
            case CommPort.INITCODE_OK:
                labelPortCurrent.setText(portName);
                commPort.Close();
                textPortStatus.setText("ok");
                break;
            case CommPort.INITCODE_NOTEXIST:
                labelPortCurrent.setText(parametrs[1]);
                commPort.Close();
                textPortStatus.setText("порт не найден");
                break;
            case CommPort.INITCODE_ERROROPEN:
                labelPortCurrent.setText(parametrs[1]);
                commPort.Close();
                textPortStatus.setText("ошибка открытия");
                break;
            default:
        }
        return ch == CommPort.INITCODE_OK;
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
            callBackTF.pusk();
        }
        if (editUserLogic != null) {

        }
    }
    // ======
    private EditUserCallBackParent getEditUserCallBackParent() {
        return new EditUserCallBackParent() {
            @Override
            public void messageCloseEditUsers() {
                editUserLogic = null;
            }
        };
    }
}
