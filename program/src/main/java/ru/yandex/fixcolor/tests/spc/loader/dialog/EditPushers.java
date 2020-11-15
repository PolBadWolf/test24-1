package ru.yandex.fixcolor.tests.spc.loader.dialog;

import ru.yandex.fixcolor.tests.spc.bd.*;
import ru.yandex.fixcolor.tests.spc.bd.usertypes.*;
import ru.yandex.fixcolor.tests.spc.lib.swing.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;

import static ru.yandex.fixcolor.tests.spc.lib.MyLogger.myLog;

public class EditPushers {
    interface CallBack {
        void messageCloseEditUsers(boolean newData);
    }

    // ***************************
    private JFrame frame;
    private JButton buttonEditTypePushers;
    private JButton buttonDelete;
    private JButton buttonEdit;
    private JButton buttonAdd;
    private JTextField textRegNumber;
    private JScrollPane scrollPushers;
    private JTable tablePushers;
    private JPanel panelTypePushers;
    private JComboBox<TypePusher> comboBoxTypePushers;
    private JTextField textForce;
    private JTextField textMove;
    private JTextField textUnclenching;
    private JTextField textWeightNominal;
    private JTable tableFindTypePushers;
    // ***************************
    private final EditPushers.CallBack callBack;
    private final BaseData connBD;
    private final long currentId_loggerUserEdit;
    // ***************************
    //
    private Pusher[] listPushers;
    private TypePusher[] listTypePushers;
    private SelectComboBox2Table_Top<TypePusher> typePusherSelectComboBox2Table;
    private final SaveEnableComponents saveEnableComponents;
    private Pusher editPusher;
    private boolean newData;

