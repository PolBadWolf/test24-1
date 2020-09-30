package org.example.test24.lib.swing;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

public class CreateComponents {
    // ---
    public static JLabel getjLabel(String text, Font font, int x, int y, int width, int height, boolean visible, boolean enable) {
        JLabel label = new JLabel();
        label.setFont(font);
        label.setText(text);
        label.setBounds(x, y, width, height);
        label.setVisible(visible);
        label.setEnabled(enable);
        return label;
    }
    // ---
    final public static int TEXTFIELD = 0;
    final public static int FORMATTEDTEXTFIELD = 1;
    final public static int PASSWORDFIELD = 2;
    public static JTextField getTextField(int typeField, Font font, int x, int y, int width, int height, DocumentFilter filter, ActionListener listener, boolean visible, boolean enable) {
        JTextField text;
        switch (typeField) {
            case TEXTFIELD:
                text = new JTextField();
                break;
            case FORMATTEDTEXTFIELD:
                text = new JFormattedTextField();
                break;
            case PASSWORDFIELD:
                text = new JPasswordField();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + typeField);
        }
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
    // ---
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
    // ---
    public static JTable getTable(int widthLast, TableModel tableModel, ModelTableNameWidth[] nameWidths, ListSelectionListener listener, boolean visible, boolean enable) {
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
        table.setVisible(visible);
        table.setEnabled(enable);
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
    // ---
    public static JScrollPane getScrollPane(int x, int y, int width, int height, Component component, boolean visible, boolean enable) {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(x, y, width, height);
        if (component != null) scrollPane.setViewportView(component);
        scrollPane.setVisible(visible);
        scrollPane.setEnabled(enable);
        return scrollPane;
    }
    // ---
    public static <T> JComboBox<T> getComboBox(Font font, int x, int y, int width, int height, boolean editable, ItemListener listener, boolean visible, boolean enable) {
        JComboBox<T> comboBox = new JComboBox<>();
        comboBox.setFont(font);
        comboBox.setBounds(x, y, width, height);
        comboBox.setEditable(editable);
        if (listener != null) comboBox.addItemListener(listener);
        comboBox.setVisible(visible);
        comboBox.setEnabled(enable);
        return comboBox;
    }
    // ---
    public static JPanel getPanel(LayoutManager layoutManager, String titledBorder, int x, int y, int width, int height, boolean visible, boolean enable) {
        JPanel panel = new JPanel();
        panel.setLayout(layoutManager);
        panel.setBorder(BorderFactory.createTitledBorder(titledBorder));
        panel.setBounds(x, y, width, height);
        panel.setVisible(visible);
        panel.setEnabled(enable);
        return panel;
    }
    // ---

}