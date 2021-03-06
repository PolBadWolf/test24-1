package ru.yandex.fixcolor.tests.spc.lib.swing;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.geom.Rectangle2D;

public class CreateComponents {
    // ---
    public static JLabel getLabel(String text, Font font, int x, int y, int width, int height, boolean visible, boolean enable) {
        JLabel label = new JLabel();
        label.setFont(font);
        label.setText(text);
        label.setBounds(x, y, width, height);
        label.setVisible(visible);
        label.setEnabled(enable);
        return label;
    }
    public static MLabel getLabel(Container parent, String text, Font font, int x, int y, boolean visible, boolean enable, int horizontalAlignment) {
        int strWidth = parent.getFontMetrics(font).stringWidth(text);
        Rectangle2D r = parent.getFontMetrics(font).getStringBounds(text, parent.getGraphics());
//        int rw = (int) r.getWidth() + 1;
        int rw = strWidth;
        int rh = (int) r.getHeight() + 1;
        MLabel label = new MLabel(horizontalAlignment);
        label.setFont(font);
        label.setBounds(label.corHor(x, strWidth, horizontalAlignment), y, rw, rh);
        label.setText(text);
        label.setVisible(visible);
        label.setEnabled(enable);
        label.setBeginX(x);
        if (parent != null) parent.add(label);
        return label;
    }
    // ---
    final public static int TEXTFIELD = 0;
    final public static int FORMATTEDTEXTFIELD = 1;
    final public static int PASSWORDFIELD = 2;

    public static JTextField getTextField(int typeField, Font font, int x, int y, int width, int height, DocumentFilter filter, ActionListener listener, boolean visible, boolean enable, boolean editable) {
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
        text.setEditable(editable);
        if (editable) {
            if (filter != null) {
                ((PlainDocument) text.getDocument()).setDocumentFilter(filter);
            }
            if (listener != null) {
                text.addActionListener(listener);
            }
        }
        text.setVisible(visible);
        text.setEnabled(enable);
        return text;
    }
    public static JTextField getTextField(int typeField, Font font, int x, int y, int width, int height, DocumentFilter filter, ActionListener listener, boolean visible, boolean enable) {
        return getTextField(
                typeField,
                font,
                x,
                y,
                width,
                height,
                filter,
                listener,
                visible,
                enable,
                true
        );
    }
    // ---
    public static JButton getButton(Container parent, String text, Font font, int x, int y, int width, int height, ActionListener listener, boolean visible, boolean enable) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBounds(x, y, width, height);
        if (listener != null) button.addActionListener(listener);
        button.setVisible(visible);
        button.setEnabled(enable);
        if (parent != null) parent.add(button);
        return button;
    }
    // ---
    public static JTable getTable(int widthLast, TableModel tableModel, ModelTableNameWidth[] nameWidths, DocumentFilter filter, ListSelectionListener listener, boolean visible, boolean enable) {
        MyJTable table = new MyJTable();
        int autoN = 0;
        String[] titles;
        if (nameWidths == null) titles = new String[0];
        else {
            titles = new String[nameWidths.length];
            // остаточная ширина
            for (int i = 0; i < nameWidths.length; i++) {
                titles[i] = nameWidths[i].title;
                if (nameWidths[i].width < 0) {
                    autoN++;
                    continue;
                }
                widthLast -= nameWidths[i].width;
            }
        }
        // авто ширина
        int autoWidth;
        if (autoN == 0) autoWidth = 0;
        else {
            autoWidth = widthLast / autoN;
        }
        //
        table.titles = titles;
        if (tableModel != null) {
            ((MyTableModel) tableModel).setTitles(titles);
            table.setModel(tableModel);
            table.getTableHeader().setReorderingAllowed(false);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            //
            if (nameWidths != null) {
                int w;
                TableColumnModel tableColumnModel = table.getColumnModel();
                for (int i = 0; i < nameWidths.length; i++) {
                    if (nameWidths[i].width < 0) w = autoWidth;
                    else w = nameWidths[i].width;
                    tableColumnModel.getColumn(i).setPreferredWidth(w);
                }
            }
        }
        //
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        if (filter != null) ((PlainDocument) table.getCellEditor()).setDocumentFilter(filter);
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
    public static <T> JComboBox<T> getComboBox(Font font, int x, int y, int width, int height, boolean editable, DocumentFilter filter, ActionListener listener, boolean visible, boolean enable) {
        JComboBox<T> comboBox = new JComboBox<>();
        comboBox.setFont(font);
        comboBox.setBounds(x, y, width, height);
        comboBox.setEditable(editable);
        if (filter != null)
            ((PlainDocument) ((JTextComponent) comboBox.getEditor().getEditorComponent()).getDocument()).setDocumentFilter(filter);
        if (listener != null) comboBox.addActionListener(listener);
        comboBox.setVisible(visible);
        comboBox.setEnabled(enable);
        return comboBox;
    }
    // ---
    public static JPanel getPanel(LayoutManager layoutManager, Font font, String titledBorder, int x, int y, int width, int height, boolean visible, boolean enable) {
        JPanel panel = new JPanel();
        panel.setLayout(layoutManager);
        panel.setBorder(BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)),
                titledBorder,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                font
        ));
        panel.setBounds(x, y, width, height);
        panel.setVisible(visible);
        panel.setEnabled(enable);
        return panel;
    }
    public static MPanelPrintableCap getPanelPrintableCap(LayoutManager layoutManager, Font font, String titledBorder, int x, int y, int width, int height, boolean visible, boolean enable) {
        MPanelPrintableCap panel = new MPanelPrintableCap();
        panel.setLayout(layoutManager);
        panel.setBorder(BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)),
                titledBorder,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                font
        ));
        panel.setBounds(x, y, width, height);
        panel.setVisible(visible);
        panel.setEnabled(enable);
        return panel;
    }
    public static MPanel getMPanel(LayoutManager layoutManager, Font font, String titledBorder, int x, int y, int width, int height, boolean visible, boolean enable) {
        MPanel panel = new MPanel();
        panel.setLayout(layoutManager);
        panel.setBorder(BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)),
                titledBorder,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                font
        ));
        panel.setBounds(x, y, width, height);
        panel.setVisible(visible);
        panel.setEnabled(enable);
        return panel;
    }
    // ---
    public static JCheckBox getJCheckBox(String text, Font font, int x, int y, int width, int height, boolean selected, ActionListener listener, boolean visible, boolean enable) {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setFont(font);
        checkBox.setText(text);
        checkBox.setSelected(selected);
        checkBox.setBounds(x, y, width, height);
        if (listener != null) checkBox.addActionListener(listener);
        return checkBox;
    }
    // ---
    public static JFrame getFrame(String title, int width, int height, boolean resizable, LayoutManager layoutManager, WindowListener listener) {
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setPreferredSize(new Dimension(width, height));
        frame.setResizable(resizable);
        frame.setLayout(layoutManager);
        frame.addWindowListener(listener);
        return frame;
    }
    // ---

}