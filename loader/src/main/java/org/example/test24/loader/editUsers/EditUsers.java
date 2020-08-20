package org.example.test24.loader.editUsers;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class EditUsers extends JFrame implements EditUsersInterface {
    private EditUsersCallBack callBack;
    private boolean lockStart = true;

    public EditUsers(EditUsersCallBack callBack) {
        this.callBack = callBack;
        lockStart = true;
        // загрузка параметров
        // инитциализация компонентов
        initComponents();
        lockStart = false;
        setVisible(true);
        // ловушка закрытия окна
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {

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

        table = getTable(new SimpleTableModel(), JTable.AUTO_RESIZE_LAST_COLUMN, 0, 360);
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

    }
    private void pushButtonNewUser() {

    }
    private void enterTextSurName() {

    }
    private void enterTextPassword() {

    }
    // <<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>
    //        действия от органов управления
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //      воздействие на органы управления
    // ==========================================
    //  ---
    // ==========================================
    // интерфейсные методы
    @Override
    public void closeFromParent() {

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

        @Override
        public int getRowCount() {
            return 0;
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return null;
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
    private JTable getTable(TableModel tableModel, int auto_resize, int columnIndex, int width) {
        JTable table = new JTable();
        try {
            table.setModel(tableModel);
            table.getTableHeader().setReorderingAllowed(false);
            table.setAutoResizeMode(auto_resize);
            table.getColumnModel().getColumn(columnIndex).setPreferredWidth(width);
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
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pushButtonDeactive();
            }
        });
        return button;
    }
    private JButton getButtonNewUser(String text, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Times New Roman", fontStyle, fontSize));
        button.setBounds(x, y, width, height);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pushButtonNewUser();
            }
        });
        return button;
    }
    private JTextField getFieldSurName(String text, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JTextField textField = new JTextField(text);
        textField.setFont(new Font("Times New Roman", fontStyle, fontSize));
        textField.setBounds(x, y, width, height);
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enterTextSurName();
            }
        });
        return textField;
    }
    private JTextField getFieldPassword(String text, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JTextField textField = new JPasswordField(text);
        textField.setFont(new Font("Times New Roman", fontStyle, fontSize));
        textField.setBounds(x, y, width, height);
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enterTextPassword();
            }
        });
        return textField;
    }
}
