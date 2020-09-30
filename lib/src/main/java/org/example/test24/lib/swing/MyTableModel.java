package org.example.test24.lib.swing;

import javax.swing.table.AbstractTableModel;

public class MyTableModel extends AbstractTableModel {
    public interface GetRowCount { int getRowCount(); }
    public interface GetColumnCount { int getColumnCount(); }
    public interface GetValueAt { Object getValueAt(int rowIndex, int columnIndex); }

    private GetRowCount getRowCount;
    private GetColumnCount getColumnCount;
    private GetValueAt getValueAt;
    private String[] titles;

    public MyTableModel(GetRowCount getRowCount, GetColumnCount getColumnCount, GetValueAt getValueAt) {
        this.getRowCount = getRowCount;
        this.getColumnCount = getColumnCount;
        this.getValueAt = getValueAt;
    }

    public void setTitles(String[] titles) { this.titles = titles; }

    @Override
    public int getRowCount() { return getRowCount.getRowCount(); }

    @Override
    public int getColumnCount() { return getColumnCount.getColumnCount(); }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) { return getValueAt.getValueAt(rowIndex, columnIndex); }

    @Override
    public String getColumnName(int column) {
        return titles[column];
    }
}
