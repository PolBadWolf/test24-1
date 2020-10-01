package org.example.test24.lib.swing;

import javax.swing.table.AbstractTableModel;

public class MyTableModel extends AbstractTableModel {
    public interface Control {
        int getRowCount();
        int getColumnCount();
        Object getValueAt(int rowIndex, int columnIndex);
    }
    private Control control = null;
    private String[] titles;

    public MyTableModel(Control control) {
        this.control = control;
    }

    public void setTitles(String[] titles) { this.titles = titles; }

    @Override
    public int getRowCount() { return control.getRowCount(); }

    @Override
    public int getColumnCount() { return control.getColumnCount(); }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) { return control.getValueAt(rowIndex, columnIndex); }

    @Override
    public String getColumnName(int column) {
        return titles[column];
    }
}
