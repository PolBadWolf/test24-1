package ru.yandex.fixcolor.tests.spc.lib.swing;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;

import javax.swing.*;
import javax.swing.text.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectComboBox2Table_Top<T> {
    private final JTable table;
    private List<T> collection;
    private final int rowMax;
    //
    private boolean comboLockSelect;
    private String textFilter;
    private List<BoundExtractedResult<T>> resultList;
    private T singleT;
    private boolean lock = true;
    //

    public void setCollections(T[] collection) {
        this.collection = Arrays.asList(collection);
        doFilter();
    }

    public SelectComboBox2Table_Top(JComboBox<T> comboBox, JTable table, T[] collection, int rowMax, String lostName) {
        this.table = table;
        this.collection = Arrays.asList(collection);
        this.rowMax = rowMax;
        //
        //iKeep = false;
        textFilter = "";
        resultList= new ArrayList<>();
        ((PlainDocument) ((JTextComponent) comboBox.getEditor().getEditorComponent()).getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                super.remove(fb, offset, length);
                if (lock) return;
                textFilter = textFilter.substring(0, offset);
                comboBoxFilterDo();
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                super.replace(fb, offset, length, text, attrs);
                if (lock) return;
                textFilter = textFilter.substring(0, offset) + text;
                comboBoxFilterDo();
            }
        });
        comboBox.addActionListener(e -> {
            if (lock) return;
            if (comboLockSelect) {
                comboLockSelect = false;
                return;
            }
            comboLockSelect = true;
            Object o;
            try {
                o = comboBox.getSelectedItem();
                if (o != null) {
                    if (o.getClass().getSimpleName().equals("String")) {
                        String s = (String) o;
                        if (s.equals(lostName)) {
                            return;
                        }
                    }
                    if (resultList.size() > 0) {
                        comboBox.setSelectedItem(resultList.get(0).getReferent());
                    }
                } else {
                    comboBox.setSelectedItem(null);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                table.clearSelection();
                table.setVisible(false);
            }
            comboLockSelect = false;
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
        MyTableModel tableModel = new MyTableModel() {
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
        };
        tableModel.setTitles(((MyJTable) table).titles);
        table.setModel(tableModel);
        ((MyJTable) table).setCallBack(() -> {
            doFilter();
            int row = table.getRowCount();
            if (row == 0) row = 1;
            table.setSize(
                    table.getWidth(),
                    table.getRowHeight() * row
            );
        });
    }
    private void comboBoxFilterDo() {
        if (!table.isVisible()) table.setVisible(true);
        table.updateUI();
    }
    private void doFilter() {
        resultList = FuzzySearch.extractTop(
                textFilter,
                this.collection,
                Object::toString,
                rowMax
        );
    }

//    public void setiKeep(boolean iKeep) {
//        this.iKeep = iKeep;
//    }

    public T getResultFist() {
        if (resultList == null || resultList.size() == 0) return null;
        return resultList.get(0).getReferent();
    }

    public void setLock(boolean lock) {
        this.lock = lock;
        doFilter();
    }
}

