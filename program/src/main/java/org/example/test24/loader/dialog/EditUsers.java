package org.example.test24.loader.dialog;

import org.example.test24.bd.BaseData;
import org.example.test24.bd.usertypes.User;
import org.example.test24.lib.swing.MySwingUtil;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;

import static org.example.test24.lib.MyLogger.myLog;

public class EditUsers extends JFrame {
    public interface CallBack {
        void messageCloseEditUsers(boolean newData);
        User getCurrentUser();
    }
    // объект обратного вызова
    private CallBack callBack;
    // события изменения
    private boolean flagEventEdit;
    // объект доступа к БД
    private BaseData connBD;
    // активный пользователь
    private User activetUser;
    private User editUser = null;
    private User[] listUsers = null;
    private User[] tablUsers = null;



    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public EditUsers(BaseData connBD, CallBack callBack) {
        this.connBD = connBD;
        this.callBack = callBack;
        flagEventEdit = false;
        // загрузка списка пользователей
        activetUser = callBack.getCurrentUser();
        readUsersFromBase();
        // инициализация компонентов
        initComponents(); // ****************************************************************
        // деактивация кнопок
        offButtonEditUser();
        setVisible(true);
        setResizable(false);
        // ловушка закрытия окна
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeFromLocal();
            }
        });
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(640, 480));
        setLayout(null);

        label_title = getLabel_title("Редактор пользователей", Font.BOLD, 28, 160, 10, 310, 33);
        add(label_title);

        label_surName = getLabel_surName("ФИО", Font.PLAIN, 16, 20, 268, 60, 30);
        add(label_surName);

        label_password = getLabel_password("Пароль", Font.PLAIN, 16, 20, 318, 60, 30);
        add(label_password);

        label_edit = getLabel_edit("Редактирование", Font.PLAIN, 18, 170, 345, 130, 60);
        add(label_edit);

        checkUsers = getJCheckBox("пользователей", false, Font.PLAIN, 14, 311, 350, 120, 25);
        add(checkUsers);

        checkPushers = getJCheckBox("толкателей", false, Font.PLAIN, 14, 311, 380, 120, 25);
        add(checkPushers);

        table = getTable(new SimpleTableModel(), 562, new BiInt[]{
                new BiInt(0, -1),
                new BiInt(1, 32),
                new BiInt(2, 122)
        });
        scroll_table = getScroll_table(table, 20,50, 580, 190);
        add(scroll_table);

        buttonDeactive = getButtonDeactive("деактивация", Font.PLAIN, 14, 440, 268, 160, 30);
        add(buttonDeactive);

        buttonNewUser = getButtonNewUser("Новый пользователь", Font.PLAIN, 14, 440, 317, 160, 30);
        add(buttonNewUser);

        buttonEditUser = getButton("Ред. пользователя", Font.PLAIN, 14, 440, 368, 160, 30, e -> pushButtonEditUser());
        add(buttonEditUser);

        fieldSurName = getFieldSurName("", Font.PLAIN, 14, 80, 270, 340, 25);
        add(fieldSurName);

        fieldPassword = getFieldPassword("", Font.PLAIN, 14, 80, 320, 340, 25);
        add(fieldPassword);

        pack();
    }
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    //      воздействие из органов управления
    private void pushButtonDeactive() {
        // деактивация выбранного пользователя
        deactiveSelectUser();
    }
    private void pushButtonNewUser() {
        String surName = fieldSurName.getText();
        String password = fieldPassword.getText();
        int rang = 0;
        if (checkUsers.isSelected()) rang |= 1 << User.RANG_USERS;
        if (checkPushers.isSelected()) rang |= 1 << User.RANG_PUSHERS;
        // запись нового пользователя в базу
        if (surName.length() == 0) {
            MySwingUtil.showMessage(this,
                    "новый пользователь",
                    "имя пользователя не задано",
                    5_000,
                    o -> buttonNewUser.setEnabled(true)
            );
            buttonNewUser.setEnabled(false);
            return;
        }
        if (password.length() == 0) {
            MySwingUtil.showMessage(this,
                    "новый пользователь",
                    "пароль пустой",
                    5_000,
                    o -> buttonNewUser.setEnabled(true)
            );
            buttonNewUser.setEnabled(false);
            return;
        }
        // проверка на повтор
        boolean flag = false;
        for (User user : listUsers) {
            if (user.surName.equals(surName)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            MySwingUtil.showMessage(this,
                    "новый пользователь",
                    "такой пользователь уже существует",
                    5_000,
                    o -> buttonNewUser.setEnabled(true)
            );
            buttonNewUser.setEnabled(false);
            return;
        }
        writeNewUserToBase(surName, password, rang);
        // очистка полей
        clearFieldEdit();
    }
    private void pushButtonEditUser() {
        String surName = fieldSurName.getText();
        String password = fieldPassword.getText();
        int rang = 0;
        if (checkUsers.isSelected()) rang |= 1 << User.RANG_USERS;
        if (checkPushers.isSelected()) rang |= 1 << User.RANG_PUSHERS;
        // запись нового пользователя в базу
        if (surName.length() == 0) {
            MySwingUtil.showMessage(this,
                    "редактирование пользователя",
                    "имя пользователя не задано",
                    5_000,
                    o -> {
                        buttonNewUser.setEnabled(true);
                        onButtonEditUser();
                    }
            );
            buttonNewUser.setEnabled(false);
            offButtonEditUser();
            return;
        }
        if (password.length() == 0) {
            MySwingUtil.showMessage(this,
                    "редактирование пользователя",
                    "пароль пустой",
                    5_000,
                    o -> {
                        buttonNewUser.setEnabled(true);
                        onButtonEditUser();
                    }
            );
            buttonNewUser.setEnabled(false);
            offButtonEditUser();
            return;
        }
        // проверка на повтор
        boolean flag = false;
        for (User user : listUsers) {
            if (user.id_user == editUser.id_user) continue;
            if (user.surName.equals(surName)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            myLog.log(Level.WARNING, "редактирование пользователя",
                    new Exception(callBack.getCurrentUser().surName + " : " + editUser.surName + " -> " + surName + " - такой пользователь уже существует"));
            MySwingUtil.showMessage(this,
                    "редактирование пользователя",
                    "такой пользователь уже существует",
                    5_000,
                    o -> {
                        buttonNewUser.setEnabled(true);
                        onButtonEditUser();
                    }
            );
            buttonNewUser.setEnabled(false);
            offButtonEditUser();
            return;
        }
        updateDataUser(
                callBack.getCurrentUser().id_loggerUser,
                editUser,
                surName,
                password,
                rang
        );
        // очистка полей
        clearFieldEdit();
    }

    private void enterTextSurName() {

   }
    private void enterTextPassword() {

    }
    private void selectTableCell() {
        onButtonEditUser();
        // выбранный пользователь
        editUser = tablUsers[table.getSelectedRow()];
        fieldSurName.setText(editUser.surName);
        fieldPassword.setText(editUser.userPassword);
        checkUsers.setSelected((editUser.rang & 1 << User.RANG_USERS) != 0);
        checkPushers.setSelected((editUser.rang & 1 << User.RANG_PUSHERS) != 0);
    }
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //      воздействие на органы управления
    private void onButtonEditUser() {
        buttonDeactive.setEnabled(true);
        buttonEditUser.setEnabled(true);
    }
    private void offButtonEditUser() {
        buttonEditUser.setEnabled(false);
        buttonDeactive.setEnabled(false);
    }
    // ==========================================
    // чтение из базы в массив
    private void readUsersFromBase() {
        try {
            listUsers = connBD.getListUsers(true);
        } catch (Exception e) {
            myLog.log(Level.SEVERE, "чтение списка пользователей", e);
            listUsers = new User[0];
        }
        ArrayList<User> list = new ArrayList<>();
        for (User user : listUsers) {
            if (user.id_user != activetUser.id_user) list.add(user);
        }
        tablUsers = list.toArray(new User[0]);
    }
    // деактивация выбранного пользователя
    private void deactiveSelectUser() {
        // выбранная строка
        try {
            connBD.deativateUser(
                    callBack.getCurrentUser().id_loggerUser,
                    editUser
            );
            // обновить таблицу
            readUsersFromBase();
            table.updateUI();
            flagEventEdit = true;
        } catch (Exception e) {
            myLog.log(Level.SEVERE, "деактивация пользователя", e);
        } finally {
            // отключить кнопки редактирования
            offButtonEditUser();
            // очистка полей
            clearFieldEdit();
            //
            table.getSelectionModel().clearSelection();
        }
    }
    // запись нового пользователя в базу
    private void writeNewUserToBase(String surName, String password, int rang) {
        try {
            connBD.writeNewUser(activetUser.id_loggerUser, surName, password, rang);
            flagEventEdit = true;
        } catch (Exception e) {
            myLog.log(Level.SEVERE, "запись нового пользователя в базу", e);
        }
        // обновить таблицу
        readUsersFromBase();
        table.updateUI();
    }
    // обновление записи о пользователе
    private void updateDataUser(long id_loggerUserEdit, User user, String surName, String password, int rang) {
        try {
            connBD.updateDataUser(user, id_loggerUserEdit, surName, password, rang);
            flagEventEdit = true;
        } catch (Exception e) {
            myLog.log(Level.SEVERE, "обновление записи пользователя в базе", e);
        }
        // обновить таблицу
        readUsersFromBase();
        table.updateUI();
    }
    // очистка полей редактирования
    private void clearFieldEdit() {
        editUser = null;
        // очистка полей
        fieldSurName.setText("");
        fieldPassword.setText("");
        checkUsers.setSelected(false);
        checkPushers.setSelected(false);
    }
    //  ---
    // ==========================================
    //           интерфейсные методы
    // закрытие окна по инициативе родителя
    public void closeFromParent() {
        removeAll();
        dispose();
        callBack.messageCloseEditUsers(false);
    }
    // закрытие окна по инициативе окна
    private void closeFromLocal() {
        removeAll();
        dispose();
        callBack.messageCloseEditUsers(flagEventEdit);
    }
    // ==========================================
    // компоненты
    JButton buttonDeactive;
    JButton buttonNewUser;
    JButton buttonEditUser;
    JCheckBox checkUsers;
    JCheckBox checkPushers;
    JTextField fieldPassword;
    JTextField fieldSurName;
    JLabel label_edit;
    JLabel label_password;
    JLabel label_surName;
    JLabel label_title;
    JScrollPane scroll_table;
    JTable table;
    // ------------------------------------------
    class SimpleTableModel extends AbstractTableModel {
        final int column_name = 0;
        final int column_datereg = 2;
        final int column_rang = 1;
        final String[] columnsName = new String[]{
                "ФИО",
                "ранг",
                "регистрация"
        };

        @Override
        public int getRowCount() {
            int row = 0;
            if (tablUsers != null) {
                row = tablUsers.length;
            }
            return row;
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            String text = "";
            if (tablUsers != null) {
                switch (columnIndex) {
                    case column_name:
                        text = tablUsers[rowIndex].surName;
                        break;
                    case column_datereg:
                        text = dateFormat.format(tablUsers[rowIndex].date_reg);
                        break;
                    case column_rang:
                        text = "";
                        if ((tablUsers[rowIndex].rang & 1 << User.RANG_USERS) != 0) text += "П";
                        if ((tablUsers[rowIndex].rang & 1 << User.RANG_PUSHERS) != 0) text += "Т";
                        break;
                    default:
                }
            }
            return text;
        }

        @Override
        public String getColumnName(int column) {
            return columnsName[column];
        }
    }
    // ------------------------------------------
    private JLabel getLabel_title(String text, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Times New Roman", fontStyle, fontSize));
        label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label.setBounds(x, y, width, height);
        return label;
    }
    private JLabel getLabel_surName(String text, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Times New Roman", fontStyle, fontSize));
        label.setBounds(x, y, width, height);
        return label;
    }
    private JLabel getLabel_password(String text, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Times New Roman", fontStyle, fontSize));
        label.setBounds(x, y, width, height);
        return label;
    }
    private JLabel getLabel_edit(String text, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Times New Roman", fontStyle, fontSize));
        label.setBounds(x, y, width, height);
        return label;
    }
    private JTable getTable(TableModel tableModel, int widthLast, BiInt[] widthColumns) {
        JTable table = new JTable();
        ArrayList<Integer> listAutoColumns = new ArrayList<>();
        try {
            table.setModel(tableModel);
            table.getTableHeader().setReorderingAllowed(false);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            for (BiInt widthColumn : widthColumns) {
                if (widthColumn.width < 0) {
                    listAutoColumns.add(widthColumn.index);
                    continue;
                }
                table.getColumnModel().getColumn(widthColumn.index).setPreferredWidth(widthColumn.width);
                widthLast = widthLast - widthColumn.width;
            }
            int wth = widthLast / listAutoColumns.size();
            for (int index : listAutoColumns) {
                table.getColumnModel().getColumn(index).setPreferredWidth(wth);
            }
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) return;
                    selectTableCell();
                }
            });
        } catch (ArrayIndexOutOfBoundsException ae) {
            ae.printStackTrace();
        }
        return table;
    }
    private JScrollPane getScroll_table(JTable table, int x, int y, int width, int height) {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(table);
        scrollPane.setBounds(x, y, width, height);
        return scrollPane;
    }
    private JButton getButtonDeactive(String text, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Times New Roman", fontStyle, fontSize));
        button.setBounds(x, y, width, height);
        button.addActionListener(e -> pushButtonDeactive());
        return button;
    }
    private JButton getButtonNewUser(String text, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Times New Roman", fontStyle, fontSize));
        button.setBounds(x, y, width, height);
        button.addActionListener(e -> pushButtonNewUser());
        return button;
    }
    private JButton getButton(String text, int fontStyle, int fontSize, int x, int y, int width, int height, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Times New Roman", fontStyle, fontSize));
        button.setBounds(x, y, width, height);
        button.addActionListener(listener);
        return button;
    }
    private JTextField getFieldSurName(String text, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JTextField textField = new JTextField(text);
        textField.setFont(new Font("Times New Roman", fontStyle, fontSize));
        textField.setBounds(x, y, width, height);
        textField.addActionListener(e -> enterTextSurName());
        return textField;
    }
    private JTextField getFieldPassword(String text, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JTextField textField = new JPasswordField(text);
        textField.setFont(new Font("Times New Roman", fontStyle, fontSize));
        textField.setBounds(x, y, width, height);
        textField.addActionListener(e -> enterTextPassword());
        return textField;
    }
    private JCheckBox getJCheckBox(String text, boolean stat, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JCheckBox box = new JCheckBox();
        box.setFont(new Font("Times New Roman", fontStyle, fontSize));
        box.setText(text);
        box.setSelected(stat);
        box.setBounds(x, y, width, height);
        return box;
    }

    class BiInt {
        public int index;
        public int width;
        public BiInt(int index, int width) {
            this.index = index;
            this.width = width;
        }
    }
}
