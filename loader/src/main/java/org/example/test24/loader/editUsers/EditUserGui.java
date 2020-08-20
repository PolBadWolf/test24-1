package org.example.test24.loader.editUsers;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.EventListener;

class EditUserGui extends JFrame {

    static EditUserGui init(EditUserInterface callBackLogic) {
        EditUserGui[] frame = new EditUserGui[1];
        new Thread(()->{
            try {
                SwingUtilities.invokeAndWait(()->{
                    frame[0] = new EditUserGui(callBackLogic);
                    frame[0].setVisible(true);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        try {
            while (frame[0] == null) {
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return frame[0];
    }

    private EditUserInterface callBackLogic;

    private EditUserGui(EditUserInterface callBackLogic) throws HeadlessException {
        this.callBackLogic = callBackLogic;
        initComponents();
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                callBackLogic.closeFromGui();
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
        /*for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }
                break;
            }
        }*/
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
    // ===================
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
        return button;
    }
    private JButton getButtonNewUser(String text, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Times New Roman", fontStyle, fontSize));
        button.setBounds(x, y, width, height);
        return button;
    }
    private JTextField getFieldSurName(String text, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JTextField textField = new JTextField(text);
        textField.setFont(new Font("Times New Roman", fontStyle, fontSize));
        textField.setBounds(x, y, width, height);
        return textField;
    }
    private JTextField getFieldPassword(String text, int fontStyle, int fontSize, int x, int y, int width, int height) {
        JTextField textField = new JPasswordField(text);
        textField.setFont(new Font("Times New Roman", fontStyle, fontSize));
        textField.setBounds(x, y, width, height);
        return textField;
    }
    // ===================
    JButton buttonDeactive;
    JButton buttonNewUser;
    JTextField fieldPassword;
    JTextField fieldSurName;
    JLabel label_password;
    JLabel label_surName;
    JLabel label_title;
    JScrollPane scroll_table;
    JTable table;
    // ===================
    class SimpleTableModel extends AbstractTableModel {
        @Override
        public int getRowCount() {
            JTable table;
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

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return super.isCellEditable(rowIndex, columnIndex);
        }

        @Override
        public String getColumnName(int column) {
            return super.getColumnName(column);
        }

        @Override
        public int findColumn(String columnName) {
            return super.findColumn(columnName);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return super.getColumnClass(columnIndex);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            super.setValueAt(aValue, rowIndex, columnIndex);
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
            super.addTableModelListener(l);
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
            super.removeTableModelListener(l);
        }

        @Override
        public TableModelListener[] getTableModelListeners() {
            return super.getTableModelListeners();
        }

        @Override
        public void fireTableDataChanged() {
            super.fireTableDataChanged();
        }

        @Override
        public void fireTableStructureChanged() {
            super.fireTableStructureChanged();
        }

        @Override
        public void fireTableRowsInserted(int firstRow, int lastRow) {
            super.fireTableRowsInserted(firstRow, lastRow);
        }

        @Override
        public void fireTableRowsUpdated(int firstRow, int lastRow) {
            super.fireTableRowsUpdated(firstRow, lastRow);
        }

        @Override
        public void fireTableRowsDeleted(int firstRow, int lastRow) {
            super.fireTableRowsDeleted(firstRow, lastRow);
        }

        @Override
        public void fireTableCellUpdated(int row, int column) {
            super.fireTableCellUpdated(row, column);
        }

        @Override
        public void fireTableChanged(TableModelEvent e) {
            super.fireTableChanged(e);
        }

        @Override
        public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
            return super.getListeners(listenerType);
        }
    }
}
