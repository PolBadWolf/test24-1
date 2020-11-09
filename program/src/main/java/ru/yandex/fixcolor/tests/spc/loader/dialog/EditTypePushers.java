package ru.yandex.fixcolor.tests.spc.loader.dialog;

import ru.yandex.fixcolor.tests.spc.bd.*;
import ru.yandex.fixcolor.tests.spc.bd.usertypes.TypePusher;
import ru.yandex.fixcolor.tests.spc.lib.swing.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;

import static ru.yandex.fixcolor.tests.spc.lib.MyLogger.myLog;

public class EditTypePushers {
    interface CallBack {
        void messageCloseEditTypePushers(boolean newData);
    }

    // ***********************************************************************
    private JFrame frame;
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

    private final CallBack callBack;
    private final BaseData connBD;
    // список тип толкателей
    private TypePusher[] listTypePushers;
    private TypePusher editTypePusher = null;
    private final long currentId_loggerUserEdit;
    SaveEnableComponents saveEnableComponents;
    private boolean newData;

    public EditTypePushers(CallBack callBack, BaseData connBD, long currentId_loggerUserEdit) {
        this.callBack = callBack;
        this.connBD = connBD;
        this.currentId_loggerUserEdit = currentId_loggerUserEdit;
        newData = false;
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
        TypePusher[] typePushers;
        typePushers = connBD.getListTypePushers(true);
        return typePushers;
    }
    // инициация компонентов
    private void initComponents() {
        frame = new JFrame("Перечень типов толкателей");
        frame.setResizable(false);
        frame.setPreferredSize(new Dimension(640, 480));
        frame.setLayout(null);
        // ---- надписи
        CreateComponents.getLabel(frame, "Тип толкателя", new Font("Times New Roman", Font.PLAIN, 18), 280, 240, true, true, MLabel.POS_CENTER);
        CreateComponents.getLabel(frame, "Усилие на штоке (кг)", new Font("Times New Roman", Font.PLAIN, 16), 275, 310, true, true, MLabel.POS_RIGHT);
        CreateComponents.getLabel(frame, "Ход штока (мм)", new Font("Times New Roman", Font.PLAIN, 16), 275, 350, true, true, MLabel.POS_RIGHT);
        CreateComponents.getLabel(frame, "Время разжатия (мc)", new Font("Times New Roman", Font.PLAIN, 16), 275, 385, true, true, MLabel.POS_RIGHT);
        // ---- поля ввода данных
        textName = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", Font.PLAIN, 18), 30, 270,400, 24, null, null, true, true);
        frame.add(textName);
        textForce = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", Font.PLAIN, 18), 290, 310, 140, 24, new FilterTextDigit(), null, true, true);
        frame.add(textForce);
        textMove = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", Font.PLAIN, 18), 290, 350, 140, 24, new FilterTextDigit(), null, true, true);
        frame.add(textMove);
        textUnclenching = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", Font.PLAIN, 18), 290, 385, 140, 24, new FilterTextDigit(), null, true, true);
        frame.add(textUnclenching);
        // ---- кнопки
        buttonDelete = CreateComponents.getButton("Удалить", new Font("Times New Roman", Font.PLAIN, 14), 470, 270, 120, 25, this::buttonDeleteAction, true, true);
        frame.add(buttonDelete);
        buttonClear = CreateComponents.getButton("Очистить", new Font("Times New Roman", Font.PLAIN, 14), 470, 310, 120, 25, this::buttonClearAction, true, true);
        frame.add(buttonClear);
        buttonEdit = CreateComponents.getButton("Изменить", new Font("Times New Roman", Font.PLAIN, 14), 470, 350, 120, 25, this::buttonEditAction, true, true);
        frame.add(buttonEdit);
        buttonAdd = CreateComponents.getButton("Добавить", new Font("Times New Roman", Font.PLAIN, 14), 470, 385, 120, 25, this::buttonAddAction, true, true);
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
        JScrollPane scrollPane = CreateComponents.getScrollPane(0, 0, 640, 220, tableTypePushers, true, true);
        frame.add(scrollPane);
        // ----
        frame.pack();
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                callBack.messageCloseEditTypePushers(newData);
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
    private boolean checkRepeatTypePusher(String v_typeName, TypePusher[] listTypePushers) {
        boolean flAgain = false;
        for (TypePusher typePusher : listTypePushers) {
            if (!v_typeName.equals(typePusher.loggerTypePusher.nameType)) continue;
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
                    () -> {
                        saveEnableComponents.restore();
                        frame.requestFocus();
                    }
            );
            return true;
        }
        return false;
    }
    // ----------- кнопки
    // button delete
    private void buttonDeleteAction(ActionEvent e) {
        if (editTypePusher == null) return;
        String[] targetNamePusher = new String[1];
        // проверка на занятость
        int countUse;
        try {
            countUse = connBD.getCountPushersFromType(editTypePusher.id_typePusher, targetNamePusher);
            if (countUse > 0) {
                saveEnableComponents.save();
                saveEnableComponents.offline();
                MySwingUtil.showMessage(frame, "удаление", "этот тип толкателя используется (" + targetNamePusher[0] + ")"
                        , 5_000, () -> {
                    saveEnableComponents.restore();
                    frame.requestFocus();
                });
                return;
            }
            connBD.deleteTypePusher(currentId_loggerUserEdit, editTypePusher);
            listTypePushers = getListTypePushers();
            newData = true;
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
    private boolean checkEditComponents() {
        if (
                textName.getText().length() == 0
                        || textForce.getText().length() == 0
                        || textMove.getText().length() == 0
                        || textUnclenching.getText().length() == 0
        ) {
            saveEnableComponents.save();
            saveEnableComponents.offline();
            MySwingUtil.showMessage(frame, "редактирование", "не все поля заполнены", 5_000, () -> {
                saveEnableComponents.restore();
                frame.requestFocus();
            });
            return true;
        }
        return false;
    }
    // button edit
    private void buttonEditAction(ActionEvent e) {
        if (editTypePusher == null) return;
        if (checkEditComponents()) return;
        // заменяемые данные
        String v_typeName = textName.getText();
        int v_force = Integer.parseInt(textForce.getText());
        int v_move = Integer.parseInt(textMove.getText());
        int v_unclenching = Integer.parseInt(textUnclenching.getText());
        // проверка на повтор
        if (!v_typeName.equals(editTypePusher.loggerTypePusher.nameType)) {
            if (checkRepeatTypePusher(v_typeName, listTypePushers)) return;
        }
        // обновление
        try {
            connBD.updateTypePusher(editTypePusher, currentId_loggerUserEdit,
                    v_typeName,
                    v_force,
                    v_move,
                    v_unclenching
            );
            newData = true;
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
        if (checkEditComponents()) return;
        // заменяемые данные
        String v_typeName = textName.getText();
        int v_force = Integer.parseInt(textForce.getText());
        int v_move = Integer.parseInt(textMove.getText());
        int v_unclenching = Integer.parseInt(textUnclenching.getText());
        // проверка на повтор
        if (checkRepeatTypePusher(v_typeName, listTypePushers)) return;
        // добавление
        try {
            connBD.writeNewTypePusher(currentId_loggerUserEdit,
                    v_typeName,
                    v_force,
                    v_move,
                    v_unclenching
            );
            newData = true;
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
        editTypePusher = listTypePushers[tableTypePushers.getSelectedRow()];
        textName.setText(editTypePusher.loggerTypePusher.nameType);
        textForce.setText(String.valueOf(editTypePusher.loggerTypePusher.forceNominal));
        textMove.setText(String.valueOf(editTypePusher.loggerTypePusher.moveNominal));
        textUnclenching.setText(String.valueOf(editTypePusher.loggerTypePusher.unclenchingTime));
    }
    // -----------
}
