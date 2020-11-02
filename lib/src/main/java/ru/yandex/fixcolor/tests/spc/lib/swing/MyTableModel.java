package ru.yandex.fixcolor.tests.spc.lib.swing;

import javax.swing.table.AbstractTableModel;

public class MyTableModel extends AbstractTableModel {
    public interface Control {
        int getRowCount();
        int getColumnCount();
        Object getValueAt(int rowIndex, int columnIndex);
    }
    private String[] titles;
    public void setTitles(String[] titles) { this.titles = titles; }
    @Override
    public String getColumnName(int column) {
        if (titles == null || titles.length == 0) return "";
        return titles[column];
    }

    @Override
    public int getRowCount() { return 0; }
    @Override
    public int getColumnCount() { return 0; }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) { return null; }
}
