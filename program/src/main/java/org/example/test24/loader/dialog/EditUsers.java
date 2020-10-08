package org.example.test24.loader.dialog;

import org.example.test24.bd.BaseData;
import org.example.test24.bd.usertypes.User;
import org.example.test24.lib.swing.CreateComponents;
import org.example.test24.lib.swing.FilterSortField2Table;
import org.example.test24.lib.swing.MySwingUtil;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
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
    private User[] listUsers = null; // полный список пользователей
    private User[] tablUsers = null; // список без активного пользователя

    private FilterSortField2Table<User> userFilterSortField2Table;



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
        offButtonEdit();
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

        label_title = CreateComponents.getLabel("Редактор пользователей", new Font("Times New Roman", Font.BOLD, 28), 160, 10, 310, 33, true, true);
        add(label_title);

        table = CreateComponents.getTable(562, null, new CreateComponents.ModelTableNameWidth[]{
                new CreateComponents.ModelTableNameWidth("ФИО", -1),
                new CreateComponents.ModelTableNameWidth("ранг", 32),
                new CreateComponents.ModelTableNameWidth("регистрация", 122)
        },
                null, null, true, true);
        scroll_table = CreateComponents.getScrollPane(20, 50, 580, 190, table, true, true);
        add(scroll_table);

        label_search = CreateComponents.getLabel("Поиск", new Font("Times New Roman", Font.PLAIN, 16), 20, 257, 60, 30, true, true);
        add(label_search);

        fieldSearch = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", Font.PLAIN, 14), 80, 259, 340, 25,
                null, null, true, true);
        add(fieldSearch);

        userFilterSortField2Table = new FilterSortField2Table<User>(
                fieldSearch,
                table,
                tablUsers,
                new FilterSortField2Table.CallBackF<User>() {
                    @Override
                    public Object decoder(int columnIndex, User result) {
                        String text;
                        switch (columnIndex) {
                            case 0:
                                text = result.surName;
                                break;
                            case 1:
                                text = "";
                                if ((result.rang & (1 << User.RANG_USERS)) != 0) text += "П";
                                if ((result.rang & (1 << User.RANG_PUSHERS)) != 0) text += "Т";
                                break;
                            case 2:
                                text = dateFormat.format(result.date_reg);
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + columnIndex);
                        }
                        return text;
                    }

                    @Override
                    public void selectRow(int rowIndex) {
                        editUser = tablUsers[rowIndex];
                        fieldSurName.setText(editUser.surName);
                        fieldPassword.setText(editUser.userPassword);
                        checkUsers.setSelected((editUser.rang & (1 << User.RANG_USERS)) != 0 );
                        checkPushers.setSelected((editUser.rang & (1 << User.RANG_PUSHERS)) != 0 );
                        table.clearSelection();
                        onButtonEdit();
                    }
                }
        );

        label_surName = CreateComponents.getLabel("ФИО", new Font("Times New Roman", Font.PLAIN, 16), 20, 298, 60, 30, true, true);
        add(label_surName);

        fieldSurName = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", Font.PLAIN, 14), 80, 300, 340, 25,
                null, null, true, true);
        add(fieldSurName);

        buttonDeactive = CreateComponents.getButton("деактивация", new Font("Times New Roman", Font.PLAIN, 14), 440, 298, 160, 30,
                this::pushButtonDeactive, true, true);
        add(buttonDeactive);

        label_password = CreateComponents.getLabel("Пароль", new Font("Times New Roman", Font.PLAIN, 16), 20, 338, 60, 30, true, true);
        add(label_password);

        fieldPassword = CreateComponents.getTextField(CreateComponents.PASSWORDFIELD, new Font("Times New Roman", Font.PLAIN, 14), 80, 340, 340, 25,
                null, null, true, true);
        add(fieldPassword);

        buttonNewUser = CreateComponents.getButton("Новый пользователь", new Font("Times New Roman", Font.PLAIN, 14), 440, 337, 160, 30,
                this::pushButtonNewUser, true, true);
        add(buttonNewUser);

        label_edit = CreateComponents.getLabel("Редактирование", new Font("Times New Roman", Font.PLAIN, 18), 170, 365, 130, 60, true, true);
        add(label_edit);

        checkUsers = CreateComponents.getJCheckBox("пользователей", new Font("Times New Roman", Font.PLAIN, 14), 311, 370, 120, 25,
                false, null, true, true);
        add(checkUsers);

        checkPushers = CreateComponents.getJCheckBox("толкателей", new Font("Times New Roman", Font.PLAIN, 14), 311, 400, 120, 25,
                false, null, true, true);
        add(checkPushers);

        buttonEditUser = CreateComponents.getButton("Ред. пользователя", new Font("Times New Roman", Font.PLAIN, 14), 440, 388, 160, 30,
                this::pushButtonEditUser, true, true);
        add(buttonEditUser);

        pack();
    }

    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    //      воздействие из органов управления
    private void pushButtonDeactive(ActionEvent actionEvent) {
        // деактивация выбранного пользователя
        deactiveSelectUser();
    }
    private void pushButtonNewUser(ActionEvent actionEvent) {
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
                    o -> {
                        buttonNewUser.setEnabled(true);
                        this.requestFocus();
                    }
            );
            buttonNewUser.setEnabled(false);
            return;
        }
        if (password.length() == 0) {
            MySwingUtil.showMessage(this,
                    "новый пользователь",
                    "пароль пустой",
                    5_000,
                    o -> {
                        buttonNewUser.setEnabled(true);
                        this.requestFocus();
                    }
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
                    o -> {
                        buttonNewUser.setEnabled(true);
                        this.requestFocus();
                    }
            );
            buttonNewUser.setEnabled(false);
            return;
        }
        writeNewUserToBase(surName, password, rang);
        // очистка полей
        clearFieldEdit();
    }
    private void pushButtonEditUser(ActionEvent actionEvent) {
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
                        onButtonEdit();
                    }
            );
            buttonNewUser.setEnabled(false);
            offButtonEdit();
            return;
        }
        if (password.length() == 0) {
            MySwingUtil.showMessage(this,
                    "редактирование пользователя",
                    "пароль пустой",
                    5_000,
                    o -> {
                        buttonNewUser.setEnabled(true);
                        onButtonEdit();
                    }
            );
            buttonNewUser.setEnabled(false);
            offButtonEdit();
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
                        onButtonEdit();
                    }
            );
            buttonNewUser.setEnabled(false);
            offButtonEdit();
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

    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //      воздействие на органы управления
    private void onButtonEdit() {
        buttonDeactive.setEnabled(true);
        buttonEditUser.setEnabled(true);
    }
    private void offButtonEdit() {
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
        if (userFilterSortField2Table != null) userFilterSortField2Table.setCollections(tablUsers);
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
            offButtonEdit();
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
    JTextField fieldSearch;
    JLabel label_edit;
    JLabel label_password;
    JLabel label_surName;
    JLabel label_title;
    JLabel label_search;
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
}
