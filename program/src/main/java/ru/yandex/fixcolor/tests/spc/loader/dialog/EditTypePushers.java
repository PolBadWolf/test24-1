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
    private JLabel label_title;
    private JTable tableTypePushers;
    private JTextField textName;
    private JTextField textForce;
    private JTextField textMove;
    private JTextField textUnclenching;
    private JTextField textClenching;
    private JTextField textWeightNominal;
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
                textClenching,
                textWeightNominal,
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
        frame = new JFrame("Редактор типов толкателей");
        frame.setResizable(false);
        frame.setPreferredSize(new Dimension(640, 530));
        frame.setLayout(null);
        //
        int shiftY = 50;
        label_title = CreateComponents.getLabel("Редактор типов толкателей", new Font("Times New Roman", Font.BOLD, 28), 120, 10, 400, 33, true, true);
        frame.add(label_title);
        // ---- надписи
        CreateComponents.getLabel(frame, "Тип толкателя", new Font("Times New Roman", Font.PLAIN, 18), 280 + shiftY, 220 + shiftY, true, true, MLabel.POS_CENTER);
        // ----
        CreateComponents.getLabel(frame, "Усилие на штоке (кг)", new Font("Times New Roman", Font.PLAIN, 16), 275, 285 + shiftY, true, true, MLabel.POS_RIGHT);
        CreateComponents.getLabel(frame, "Ход штока (мм)", new Font("Times New Roman", Font.PLAIN, 16), 275, 313 + shiftY, true, true, MLabel.POS_RIGHT);
        CreateComponents.getLabel(frame, "Время подъема (cек)", new Font("Times New Roman", Font.PLAIN, 16), 275, 343 + shiftY, true, true, MLabel.POS_RIGHT);
        CreateComponents.getLabel(frame, "Время опускания (cек)", new Font("Times New Roman", Font.PLAIN, 16), 275, 371 + shiftY, true, true, MLabel.POS_RIGHT);
        CreateComponents.getLabel(frame, "Вес гидротолкателя (кг)", new Font("Times New Roman", Font.PLAIN, 16), 275, 400 + shiftY, true, true, MLabel.POS_RIGHT);
        // ---- поля ввода данных
        textName = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", Font.PLAIN, 18), 30, 250 + shiftY,400, 24, null, null, true, true);
        frame.add(textName);
        // ----
        textForce = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", Font.PLAIN, 18), 290, 285 + shiftY, 140, 24, new FilterTextDigit(), null, true, true);
        frame.add(textForce);
        textMove = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", Font.PLAIN, 18), 290, 313 + shiftY, 140, 24, new FilterTextDigit(), null, true, true);
        frame.add(textMove);
        textUnclenching = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", Font.PLAIN, 18), 290, 343 + shiftY, 140, 24, new FilterTextDigitTchk(), null, true, true);
        frame.add(textUnclenching);
        textClenching = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", Font.PLAIN, 18), 290, 371 + shiftY, 140, 24, new FilterTextDigitTchk(), null, true, true);
        frame.add(textClenching);
        textWeightNominal = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", Font.PLAIN, 18), 290, 400 + shiftY, 140, 24, new FilterTextDigit(), null, true, true);
        frame.add(textWeightNominal);
        // ---- кнопки
        buttonDelete = CreateComponents.getButton(frame, "Удалить", new Font("Times New Roman", Font.PLAIN, 14), 470, 270 + shiftY, 120, 25, this::pushButtonDelete, true, true);
        buttonClear = CreateComponents.getButton(frame, "Очистить", new Font("Times New Roman", Font.PLAIN, 14), 470, 310 + shiftY, 120, 25, this::buttonClearAction, true, true);
        buttonEdit = CreateComponents.getButton(frame, "Изменить", new Font("Times New Roman", Font.PLAIN, 14), 470, 350 + shiftY, 120, 25, this::buttonEditAction, true, true);
        buttonAdd = CreateComponents.getButton(frame, "Добавить", new Font("Times New Roman", Font.PLAIN, 14), 470, 385 + shiftY, 120, 25, this::buttonAddAction, true, true);
        // ---- таблица
        tableTypePushers = CreateComponents.getTable(
                frame.getPreferredSize().width - 50,
                new MyTableModel() {
                    @Override
                    public int getRowCount() {
                        if (listTypePushers == null) return 0;
                        return listTypePushers.length;
                    }

                    @Override
                    public int getColumnCount() {
                        return 6;
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
                            case 4:
                                text = String.valueOf(typePusher.loggerTypePusher.clenchingTime);
                                break;
                            case 5:
                                text = String.valueOf(typePusher.loggerTypePusher.weightNominal);
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + columnIndex);
                        }
                        return text;
                    }
                },
                new CreateComponents.ModelTableNameWidth[]{
                        new CreateComponents.ModelTableNameWidth("Тип толкателя", -1),
                        new CreateComponents.ModelTableNameWidth("Усилие(кг)", 80), //130
                        new CreateComponents.ModelTableNameWidth("Ход (мм)", 70),       //100
                        new CreateComponents.ModelTableNameWidth("Подъем (сек)", 100), //130
                        new CreateComponents.ModelTableNameWidth("Опуск. (сек)", 80), //130
                        new CreateComponents.ModelTableNameWidth("Вес (кг)", 60)
                },
                null,
                this::tableTypePushersChanged,
                true,
                true
        );
        JScrollPane scrollPane = CreateComponents.getScrollPane(5, shiftY, frame.getPreferredSize().width - 30, 220, tableTypePushers, true, true);
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
    static class FilterTextDigit extends DocumentFilter {
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
    static class FilterTextDigitTchk extends DocumentFilter {
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text.length() == 1) {
                boolean flExit = false;
                int x = text.codePointAt(0);
                if (x < '0') flExit = true;
                if (x > '9') flExit = true;
                if (x == '.') flExit = false;
                if (x == ',') {
                    text = ".";
                    flExit = false;
                }
                if (flExit) return;
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
    ru.yandex.fixcolor.tests.spc.lib.swing.SaveEnableComponents saveEnableComponentsDelete = null;
    private void pushButtonDelete(ActionEvent e) {
        saveEnableComponentsDelete = new SaveEnableComponents(new Component[]{
                frame,
                textName,
                textForce,
                textMove,
                textUnclenching,
                textClenching,
                textWeightNominal,
                buttonDelete,
                buttonClear,
                buttonEdit,
                buttonAdd
        });
        saveEnableComponentsDelete.save();
        saveEnableComponentsDelete.offline();
        ru.yandex.fixcolor.tests.spc.lib.swing.MySwingUtil.showMessageYesNo(frame, "удаление толкателя", "удалить ?",
                5_000, this::pushButtonDeleteYes, this::pushButtonDeleteNo);
    }
    private void pushButtonDeleteYes() {
        saveEnableComponentsDelete.restore();
        frame.requestFocus();
        callButtonDelete();
    }
    private void pushButtonDeleteNo() {
        saveEnableComponentsDelete.restore();
        frame.requestFocus();
    }
    private void callButtonDelete() {
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
                        || textClenching.getText().length() == 0
                        || textWeightNominal.getText().length() == 0
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
        float v_unclenching = Float.parseFloat(textUnclenching.getText());
        float v_clenching = Float.parseFloat(textClenching.getText());
        int v_weightNominal = Integer.parseInt(textWeightNominal.getText());
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
                    v_unclenching,
                    v_clenching,
                    v_weightNominal
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
        float v_unclenching = Float.parseFloat(textUnclenching.getText());
        float v_clenching = Float.parseFloat(textClenching.getText());
        int v_weightNominal = Integer.parseInt(textWeightNominal.getText());
        // проверка на повтор
        if (checkRepeatTypePusher(v_typeName, listTypePushers)) return;
        // добавление
        try {
            connBD.writeNewTypePusher(currentId_loggerUserEdit,
                    v_typeName,
                    v_force,
                    v_move,
                    v_unclenching,
                    v_clenching,
                    v_weightNominal
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
        textClenching.setText("");
        textWeightNominal.setText("");
    }
    // ----------- таблицы
    private void tableTypePushersChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) return;
        editTypePusher = listTypePushers[tableTypePushers.getSelectedRow()];
        textName.setText(editTypePusher.loggerTypePusher.nameType);
        textForce.setText(String.valueOf(editTypePusher.loggerTypePusher.forceNominal));
        textMove.setText(String.valueOf(editTypePusher.loggerTypePusher.moveNominal));
        textUnclenching.setText(String.valueOf(editTypePusher.loggerTypePusher.unclenchingTime));
        textClenching.setText(String.valueOf(editTypePusher.loggerTypePusher.clenchingTime));
        textWeightNominal.setText(String.valueOf(editTypePusher.loggerTypePusher.weightNominal));
    }
    // -----------
}
