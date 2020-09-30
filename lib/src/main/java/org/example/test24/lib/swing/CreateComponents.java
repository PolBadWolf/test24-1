package org.example.test24.lib.swing;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionListener;

public class CreateComponents {
    public static JLabel getjLabel(String text, Font font, int x, int y, int width, int height, boolean visible, boolean enable) {
        JLabel label = new JLabel();
        label.setFont(font);
        label.setText(text);
        label.setBounds(x, y, width, height);
        label.setVisible(visible);
        label.setEnabled(enable);
        return label;
    }

    public static JTextField getTextField(Font font, int x, int y, int width, int height, DocumentFilter filter, ActionListener listener, boolean visible, boolean enable) {
        JTextField text = new JTextField();
        text.setFont(font);
        text.setBounds(x, y, width, height);
        if (filter != null) {
            ((PlainDocument) text.getDocument()).setDocumentFilter(filter);
        }
        if (listener != null) {
            text.addActionListener(listener);
        }
        text.setVisible(visible);
        text.setEnabled(enable);
        return text;
    }

    public static JButton getButton(String text, Font font, int x, int y, int width, int height, ActionListener listener, boolean visible, boolean enable) {
        JButton button = new JButton();
        button.setFont(font);
        button.setText(text);
        button.setBounds(x, y, width, height);
        if (listener != null) button.addActionListener(listener);
        button.setVisible(visible);
        button.setEnabled(enable);
        return button;
    }

    public static JTable getTable(int widthLast, TableModel tableModel, ModelTableNameWidth[] nameWidths, ListSelectionListener listener) {
        JTable table = new JTable();
        int autoN = 0;
        String[] titles = new String[nameWidths.length];
        // остаточная ширина
        for (int i = 0; i < nameWidths.length; i++) {
            titles[i] = nameWidths[i].title;
            if (nameWidths[i].width < 0) {
                autoN++;
                continue;
            }
            widthLast -= nameWidths[i].width;
        }
        // авто ширина
        int autoWidth;
        if (autoN == 0) autoWidth = 0;
        else {
            autoWidth = widthLast / autoN;
        }
        //
        ((MyTableModel) tableModel).setTitles(titles);
        table.setModel(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        //
        int w_i = 0;
        int w;
        TableColumnModel tableColumnModel = table.getColumnModel();
        for (int i = 0; i < nameWidths.length; i++) {
            if (nameWidths[i].width < 0) w = autoWidth;
            else w = nameWidths[i].width;
            tableColumnModel.getColumn(i).setPreferredWidth(w);
            w_i += w;
        }
        //
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        if (listener != null) table.getSelectionModel().addListSelectionListener(listener);
        return table;
    }

    public static class ModelTableNameWidth {
        public String title;
        public int width;

        public ModelTableNameWidth(String title, int width) {
            this.title = title;
            this.width = width;
        }
    }
}