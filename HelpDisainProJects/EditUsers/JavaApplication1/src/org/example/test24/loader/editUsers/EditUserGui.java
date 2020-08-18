/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.test24.loader.editUsers;

import java.util.Enumeration;
import java.util.EventListener;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Gumerovmn
 */
public class EditUserGui extends javax.swing.JFrame {

    /**
     * Creates new form EditUserGui
     */
    public EditUserGui() {
        initComponents();
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        TableColumnModel columnModel = jTable1.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(360);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        label_title = new javax.swing.JLabel();
        label_surName = new javax.swing.JLabel();
        label_password = new javax.swing.JLabel();
        scroll_table = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        buttonDeactive = new javax.swing.JButton();
        buttonNewUser = new javax.swing.JButton();
        fieldSurName = new javax.swing.JTextField();
        fieldPassword = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Редактор пользователей");
        setPreferredSize(new java.awt.Dimension(640, 480));
        getContentPane().setLayout(null);

        label_title.setFont(new java.awt.Font("Times New Roman", 1, 28)); // NOI18N
        label_title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_title.setText("Редактор пользователей");
        getContentPane().add(label_title);
        label_title.setBounds(160, 10, 310, 33);

        label_surName.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        label_surName.setText("ФИО");
        getContentPane().add(label_surName);
        label_surName.setBounds(80, 330, 60, 30);

        label_password.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        label_password.setText("Пароль");
        getContentPane().add(label_password);
        label_password.setBounds(80, 390, 60, 30);

        table.setModel(new SimpleModel());
        table.getTableHeader().setReorderingAllowed(false);
        scroll_table.setViewportView(table);

        getContentPane().add(scroll_table);
        scroll_table.setBounds(20, 50, 580, 190);

        buttonDeactive.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        buttonDeactive.setText("деактивация");
        getContentPane().add(buttonDeactive);
        buttonDeactive.setBounds(493, 260, 100, 40);

        buttonNewUser.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        buttonNewUser.setText("Новый пользователь");
        getContentPane().add(buttonNewUser);
        buttonNewUser.setBounds(450, 380, 140, 40);

        fieldSurName.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        fieldSurName.setText("jTextField1");
        getContentPane().add(fieldSurName);
        fieldSurName.setBounds(140, 330, 450, 30);

        fieldPassword.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        fieldPassword.setText("jTextField1");
        getContentPane().add(fieldPassword);
        fieldPassword.setBounds(140, 390, 250, 30);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EditUserGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditUserGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditUserGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditUserGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EditUserGui().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonDeactive;
    private javax.swing.JButton buttonNewUser;
    private javax.swing.JTextField fieldPassword;
    private javax.swing.JTextField fieldSurName;
    private javax.swing.JLabel label_password;
    private javax.swing.JLabel label_surName;
    private javax.swing.JLabel label_title;
    private javax.swing.JScrollPane scroll_table;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables

    class SimpleModel extends AbstractTableModel {
        @Override
        public int getRowCount() {
            JTable table;
            return 20;
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
            String name = "";
            switch(column) {
                case 0:
                    name = "ФИО";
                    break;
                case 1:
                    name = "регистрация";
                    break;
                case 2:
                    name = "отмена";
                break;
            }
            return name;
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