    public EditPushers(EditPushers.CallBack callBack, BaseData connBD, long currentId_loggerUserEdit) throws BaseDataException {
        this.callBack = callBack;
        this.connBD = connBD;
        this.currentId_loggerUserEdit = currentId_loggerUserEdit;
        newData = false;
        // загрузка списка толкателей
        try {
            listPushers = getListPushers();
        } catch (BaseDataException e) {
            myLog.log(Level.SEVERE, "ошибка получения списка толкателей", e);
            // в этом месте выход
            throw new BaseDataException("редактирование толкателей", e, Status.SQL_TRANSACTION_ERROR);
        }
        // инициация компонентов
        initComponents();
        //
        saveEnableComponents = new SaveEnableComponents(new Component[]{
                frame,
                buttonEditTypePushers,
                buttonDelete,
                buttonEdit,
                buttonAdd,
                textRegNumber,
                scrollPushers,
                tablePushers,
                comboBoxTypePushers,
                panelTypePushers,
                textMove,
                textForce,
                textUnclenching,
                textWeightNominal,
                tableFindTypePushers
        });
        // загрузка типов компонентов
        try {
            listTypePushers = connBD.getListTypePushers(true);
            try {
                MyUtil.loadToComboBox(
                        listTypePushers,
                        comboBoxTypePushers,
                        true,
                        null
                );
            } catch (Exception e) {
                throw new BaseDataException(e, Status.ERROR);
            }
            typePusherSelectComboBox2Table = new SelectComboBox2Table_Top<>(comboBoxTypePushers, tableFindTypePushers, listTypePushers, 7, null);
            callComboBoxTypePushers(null);
            typePusherSelectComboBox2Table.setLock(false);
        } catch (BaseDataException e) {
            e.printStackTrace();
        }
    }
    // ----
    // загрузка списка типа толкателей
    private Pusher[] getListPushers() throws BaseDataException {
        Pusher[] listPushers;
        try {
            listPushers = connBD.getListPushers(true);
        } catch (Exception exception) {
            throw new BaseDataException("загрузка типа толкателей", exception, Status.BASE_TYPE_ERROR);
        }
        return listPushers;
    }
    // инициация компонентов
    private void initComponents() {
        frame = new JFrame("Список толкателей");
        frame.setResizable(false);
        frame.setPreferredSize(new Dimension(640, 480));
        frame.setLayout(null);
        //
        CreateComponents.getLabel(frame, "Регистрационный номер", new Font("Times New Roman", Font.PLAIN, 18), 200, 230, true, true, MLabel.POS_LEADS);
        textRegNumber = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", Font.PLAIN, 14), 410, 230,200, 25, null, null, true, true);
        buttonDelete = CreateComponents.getButton("Удалить", new Font("Times New Roman", Font.PLAIN, 14), 30, 275, 120, 25, this::callButtonDelete, true, true);
        buttonEdit = CreateComponents.getButton("Изменить", new Font("Times New Roman", Font.PLAIN, 14), 30, 315, 120, 25, this::callButtonEditPusher, true, true);
        buttonAdd = CreateComponents.getButton("Добавить", new Font("Times New Roman", Font.PLAIN, 14), 30, 355, 120, 25, this::callButtonAdd, true, true);
        buttonEditTypePushers = CreateComponents.getButton("Типы Толкат.", new Font("Times New Roman", Font.PLAIN, 14), 30, 395, 120, 25, this::callButtonEditTypePushers, true, true);
        // ---- таблица
        tablePushers = CreateComponents.getTable(
                640 - 17,
                new MyTableModel() {
                    @Override
                    public int getRowCount() {
                        if (listPushers == null) return 0;
                        return listPushers.length;
                    }

                    @Override
                    public int getColumnCount() {
                        return 5;
                        //return super.getColumnCount();
                    }

                    @Override
                    public Object getValueAt(int rowIndex, int columnIndex) {
                        Pusher pusher = listPushers[rowIndex];
                        String text;
                        switch (columnIndex) {
                            case 0:
                                text = pusher.loggerPusher.namePusher;
                                break;
                            case 1:
                                text = pusher.loggerPusher.typePusher.loggerTypePusher.nameType;
                                break;
                            case 2:
                                text = String.valueOf(pusher.loggerPusher.typePusher.loggerTypePusher.forceNominal);
                                break;
                            case 3:
                                text = String.valueOf(pusher.loggerPusher.typePusher.loggerTypePusher.moveNominal);
                                break;
                            case 4:
                                text = String.valueOf(pusher.loggerPusher.typePusher.loggerTypePusher.unclenchingTime);
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + columnIndex);
                        }
                        return text;
                        //return super.getValueAt(rowIndex, columnIndex);
                    }
                },
                new CreateComponents.ModelTableNameWidth[]{
                        new CreateComponents.ModelTableNameWidth("Рег. номер", -1),
                        new CreateComponents.ModelTableNameWidth("Тип толкателя", -1),
                        new CreateComponents.ModelTableNameWidth("Ном. усилие (кг)", 100),
                        new CreateComponents.ModelTableNameWidth("Ном.ход (мм)", 95),
                        new CreateComponents.ModelTableNameWidth("Время разж. (мс)", 110)
                },
                null,
                this::tablePushersChanged,
                true,
                true
        );
        scrollPushers = CreateComponents.getScrollPane(0, 0, 640, 220, tablePushers, true, true);
        frame.add(textRegNumber);
        frame.add(buttonDelete);
        frame.add(buttonEdit);
        frame.add(buttonAdd);
        frame.add(buttonEditTypePushers);
        frame.add(scrollPushers);
        //
        panelTypePushers = CreateComponents.getPanel(null, new Font("Times New Roman", Font.PLAIN, 12), "Параметры типа толкателя", 190, 260, 420, 170, true, true);
        CreateComponents.getLabel(panelTypePushers, "Тип толкателя", new Font("Times New Roman", Font.PLAIN, 18), 200, 20, true, true, MLabel.POS_RIGHT);
        CreateComponents.getLabel(panelTypePushers, "Усилие на штоке (кг)", new Font("Times New Roman", Font.PLAIN, 18), 200, 48, true, true, MLabel.POS_RIGHT);
        CreateComponents.getLabel(panelTypePushers, "Ход штока (мм)", new Font("Times New Roman", Font.PLAIN, 18), 200, 78, true, true, MLabel.POS_RIGHT);
        CreateComponents.getLabel(panelTypePushers, "Время разжатия (мс)", new Font("Times New Roman", Font.PLAIN, 18), 200, 108, true, true, MLabel.POS_RIGHT);
        CreateComponents.getLabel(panelTypePushers, "Вес гидротолкателя (кг)", new Font("Times New Roman", Font.PLAIN, 18), 200, 138, true, true, MLabel.POS_RIGHT);
        comboBoxTypePushers = CreateComponents.getComboBox(new Font("Times New Roman", Font.PLAIN, 14), 220, 20, 190, 25,
              true,
                null,
                this::callComboBoxTypePushers,
                true, true);
        tableFindTypePushers = CreateComponents.getTable(200,
                null,
                null,
                null,
                null,
                false,
                true
        );
        tableFindTypePushers.setBounds(220, 55, 220, 300);

