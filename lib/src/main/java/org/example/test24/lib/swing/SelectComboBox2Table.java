package org.example.test24.lib.swing;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectComboBox2Table<T> {
    interface CallBack {
        void enterComboBox(String lastLogin);
    }
    private final JTable table;
    private final List<T> cs;
    //
    private boolean iKeep;
    private boolean comboLockSelect;
    private boolean comboLockListener;
    private String textFilter;
    private List<BoundExtractedResult<T>> resultList;
    private T singleT;
    private String lostLogin;
    private CallBack callBack;
    //
    private final ItemListener[] comboBoxItemListeners;

    public SelectComboBox2Table(JComboBox<T> comboBox, JTable table, T[] cs, CallBack callBack) {
        this.table = table;
        this.cs = Arrays.asList(cs);
        this.callBack = callBack;
        //
        iKeep = false;
        textFilter = "";
        resultList= new ArrayList<>();
        ((PlainDocument) ((JTextComponent) comboBox.getEditor().getEditorComponent()).getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                super.remove(fb, offset, length);
                textFilter = textFilter.substring(0, offset);
                comboBoxFilterDo();
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                super.replace(fb, offset, length, text, attrs);
                textFilter = textFilter.substring(0, offset) + text;
                comboBoxFilterDo();
            }
        });
        comboBoxItemListeners = comboBox.getItemListeners();
        for (ItemListener il : comboBoxItemListeners) comboBox.removeItemListener(il);
        comboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.DESELECTED) return;
            if (comboLockSelect) return;
            comboLockSelect = true;
            lostLogin = (String) comboBox.getSelectedItem();
            if (resultList.size() > 0) {
                Object o = resultList.get(0).getReferent();
                comboBox.setSelectedItem(o);
                table.clearSelection();
                table.setVisible(false);
            }
            comboLockSelect = false;
        });
        comboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.DESELECTED) return;
            if (comboLockSelect) return;
            if (comboLockListener) return;
            comboLockListener = true;
            for (ItemListener il : comboBoxItemListeners) il.itemStateChanged(e);
            comboLockListener = false;
        });
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) return;
            int sRow = table.getSelectedRow();
            if (sRow < 0) return;
            Object o = resultList.get(sRow).getReferent();
            comboBox.setSelectedItem(o);
            table.clearSelection();
            table.setVisible(false);
        });
        table.setModel(new MyTableModel() {
            @Override
            public int getRowCount() {
                int row = resultList.size();
                if (resultList.size() == 0) row = 1;
                return row;
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (resultList.size() == 0) return "";
                singleT = resultList.get(rowIndex).getReferent();
                return singleT.toString();
            }
        });
        ((MyJTable) table).setCallUpdate(jTable -> {
            resultList = FuzzySearch.extractTop(
                    textFilter,
                    this.cs,
                    Object::toString,
                    7
            );
            int row = table.getRowCount();
            if (row == 0) row = 1;
            table.setSize(
                    table.getWidth(),
                    table.getRowHeight() * row
            );
        });
    }
    private void comboBoxFilterDo() {
        if (!iKeep) {
            iKeep = true;
            return;
        }
        if (!table.isVisible()) table.setVisible(true);
        table.updateUI();
    }

    public void setiKeep(boolean iKeep) {
        this.iKeep = iKeep;
    }

    public T getResultFist() {
        if (resultList == null || resultList.size() == 0) return null;
        return resultList.get(0).getReferent();
    }

    public String getLostLogin() {
        return lostLogin;
    }
}

