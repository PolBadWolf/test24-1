package org.example.test24.loader.editUsers;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.EventListener;

class EditUserGui extends JFrame {

    static EditUserGui init(EditUserInt callBackLogic) {
        EditUserGui[] frame = new EditUserGui[1];
        try {
            SwingUtilities.invokeAndWait(()->{
                frame[0] = new EditUserGui(callBackLogic);
                frame[0].initComponents();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return frame[0];
    }

    private EditUserInt callBackLogic;

    private EditUserGui(EditUserInt callBackLogic) throws HeadlessException {
        this.callBackLogic = callBackLogic;
    }

    private void initComponents() {
        setPreferredSize(new Dimension(640, 480));
        setLayout(null);

        pack();
    }

    class Simple extends AbstractTableModel {
        @Override
        public int getRowCount() {
            JTable table;
            return 0;
        }

        @Override
        public int getColumnCount() {
            return 0;
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