        textForce = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", Font.PLAIN, 14), 220, 50, 170, 25, //67
                null, null, true, true, false);
        textMove = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", Font.PLAIN, 14), 220, 80, 170, 25, //106
                null, null, true, true, false);
        textUnclenching = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", Font.PLAIN, 14), 220, 110, 170, 25, //140
                null, null, true, true, false);
        textWeightNominal = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", Font.PLAIN, 14), 220, 140, 170, 25, //140
                null, null, true, true, false);
        panelTypePushers.add(tableFindTypePushers);
        panelTypePushers.add(comboBoxTypePushers);
        panelTypePushers.add(textForce);
        panelTypePushers.add(textMove);
        panelTypePushers.add(textUnclenching);
        panelTypePushers.add(textWeightNominal);
        frame.add(panelTypePushers);
        //
        frame.pack();
        frame.setVisible(true);
        frame.requestFocus();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                callBack.messageCloseEditUsers(newData);
                frame.removeAll();
                frame.dispose();
            }
        });
    }

    // ----------- таблицы
    private void tablePushersChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) return;
        int row = tablePushers.getSelectedRow();
        if (row < 0) return;
        editPusher = listPushers[row];
        textRegNumber.setText(editPusher.loggerPusher.namePusher);
        comboBoxTypePushers.setSelectedItem(editPusher.loggerPusher.typePusher);
    }
    // -----------
    private void clearFields() {
        textRegNumber.setText("");
        comboBoxTypePushers.setSelectedItem(null);
        tablePushers.clearSelection();
    }
    private boolean refreshListPushers() {
        try {
            listPushers = connBD.getListPushers(true);
        } catch (Exception e) {
            myLog.log(Level.SEVERE, "обновление списка толкателей", e);
            MySwingUtil.showMessage(frame, "ошибка БД", "обновление списка толкателей",
                    10_000, () -> frame.requestFocus()
            );
            return true;
        }
        editPusher = null;
        tablePushers.updateUI();
        return false;
    }
    // -----------
    private void callComboBoxTypePushers(ActionEvent actionEvent) {
        try {
            TypePusher tp = (TypePusher) comboBoxTypePushers.getSelectedItem();
            textForce.setText(String.valueOf(tp.loggerTypePusher.forceNominal));
            textMove.setText(String.valueOf(tp.loggerTypePusher.moveNominal));
            textUnclenching.setText(String.valueOf(tp.loggerTypePusher.unclenchingTime));
            textWeightNominal.setText(String.valueOf(tp.loggerTypePusher.weightNominal));
        } catch (Exception exception) {
            textForce.setText("");
            textMove.setText("");
            textUnclenching.setText("");
            textWeightNominal.setText("");
        }
    }
    private boolean checkComponentsEdit() {
        if (
                textRegNumber.getText().length() == 0 ||
                        comboBoxTypePushers.getSelectedItem() == null
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
    private void callButtonDelete(ActionEvent actionEvent) {
        if (checkComponentsEdit()) return;
        try {
            connBD.deletePusher(0, editPusher);
            newData = true;
        } catch (BaseDataException e) {
            myLog.log(Level.SEVERE, "ошибка удаления толкателя");
            MySwingUtil.showMessage(frame, "ошибка БД", "ошибка удаления толкателя", 10_000, () -> frame.requestFocus());
            tablePushers.clearSelection();
            return;
        }
        // очистка полей
        clearFields();
        // обновление записей
        refreshListPushers();
    }
    private void callButtonAdd(ActionEvent actionEvent) {
        if (checkComponentsEdit()) return;
        // заменяемые данные
        String updateRegNumber = textRegNumber.getText();
        long updateId_TypePusher = ((TypePusher) comboBoxTypePushers.getSelectedItem()).id_typePusher;
        // проверка на повтор
        {
            boolean flAgain = false;
            for (Pusher pusher : listPushers) {
                if (!updateRegNumber.equals(pusher.loggerPusher.namePusher)) continue;
                flAgain = true;
                break;
            }
            if (flAgain) {
                saveEnableComponents.save();
                saveEnableComponents.offline();
                MySwingUtil.showMessage(frame,
                        "редактирование списка толкателей",
                        "такой толкатель уже существует",
                        5_000, () -> {
                            saveEnableComponents.restore();
                            frame.requestFocus();
                        });
                tablePushers.clearSelection();
                return;
            }
        }
        // добавление
        try {
            connBD.writeNewPusher(currentId_loggerUserEdit, updateRegNumber, updateId_TypePusher);
            newData = true;
        } catch (BaseDataException e) {
            myLog.log(Level.SEVERE, "запись нового толкателя в БД", e);
            MySwingUtil.showMessage(frame, "ошибка БД", "Запись нового толкателя", 10_000, () -> frame.requestFocus());
            return;
        }
        // очистка полей
        clearFields();
        // обновление записей
        refreshListPushers();
    }
    private void callButtonEditTypePushers(ActionEvent actionEvent) {
        saveEnableComponents.save();
        saveEnableComponents.offline();
        new Thread(()-> SwingUtilities.invokeLater(()-> new EditTypePushers(
                newData -> {
                    saveEnableComponents.restore();
                    if  (newData) {
                        // обновление данных по типам толкатей
                        typePusherSelectComboBox2Table.setLock(true);
                        try {
                            listTypePushers = connBD.getListTypePushers(true);
                            if (refreshListPushers()) new BaseDataException("ошибка обновления списка толкателей", Status.ERROR);
                            try {
                                MyUtil.loadToComboBox(
                                        listTypePushers,
                                        comboBoxTypePushers,
                                        true,
                                        null
                                );
                            } catch (Exception e) {
                                throw new BaseDataException(e, Status.ERROR);
                            }
                            typePusherSelectComboBox2Table.setCollections(listTypePushers);
                            EditPushers.this.newData = true;
                        } catch (BaseDataException e) {
                            e.printStackTrace();
                        }
                        comboBoxTypePushers.setSelectedIndex(-1);
                        typePusherSelectComboBox2Table.setLock(false);
                    }
                    frame.requestFocus();
                },
                connBD,
                currentId_loggerUserEdit
        )), "create type pushers").start();
    }
    // редактирование
    private void callButtonEditPusher(ActionEvent actionEvent) {
        if (checkComponentsEdit()) return;
        // заменяемые данные
        Pusher selectPusher = listPushers[tablePushers.getSelectedRow()];
        String updateRegNumber = textRegNumber.getText();
        long updateId_TypePusher = ((TypePusher) comboBoxTypePushers.getSelectedItem()).id_typePusher;
        // проверка на повтор
        if (!selectPusher.loggerPusher.namePusher.equals(updateRegNumber)) {
            boolean flag = false;
            for (Pusher pusher : listPushers) {
                if (pusher.id_pusher == selectPusher.id_pusher) continue;
                if (!pusher.loggerPusher.namePusher.equals(updateRegNumber)) continue;
                flag = true;
                break;
            }
            if (flag) {
                saveEnableComponents.save();
                saveEnableComponents.offline();
                MySwingUtil.showMessage(frame,
                        "редактирование списка толкателей",
                        "толкатель с таким именем уже существует",
                        5_000, () -> {
                            saveEnableComponents.restore();
                            frame.requestFocus();
                        });
                tablePushers.clearSelection();
                return;
            }
        }
        //
        if (selectPusher.loggerPusher.namePusher.equals(updateRegNumber) && selectPusher.loggerPusher.typePusher.id_typePusher == updateId_TypePusher) return;
        //
        try {
            connBD.updatePusher(selectPusher, currentId_loggerUserEdit, updateRegNumber, updateId_TypePusher);
            EditPushers.this.newData = true;
        } catch (BaseDataException e) {
            myLog.log(Level.SEVERE, "ошибка редактирования толкателей", e);
            saveEnableComponents.save();
            saveEnableComponents.offline();
            MySwingUtil.showMessage(frame,
                    "редактирование списка толкателей",
                    "ошибка редактирования толкателей",
                    5_000, () -> {
                        saveEnableComponents.restore();
                        frame.requestFocus();
                    });
            tablePushers.clearSelection();
            return;
        } finally {
            clearFields();
        }
        //
    }
}
