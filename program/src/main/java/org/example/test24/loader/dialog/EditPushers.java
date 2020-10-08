package org.example.test24.loader.dialog;

import org.example.test24.bd.BaseData;
import org.example.test24.bd.BaseDataException;
import org.example.test24.bd.Status;
import org.example.test24.bd.usertypes.Pusher;
import org.example.test24.bd.usertypes.TypePusher;
import org.example.test24.lib.swing.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;

import static org.example.test24.lib.MyLogger.myLog;

public class EditPushers {
    interface CallBack {
        void messageCloseEditUsers();
        long getCurrentId_loggerUser();
    }

    // ***************************
    private JFrame frame;
    private JLabel jLabel1;
    private JButton buttonEditTypePushers;
    private JButton buttonDelete;
    private JButton buttonEdit;
    private JButton buttonAdd;
    private JTextField textRegNumber;
    private JScrollPane scrollPushers;
    private JTable tablePushers;
    private JPanel panelTypePushers;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JComboBox<TypePusher> comboBoxTypePushers;
    private JTextField textForce;
    private JTextField textMove;
    private JTextField textUnclenching;
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
    private SaveEnableComponents saveEnableComponents;
    private Pusher editPusher;

    public EditPushers(EditPushers.CallBack callBack, BaseData connBD, long currentId_loggerUserEdit) {
        this.callBack = callBack;
        this.connBD = connBD;
        this.currentId_loggerUserEdit = currentId_loggerUserEdit;
        // загрузка списка толкателей
        try {
            listPushers = getListPushers();
        } catch (BaseDataException e) {
            myLog.log(Level.SEVERE, "ошибка получения списка толкателей", e);
            // в этом месте выход
            //return;
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
                tableFindTypePushers
        });
        // загрузка типов компонентов
        try {
            listTypePushers = connBD.getListTypePushers(true);
            MyUtil.loadToComboBox(
                    listTypePushers,
                    comboBoxTypePushers,
                    true,
                    null
            );
            typePusherSelectComboBox2Table = new SelectComboBox2Table_Top<>(comboBoxTypePushers, tableFindTypePushers, listTypePushers, 7, null);
            callComboBoxTypePushers(null);
            typePusherSelectComboBox2Table.setLock(false);
        } catch (BaseDataException e) {
            e.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    // ----
    // загрузка списка типа толкателей
    private Pusher[] getListPushers() throws BaseDataException {
        Pusher[] listPushers = new Pusher[0];
        try {
            listPushers = connBD.getListPushers(true);
        } catch (Exception exception) {
            throw new BaseDataException("загрузка типа толкателей !!!!!!!! переправить", exception, Status.BASE_TYPE_ERROR);
        }
        return listPushers;
    }
    // инициация компонентов
    private void initComponents() {
        frame = new JFrame();
        frame.setResizable(false);
        frame.setPreferredSize(new Dimension(640, 480));
        frame.setLayout(null);
        //
        jLabel1 = CreateComponents.getLabel("Регистрационный номер", new Font("Times New Roman", Font.PLAIN, 18), 200, 230, 210, 25, true, true);
        textRegNumber = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", Font.PLAIN, 14), 410, 230,200, 25, null, null, true, true);
        buttonDelete = CreateComponents.getButton("Удалить", new Font("Times New Roman", Font.PLAIN, 14), 30, 275, 120, 25, null, true, true);
        buttonEdit = CreateComponents.getButton("Редактировать", new Font("Times New Roman", Font.PLAIN, 14), 30, 315, 120, 25, null, true, true);
        buttonAdd = CreateComponents.getButton("Добавить", new Font("Times New Roman", Font.PLAIN, 14), 30, 355, 120, 25, this::callButtonAdd, true, true);
        buttonEditTypePushers = CreateComponents.getButton("Тип.Толкат.", new Font("Times New Roman", Font.PLAIN, 14), 30, 395, 120, 25, this::callButtonEditTypePushers, true, true);
        // ---- таблица
        tablePushers = CreateComponents.getTable(
                640 - 17,
                new MyTableModel() {
                    @Override
                    public int getRowCount() {
                        if (listPushers == null) return 0;
                        return listPushers.length;
                        //return super.getRowCount();
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
                        new CreateComponents.ModelTableNameWidth("Время разж. (сек)", 110)
                },
                null,
                this::tablePushersChanged,
                true,
                true
        );
        scrollPushers = CreateComponents.getScrollPane(0, 0, 640, 220, tablePushers, true, true);
        frame.add(jLabel1);
        frame.add(textRegNumber);
        frame.add(buttonDelete);
        frame.add(buttonEdit);
        frame.add(buttonAdd);
        frame.add(buttonEditTypePushers);
        frame.add(scrollPushers);
        //
        panelTypePushers = CreateComponents.getPanel(null, new Font("Times New Roman", Font.PLAIN, 12), "Параметры типа толкателя", 190, 260, 420, 170, true, true);
        jLabel2 = CreateComponents.getLabel("Тип толкателя", new Font("Times New Roman", Font.PLAIN, 18), 20, 28, 190, 25, true, true);
        jLabel3 = CreateComponents.getLabel("Усилие на штоке (кг)", new Font("Times New Roman", Font.PLAIN, 18), 20, 65, 190, 25, true, true);
        jLabel4 = CreateComponents.getLabel("Ход штока (мм)", new Font("Times New Roman", Font.PLAIN, 18), 20, 104, 190, 25, true, true);
        jLabel5 = CreateComponents.getLabel("Время разжатия (сек)", new Font("Times New Roman", Font.PLAIN, 18), 20, 138, 210, 25, true, true);
        comboBoxTypePushers = CreateComponents.getComboBox(new Font("Times New Roman", Font.PLAIN, 14), 220, 28, 190, 25,
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

        textForce = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", Font.PLAIN, 14), 220, 67, 170, 25,
                null, null, true, true, false);
        textMove = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", Font.PLAIN, 14), 220, 106, 170, 25,
                null, null, true, true, false);
        textUnclenching = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", Font.PLAIN, 14), 220, 140, 170, 25,
                null, null, true, true, false);
        panelTypePushers.add(tableFindTypePushers);
        panelTypePushers.add(jLabel2);
        panelTypePushers.add(jLabel3);
        panelTypePushers.add(jLabel4);
        panelTypePushers.add(jLabel5);
        panelTypePushers.add(comboBoxTypePushers);
        panelTypePushers.add(textForce);
        panelTypePushers.add(textMove);
        panelTypePushers.add(textUnclenching);
        frame.add(panelTypePushers);
        //
        frame.pack();
        frame.setVisible(true);
        frame.requestFocus();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                callBack.messageCloseEditUsers();
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
    /*private TypePusher selectTypePusher(TypePusher[] listTypePushers, long idxLooger) {
        TypePusher tp = null;
        for (TypePusher typePusher : listTypePushers) {
            if (typePusher.loggerTypePusher.id_loggerTypePusher == idxLooger) {
                tp = typePusher;
                break;
            }
        }
        return tp;
    }*/
    // -----------
    private void clearFields() {
        textRegNumber.setText("");
        comboBoxTypePushers.setSelectedItem(null);
    }

    // -----------
    private void callComboBoxTypePushers(ActionEvent actionEvent) {
        try {
            TypePusher tp = (TypePusher) comboBoxTypePushers.getSelectedItem();
            textForce.setText(String.valueOf(tp.loggerTypePusher.forceNominal));
            textMove.setText(String.valueOf(tp.loggerTypePusher.moveNominal));
            textUnclenching.setText(String.valueOf(tp.loggerTypePusher.unclenchingTime));
        } catch (Exception exception) {
            textForce.setText("");
            textMove.setText("");
            textUnclenching.setText("");
        }
    }
    private void callButtonAdd(ActionEvent actionEvent) {
        if (
                textRegNumber.getText().length() == 0 ||
                        comboBoxTypePushers.getSelectedItem() == null
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
        String updateRegNumber = textRegNumber.getText();
        long updateId_TypePusher = ((TypePusher) comboBoxTypePushers.getSelectedItem()).id_typePusher;
        // проверка на повтор
        {
            boolean flAgain = false;
            for (int i = 0; i < listPushers.length; i++) {
                if (!updateRegNumber.equals(listPushers[i].loggerPusher.namePusher)) continue;
                flAgain = true;
                break;
            }
            if (flAgain) {
                saveEnableComponents.save();
                saveEnableComponents.offline();
                MySwingUtil.showMessage(frame,
                        "редактирование списка толкателей",
                        "такой толкатель уже существует",
                        5_000, o -> {
                            saveEnableComponents.restore();
                            frame.requestFocus();
                        });
                return;
            }
        }
        // добавление
        try {
            connBD.writeNewPusher(currentId_loggerUserEdit, updateRegNumber, updateId_TypePusher);
        } catch (BaseDataException e) {
            e.printStackTrace();
        }
        // очистка полей
        clearFields();
        // обновление записей
        try {
            listPushers = connBD.getListPushers(true);
        } catch (Exception exception) {
            //exception.printStackTrace();
        }
        tablePushers.updateUI();
    }
    private void callButtonEditTypePushers(ActionEvent actionEvent) {
        saveEnableComponents.save();
        saveEnableComponents.offline();
        new Thread(()->{
            SwingUtilities.invokeLater(()->{
                new EditTypePushers(
                        new EditTypePushers.CallBack() {
                            @Override
                            public void messageCloseEditUsers(boolean newData) {
                                saveEnableComponents.restore();
                                // обновление данных по типам толкатей
                                typePusherSelectComboBox2Table.setLock(true);
                                try {
                                    listTypePushers = connBD.getListTypePushers(true);
                                    MyUtil.loadToComboBox(
                                            listTypePushers,
                                            comboBoxTypePushers,
                                            true,
                                            null
                                    );
                                    typePusherSelectComboBox2Table.setCollections(listTypePushers);
                                } catch (BaseDataException e) {
                                    e.printStackTrace();
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                                comboBoxTypePushers.setSelectedIndex(-1);
                                typePusherSelectComboBox2Table.setLock(false);
                                frame.requestFocus();
                            }
                        },
                        connBD,
                        currentId_loggerUserEdit
                );
            });
        }, "create type pushers").start();
    }
}
