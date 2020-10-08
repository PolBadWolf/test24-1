package org.example.test24.lib.swing;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.util.Vector;
import java.util.function.Consumer;

public class MyJTable extends JTable {
    private Consumer<JTable> callUpdate = null;
    public String[] titles;

    public void setCallUpdate(Consumer<JTable> callUpdate) {
        this.callUpdate = callUpdate;
    }

    @Override
    public void updateUI() {
        if (callUpdate != null) callUpdate.accept(this);
        super.updateUI();
    }

    public MyJTable() {
    }

    public MyJTable(TableModel dm) {
        super(dm);
    }

    public MyJTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
    }

    public MyJTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
    }

    public MyJTable(int numRows, int numColumns) {
        super(numRows, numColumns);
    }

    public MyJTable(Vector rowData, Vector columnNames) {
        super(rowData, columnNames);
    }

    public MyJTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
    }
}
