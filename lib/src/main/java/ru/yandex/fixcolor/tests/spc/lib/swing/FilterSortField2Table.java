package ru.yandex.fixcolor.tests.spc.lib.swing;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FilterSortField2Table<T> {
    public interface CallBackF <R> {
        Object decoder(int columnIndex, R result);
        void selectRow(int rowIndex);
    }
    private final JTextField textField;
    private final JTable table;
    private List<T> collections;
    //private final CallBack<T> callBack;
    private List<BoundExtractedResult<T>> results;
    private String textF;

    public void setCollections(T[] collections) {
        this.collections = Arrays.asList(collections);
        fiterDoc();
    }

    public FilterSortField2Table(JTextField textField, JTable table, T[] collections, CallBackF<T> callBack) {
        this.textField = textField;
        this.table = table;
        this.collections = Arrays.asList(collections);
        //this.callBack = callBack;
        textF = "";
        //
        MyTableModel tableModel = new MyTableModel() {
            @Override
            public int getRowCount() {
                //return super.getRowCount();
                if (results == null) return 0;
                return results.size();
            }

            @Override
            public int getColumnCount() {
                return 3;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                //return super.getValueAt(rowIndex, columnIndex);
                if (rowIndex < 0 || columnIndex < 0) return  null;
                return callBack.decoder(columnIndex, results.get(rowIndex).getReferent());
            }
        };
        tableModel.setTitles(((MyJTable) table).titles);
        table.setModel(tableModel);
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter(){
            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                super.remove(fb, offset, length);
                textF = textF.substring(0, offset);
                fiterDoc();
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                super.replace(fb, offset, length, text, attrs);
                textF = textF.substring(0, offset) + text;
                fiterDoc();
            }
        });
        textField.addActionListener(e -> {
            if (results == null || results.size() == 0) return;
            callBack.selectRow(results.get(0).getIndex());
        });
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                if (results == null || results.size() == 0) return;
                int row = table.getSelectedRow();
                if (row < 0) return;
                callBack.selectRow(results.get(row).getIndex());
            }
        });
        fiterDoc();
    }
    //
    private void fiterDoc() {
        results = FuzzySearch.extractSorted(
                (String) textField.getText(),
                collections,
                Objects::toString
        );
        table.updateUI();
    }
}
