package org.example.test24.loader.dialog;

import org.example.test24.bd.BaseData;
import org.example.test24.bd.BaseDataException;
import org.example.test24.bd.usertypes.TypePusher;
import org.example.test24.lib.swing.CreateComponents;
import org.example.test24.lib.swing.MySwingUtil;
import org.example.test24.lib.swing.MyTableModel;
import org.example.test24.lib.swing.SaveEnableComponents;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;

import static org.example.test24.lib.MyLogger.myLog;

public class EditTypePushers {
    interface CallBack {
        void messageCloseEditUsers(boolean newData);
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
    private TypePusher[] listTypePushers;
    private TypePusher editTypePusher = null;
    private long currentId_loggerUserEdit;
    SaveEnableComponents saveEnableComponents;

    public EditTypePushers(CallBack callBack, BaseData connBD, long currentId_loggerUserEdit) {
        this.callBack = callBack;
        this.connBD = connBD;
        this.currentId_loggerUserEdit = currentId_loggerUserEdit;
//        currentId_loggerUserEdit = callBack.getCurrentId_loggerUser();
        // загрузка списка типа толкателей
        try {
            listTypePushers = getListTypePushers();
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
    // загрузка списка типа толкателей
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
        jLabel1 = CreateComponents.getjLabel("Тип толкателя", new Font("Times New Roman", 0, 18), 120, 230, 210, 24, true, true);
        frame.add(jLabel1);
        jLabel2 = CreateComponents.getjLabel("Усилие на штоке (кг)", new Font("Times New Roman", 0, 18), 30, 310, 190, 24, true, true);
        frame.add(jLabel2);
        jLabel3 = CreateComponents.getjLabel("Ход штока (мм)", new Font("Times New Roman", 0, 18), 40, 350, 140, 24, true, true);
        frame.add(jLabel3);
        jLabel4 = CreateComponents.getjLabel("Время разжатия (сек)", new Font("Times New Roman", 0, 18), 40, 385, 140, 24, true, true);
        frame.add(jLabel4);
        // ---- поля ввода данных
        textName = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", 0, 18), 30, 270,400, 24, null, null, true, true);
        frame.add(textName);
        textForce = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", 0, 18), 290, 310, 140, 24, new FilterTextDigit(), null, true, true);
        frame.add(textForce);
        textMove = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", 0, 18), 290, 350, 140, 24, new FilterTextDigit(), null, true, true);
        frame.add(textMove);
        textUnclenching = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", 0, 18), 290, 385, 140, 24, new FilterTextDigit(), null, true, true);
        frame.add(textUnclenching);
        // ---- кнопки
        buttonDelete = CreateComponents.getButton("Удалить", new Font("Times New Roman", 0, 14), 470, 270, 120, 25, this::buttonDeleteAction, true, true);
        frame.add(buttonDelete);
        buttonClear = CreateComponents.getButton("Очистить", new Font("Times New Roman", 0, 14), 470, 310, 120, 25, this::buttonClearAction, true, true);
        frame.add(buttonClear);
        buttonEdit = CreateComponents.getButton("Редактировать", new Font("Times New Roman", 0, 14), 470, 350, 120, 25, this::buttonEditAction, true, true);
        frame.add(buttonEdit);
        buttonAdd = CreateComponents.getButton("Добавить", new Font("Times New Roman", 0, 14), 470, 385, 120, 25, this::buttonAddAction, true, true);
        frame.add(buttonAdd);
        // ---- таблица
        tableTypePushers = CreateComponents.getTable(
                640 - 17,
                new MyTableModel() {
                    @Override
                    public int getRowCount() {
                        if (listTypePushers == null) return 0;
                        return listTypePushers.length;
                    }

                    @Override
                    public int getColumnCount() {
                        return 4;
                    }

                    @Override
                    public Object getValueAt(int rowIndex, int columnIndex) {
                        if (rowIndex < 0 || columnIndex < 0) return "";
                        TypePusher typePusher = listTypePushers[rowIndex];
                        String text;
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
                },
                new CreateComponents.ModelTableNameWidth[]{
                        new CreateComponents.ModelTableNameWidth("Тип толкателя", -1),
                        new CreateComponents.ModelTableNameWidth("Усилие на штоке (кг)", 130),
                        new CreateComponents.ModelTableNameWidth("Ход штока (мм)", 100),
                        new CreateComponents.ModelTableNameWidth("Время разжатия (сек)", 130)
                },
                null,
                this::tableTypePushersChanged,
                true,
                true
        );
        scrollPane = CreateComponents.getScrollPane(0, 0, 640, 220, tableTypePushers, true, true);
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
            listTypePushers = getListTypePushers();
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
            for (int i = 0; i < listTypePushers.length; i++) {
                if (!v_typeName.equals(listTypePushers[i].loggerTypePusher.nameType)) continue;
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
            for (int i = 0; i < listTypePushers.length; i++) {
                if (!v_typeName.equals(listTypePushers[i].loggerTypePusher.nameType)) continue;
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
            listTypePushers = getListTypePushers();
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
        editTypePusher = listTypePushers[tableTypePushers.getSelectedRow()];
    }
    class ControlTableTypePushers implements MyTableModel.Control {
        @Override
        public int getRowCount() {
            int row = 0;
            if (listTypePushers != null) row = listTypePushers.length;
            return row;
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            String text;
            TypePusher typePusher = listTypePushers[rowIndex];
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
    }
    // -----------
}
