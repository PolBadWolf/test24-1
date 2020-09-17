package org.example.test24.loader.dialog;

import org.example.test24.bd.BaseData;
import org.example.test24.bd.BaseData1;
import org.example.test24.bd.UserClass;

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
import java.util.Arrays;
import java.util.logging.Level;

import static org.example.test24.lib.MyLogger.myLog;

public class EditUsers extends JFrame
{
    public interface CallBack {
        void messageCloseEditUsers(boolean newData);
    }
    // объект обратного вызова
    private CallBack callBack;
    // объект доступа к БД
    BaseData connBD;

    private UserClass[] tableUserClass = null;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public EditUsers(BaseData connBD, CallBack callBack) {
        this.connBD = connBD;
        this.callBack = callBack;
        // загрузка списка пользователей
        readUsersFromBase();
        // инициализация компонентов
        initComponents(); // ****************************************************************
        // деактивация кнопок
        offButtonDeactive();
        offButtonNewUser();
        setVisible(true);
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
        // запись нового пользователя в базу
        writeNewUserToBase();
    }
    private void enterTextSurName() {
        // проверка введенных данных о новом пользователе
        checkFieldsNewUser();
   }
    private void enterTextPassword() {
        // проверка введенных данных о новом пользователе
        checkFieldsNewUser();
    }
    private void selectTableCell() {
        onButtonDeactive();
    }
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //      воздействие на органы управления
    private void onButtonDeactive() {
        buttonDeactive.setEnabled(true);
    }
    private void offButtonDeactive() {
        buttonDeactive.setEnabled(false);
    }
    private void onButtonNewUser() {
        buttonNewUser.setEnabled(true);
    }
    private void offButtonNewUser() {
        buttonNewUser.setEnabled(false);
    }
    // ==========================================
    // чтение из базы в массив
    private void readUsersFromBase() {
        try {
            tableUserClass = connBD.getListUsers(true);
        } catch (Exception e) {
            myLog.log(Level.SEVERE, "чтение списка пользователей", e);
            tableUserClass = new UserClass[0];
        }
    }
    // проверка введенных данных о новом пользователе
    private void checkFieldsNewUser() {
        if ((fieldSurName.getText().length() > 0) && (fieldPassword.getText().length() > 0)) {
            onButtonNewUser();
        } else {
            offButtonNewUser();
        }
    }
    // деактивация выбранного пользователя
    private void deactiveSelectUser() {
        // выбранная строка
        int id = tableUserClass[table.getSelectedRow()].id;
        // доступ к базе
        BaseData1 bdSql = null; //callBack.getBdInterface();
        try {
            // деактивация
            bdSql.deactiveUser(id);
            // обновить таблицу
            readUsersFromBase();
            table.updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // запись нового пользователя в базу
    private void writeNewUserToBase() {
        // доступ к базе
        BaseData1 bdSql = null; //callBack.getBdInterface();
        try {
            // запись
            bdSql.writeNewUser(fieldSurName.getText(), fieldPassword.getText());
            // обновить таблицу
            readUsersFromBase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // очистка полей
        fieldSurName.setText("");
        fieldPassword.setText("");
        // деактивация кнопки
        offButtonNewUser();
        // обновить таблицу
        readUsersFromBase();
        table.updateUI();
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
        callBack.messageCloseEditUsers(false);
    }
    // ==========================================
    // компоненты
    JButton buttonDeactive;
    JButton buttonNewUser;
    JTextField fieldPassword;
    JTextField fieldSurName;
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
            if (tableUserClass != null) {
                row = tableUserClass.length;
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
            if (tableUserClass != null) {
                switch (columnIndex) {
                    case column_name:
                        text = tableUserClass[rowIndex].name;
                        break;
                    case column_datereg:
                        text = dateFormat.format(tableUserClass[rowIndex].date_reg);
                        break;
                    case column_rang:
                        text = "rang";
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
    class BiInt {
        public int index;
        public int width;
        public BiInt(int index, int width) {
            this.index = index;
            this.width = width;
        }
    }
}
