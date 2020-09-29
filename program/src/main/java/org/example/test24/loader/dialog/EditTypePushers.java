package org.example.test24.loader.dialog;

import org.example.test24.bd.BaseData;
import org.example.test24.bd.BaseDataException;
import org.example.test24.bd.usertypes.TypePusher;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;

import static org.example.test24.lib.MyLogger.myLog;

public class EditTypePushers {
    interface CallBack {

    }

    // ***********************************************************************
    private JFrame frame;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JScrollPane scrollPane;
    private JTable tableTypePushers;
    private JTextField textName;
    private JTextField textForce;
    private JTextField textMove;
    private JTextField textUnclenching;
    private JButton buttonClear;
    private JButton buttonEdit;
    private JButton buttonAdd;
    // ***********************************************************************

    private CallBack callBack;
    private BaseData connBD;
    // список тип толкателей
    private TypePusher[] typePushers;

    public EditTypePushers(CallBack callBack, BaseData connBD) {
        this.callBack = callBack;
        this.connBD = connBD;
        // загрузка списка типа толкателей
        try {
            typePushers = getListTypePushers();
        } catch (BaseDataException e) {
            myLog.log(Level.SEVERE, "ошибка получения списка типа толкателей", e);
            // в этом месте выход
            return;
        }
        // инициация компонентов
        initComponents();
    }
    // загрузка списка типа компонентов
    private TypePusher[] getListTypePushers() throws BaseDataException {
        TypePusher[] typePushers = new TypePusher[0];
        typePushers = connBD.getListTypePushers(true);
        return typePushers;
    }
    // инициация компонентов
    private void initComponents() {
        frame = new JFrame();
        frame.setResizable(false);
        frame.setPreferredSize(new Dimension(640, 480));
        frame.setLayout(null);
        // ---- надписи
        jLabel1 = getjLabel("Тип толкателя", new Font("Times New Roman", 0, 18), 120, 230, 210, 24);
        frame.add(jLabel1);
        jLabel2 = getjLabel("Усилие на штоке (кг)", new Font("Times New Roman", 0, 18), 30, 310, 190, 24);
        frame.add(jLabel2);
        jLabel3 = getjLabel("Ход штока (мм)", new Font("Times New Roman", 0, 18), 40, 350, 140, 24);
        frame.add(jLabel3);
        jLabel4 = getjLabel("Время разжатия (сек)", new Font("Times New Roman", 0, 18), 40, 385, 140, 24);
        frame.add(jLabel4);
        // ---- поля ввода данных
        textName = getTextField(new Font("Times New Roman", 0, 18), 30, 270,400, 24, null);
        frame.add(textName);
        textForce = getTextField(new Font("Times New Roman", 0, 18), 290, 310, 140, 24, new FilterTextDigit());
        frame.add(textForce);
        textMove = getTextField(new Font("Times New Roman", 0, 18), 290, 360, 140, 24, new FilterTextDigit());
        frame.add(textMove);
        textUnclenching = getTextField(new Font("Times New Roman", 0, 18), 290, 400, 140, 24, new FilterTextDigit());
        frame.add(textUnclenching);
        // ---- кнопки
        buttonClear = getButton("Очистить", new Font("Times New Roman", 0, 14), 470, 320, 120, 25, this::buttonClearAction);
        frame.add(buttonClear);
        buttonEdit = getButton("Редактировать", new Font("Times New Roman", 0, 14), 470, 360, 120, 25, this::buttonEditAction);
        frame.add(buttonEdit);
        buttonClear = getButton("Добавить", new Font("Times New Roman", 0, 14), 470, 400, 120, 25, this::buttonAddAction);
        frame.add(buttonClear);
        // ---- таблица
        tableTypePushers = getTable(640 - 17, new TableModelTypePushers(),
                new ModelTableNameWidth[]{
                        new ModelTableNameWidth("Тип толкателя", -1),
                        new ModelTableNameWidth("Усилие на штоке (кг)", 130),
                        new ModelTableNameWidth("Ход штока (мм)", 100),
                        new ModelTableNameWidth("Время разжатия (сек)", 130)
                },
                this::tableTypePushersChanged
        );
        scrollPane = getScrollPane(0, 0, 640, 220, tableTypePushers);
        frame.add(scrollPane);
        // ----
        frame.pack();
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
            }
        });
    }
    // -----------
    class FilterTextDigit extends DocumentFilter {
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text.length() == 1) {
                int x = text.codePointAt(0);
                if (x < '0') return;
                if (x > '9') return;
            }
            super.replace(fb, offset, length, text, attrs);
        }
    }
    // -----------
    private JLabel getjLabel(String text, Font font, int x, int y, int width, int height) {
        JLabel label = new JLabel();
        label.setFont(font);
        label.setText(text);
        label.setBounds(x, y, width, height);
        return label;
    }
    private JTextField getTextField(Font font, int x, int y, int width, int height, DocumentFilter filter) {
        JTextField text = new JTextField();
        text.setFont(font);
        text.setBounds(x, y, width, height);
        if (filter != null) { ((PlainDocument) text.getDocument()).setDocumentFilter(filter); }
        return text;
    }
    private JButton getButton(String text, Font font, int x, int y, int width, int height, ActionListener listener) {
        JButton button = new JButton();
        button.setFont(font);
        button.setText(text);
        button.setBounds(x, y, width, height);
        if (listener != null) button.addActionListener(listener);
        return button;
    }
    private JScrollPane getScrollPane(int x, int y, int width, int height, Component component) {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(x, y, width, height);
        if (component != null) scrollPane.setViewportView(component);
        return scrollPane;
    }
    private JTable getTable(int widthLast, TableModel tableModel, ModelTableNameWidth[] nameWidths, ListSelectionListener listener) {
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
        ((TableModelTypePushers) tableModel).setTitles(titles);
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
    // ----------- кнопки
    // button clear
    private void buttonClearAction(ActionEvent e) {

    }
    // button edit
    private void buttonEditAction(ActionEvent e) {

    }
    // button add
    private void buttonAddAction(ActionEvent e) {

    }
    // ----------- таблицы
    private void tableTypePushersChanged(ListSelectionEvent e) {

    }
    // -----------
    class TableModelTypePushers extends AbstractTableModel {
        private String[] titles;

        public void setTitles(String[] titles) {
            this.titles = titles;
        }

        @Override
        public int getRowCount() {
            int row = 0;
            if (typePushers != null) row = typePushers.length;
            return row;
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            String text = "";
            TypePusher typePusher = typePushers[rowIndex];
            switch (columnIndex) {
                case 0:
                    text = typePusher.loggerTypePusher.nameType;
                    break;
                case 1:
                    text = String.valueOf(typePusher.loggerTypePusher.forceNominal);
                case 2:
                    text = String.valueOf(typePusher.loggerTypePusher.moveNominal);
                case 3:
                    text = String.valueOf(typePusher.loggerTypePusher.unclenchingTime);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + columnIndex);
            }
            return text;
        }

        @Override
        public String getColumnName(int column) {
            return titles[column];
        }
    }
}
