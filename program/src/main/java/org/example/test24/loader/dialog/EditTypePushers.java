package org.example.test24.loader.dialog;

import org.example.test24.bd.BaseData;
import org.example.test24.bd.BaseDataException;
import org.example.test24.bd.usertypes.TypePusher;
import org.example.test24.lib.swing.MySwingUtil;
import org.example.test24.lib.swing.SaveEnableComponents;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
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
        long getCurrentId_loggerUser();
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
    private JButton buttonDelete;
    private JButton buttonClear;
    private JButton buttonEdit;
    private JButton buttonAdd;
    // ***********************************************************************

    private CallBack callBack;
    private BaseData connBD;
    // список тип толкателей
    private TypePusher[] typePushers;
    private TypePusher editTypePusher = null;
    private long currentId_loggerUserEdit;
    SaveEnableComponents saveEnableComponents;

    public EditTypePushers(CallBack callBack, BaseData connBD) {
        this.callBack = callBack;
        this.connBD = connBD;
        currentId_loggerUserEdit = callBack.getCurrentId_loggerUser();
        // загрузка списка типа толкателей
        try {
            typePushers = getListTypePushers();
        } catch (BaseDataException e) {
            myLog.log(Level.SEVERE, "ошибка получения списка типа толкателей", e);
            // в этом месте выход
            //return;
        }
        // инициация компонентов
        initComponents();
        //
        saveEnableComponents = new SaveEnableComponents(new Component[]{
                frame,
                textName,
                textForce,
                textMove,
                textUnclenching,
                buttonDelete,
                buttonClear,
                buttonEdit,
                buttonAdd
        });
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
        textMove = getTextField(new Font("Times New Roman", 0, 18), 290, 350, 140, 24, new FilterTextDigit());
        frame.add(textMove);
        textUnclenching = getTextField(new Font("Times New Roman", 0, 18), 290, 385, 140, 24, new FilterTextDigit());
        frame.add(textUnclenching);
        // ---- кнопки
        buttonDelete = getButton("Удалить", new Font("Times New Roman", 0, 14), 470, 270, 120, 25, this::buttonDeleteAction);
        frame.add(buttonDelete);
        buttonClear = getButton("Очистить", new Font("Times New Roman", 0, 14), 470, 310, 120, 25, this::buttonClearAction);
        frame.add(buttonClear);
        buttonEdit = getButton("Редактировать", new Font("Times New Roman", 0, 14), 470, 350, 120, 25, this::buttonEditAction);
        frame.add(buttonEdit);
        buttonAdd = getButton("Добавить", new Font("Times New Roman", 0, 14), 470, 385, 120, 25, this::buttonAddAction);
        frame.add(buttonAdd);
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
    // button delete
    private void buttonDeleteAction(ActionEvent e) {
        if (editTypePusher == null) return;
        try {
            connBD.deativateTypePusher(currentId_loggerUserEdit, editTypePusher);
        } catch (BaseDataException baseDataException) {
            baseDataException.printStackTrace();
            return;
        }
        // обновить список
        try {
            typePushers = getListTypePushers();
        } catch (BaseDataException baseDataException) {
            baseDataException.printStackTrace();
            return;
        }
        tableTypePushers.updateUI();
        clearFields();
        tableTypePushers.getSelectionModel().clearSelection();
    }
    // button clear
    private void buttonClearAction(ActionEvent e) {
        clearFields();
        tableTypePushers.getSelectionModel().clearSelection();
    }
    // button edit
    private void buttonEditAction(ActionEvent e) {
        if (editTypePusher == null) return;
        if (
                textName.getText().length() == 0
                || textForce.getText().length() == 0
                || textMove.getText().length() == 0
                || textUnclenching.getText().length() == 0
        ) {
            saveEnableComponents.save();
            saveEnableComponents.offline();
            MySwingUtil.showMessage(frame, "редактирование", "не все поля заполнены", 5_000, o -> {
                saveEnableComponents.restore();
                frame.requestFocus();
            });
            return;
        }
        // заменяемые данные
        String v_typeName = textName.getText();
        int v_force = Integer.parseInt(textForce.getText());
        int v_move = Integer.parseInt(textMove.getText());
        int v_unclenching = Integer.parseInt(textUnclenching.getText());
        // проверка на повтор
        if (!v_typeName.equals(editTypePusher.loggerTypePusher.nameType)) {
            boolean flAgain = false;
            for (int i = 0; i < typePushers.length; i++) {
                if (!v_typeName.equals(typePushers[i].loggerTypePusher.nameType)) continue;
                flAgain = true;
                break;
            }
            if (flAgain) {
                saveEnableComponents.save();
                saveEnableComponents.offline();
                MySwingUtil.showMessage(frame,
                        "редактирование типа гидротолкателя",
                        "такой тип уже существует",
                        5_000,
                        o -> {
                            saveEnableComponents.restore();
                            frame.requestFocus();
                        }
                );
                return;
            }
        }
        // обновление
        try {
            connBD.updateTypePusher(editTypePusher, currentId_loggerUserEdit,
                    v_typeName,
                    v_force,
                    v_move,
                    v_unclenching
            );
            tableTypePushers.updateUI();
        } catch (BaseDataException baseDataException) {
            baseDataException.printStackTrace();
        } finally {
            clearFields();
            tableTypePushers.getSelectionModel().clearSelection();
        }
    }
    // button add
    private void buttonAddAction(ActionEvent e) {
        if (
                textName.getText().length() == 0
                        || textForce.getText().length() == 0
                        || textMove.getText().length() == 0
                        || textUnclenching.getText().length() == 0
        ) {
            saveEnableComponents.save();
            saveEnableComponents.offline();
            MySwingUtil.showMessage(frame, "редактирование", "не все поля заполнены", 5_000, o -> {
                saveEnableComponents.restore();
                frame.requestFocus();
            });
            return;
        }
        // заменяемые данные
        String v_typeName = textName.getText();
        int v_force = Integer.parseInt(textForce.getText());
        int v_move = Integer.parseInt(textMove.getText());
        int v_unclenching = Integer.parseInt(textUnclenching.getText());
        // проверка на повтор
        {
            boolean flAgain = false;
            for (int i = 0; i < typePushers.length; i++) {
                if (!v_typeName.equals(typePushers[i].loggerTypePusher.nameType)) continue;
                flAgain = true;
                break;
            }
            if (flAgain) {
                saveEnableComponents.save();
                saveEnableComponents.offline();
                MySwingUtil.showMessage(frame,
                        "редактирование типа гидротолкателя",
                        "такой тип уже существует",
                        5_000,
                        o -> {
                            saveEnableComponents.restore();
                            frame.requestFocus();
                        }
                );
                return;
            }
        }
        // добавление
        try {
            connBD.writeNewTypePusher(currentId_loggerUserEdit,
                    v_typeName,
                    v_force,
                    v_move,
                    v_unclenching
            );
        } catch (BaseDataException baseDataException) {
            baseDataException.printStackTrace();
        } finally {
            clearFields();
            tableTypePushers.getSelectionModel().clearSelection();
        }
        try {
            typePushers = getListTypePushers();
        } catch (BaseDataException baseDataException) {
            baseDataException.printStackTrace();
        }
        tableTypePushers.updateUI();
        //
    }
    private void clearFields() {
        editTypePusher = null;
        textName.setText("");
        textForce.setText("");
        textMove.setText("");
        textUnclenching.setText("");
    }
    // ----------- таблицы
    private void tableTypePushersChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) return;
        textName.setText(editTypePusher.loggerTypePusher.nameType);
        textForce.setText(String.valueOf(editTypePusher.loggerTypePusher.forceNominal));
        textMove.setText(String.valueOf(editTypePusher.loggerTypePusher.moveNominal));
        textUnclenching.setText(String.valueOf(editTypePusher.loggerTypePusher.unclenchingTime));
        editTypePusher = typePushers[tableTypePushers.getSelectedRow()];
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
                    break;
                case 2:
                    text = String.valueOf(typePusher.loggerTypePusher.moveNominal);
                    break;
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
