package org.example.test24.loader.dialog;

import org.example.test24.bd.BaseData;
import org.example.test24.bd.BaseDataException;
import org.example.test24.bd.Status;
import org.example.test24.bd.usertypes.Pusher;
import org.example.test24.bd.usertypes.TypePusher;
import org.example.test24.lib.swing.CreateComponents;
import org.example.test24.lib.swing.MyTableModel;
import org.example.test24.lib.swing.MyUtil;
import org.example.test24.lib.swing.SelectComboBox2Table;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    private JButton buttonFilter;
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
    // ***************************
    private EditPushers.CallBack callBack;
    private BaseData connBD;
    private long currentId_loggerUserEdit;
    // ***************************
    private JTable tableFindTypePushers;
    //
    private Pusher[] listPushers;
    private TypePusher[] listTypePushers;
    private SelectComboBox2Table<TypePusher> typePusherSelectComboBox2Table;

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
        // загрузка типов компонентов
        try {
            listTypePushers = connBD.getListTypePushers(true);
        } catch (BaseDataException e) {
            e.printStackTrace();
        }
        try {
            MyUtil.<TypePusher>loadToComboBox(
                    listTypePushers,
                    comboBoxTypePushers,
                    true,
                    null
            );
            typePusherSelectComboBox2Table = new SelectComboBox2Table<TypePusher>(comboBoxTypePushers, tableFindTypePushers, listTypePushers, null);
//            typePusherSelectComboBox2Table.setiKeep(true);
            callComboBoxTypePushers(null);
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
        jLabel1 = CreateComponents.getjLabel("Регистрационный номер", new Font("Times New Roman", 0, 18), 200, 230, 210, 25, true, true);
        textRegNumber = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", 0, 14), 410, 230,200, 25, null, null, true, true);
        buttonFilter = CreateComponents.getButton("Сброс Флтр.", new Font("Times New Roman", 0, 14), 30, 260, 120, 25, null, true, true);
        buttonDelete = CreateComponents.getButton("Удалить", new Font("Times New Roman", 0, 14), 30, 300, 120, 25, null, true, true);
        buttonEdit = CreateComponents.getButton("Редактировать", new Font("Times New Roman", 0, 14), 30, 340, 120, 25, null, true, true);
        buttonAdd = CreateComponents.getButton("Добавить", new Font("Times New Roman", 0, 14), 30, 380, 120, 25, this::callButtonAdd, true, true);
        // ---- таблица
        tablePushers = CreateComponents.getTable(
                640 - 17,
                /*new MyTableModel(
                        new ControlTablePushers()
                )*/
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
                                text = pusher.loggerPusher.loggerTypePusher.nameType;
                                break;
                            case 2:
                                text = String.valueOf(pusher.loggerPusher.loggerTypePusher.forceNominal);
                                break;
                            case 3:
                                text = String.valueOf(pusher.loggerPusher.loggerTypePusher.moveNominal);
                                break;
                            case 4:
                                text = String.valueOf(pusher.loggerPusher.loggerTypePusher.unclenchingTime);
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
        frame.add(scrollPushers);
        //
        panelTypePushers = CreateComponents.getPanel(null, new Font("Times New Roman", Font.PLAIN, 12), "Параметры типа толкателя", 190, 260, 420, 170, true, true);
        jLabel2 = CreateComponents.getjLabel("Тип толкателя", new Font("Times New Roman", 0, 18), 20, 28, 190, 25, true, true);
        jLabel3 = CreateComponents.getjLabel("Усилие на штоке (кг)", new Font("Times New Roman", 0, 18), 20, 65, 190, 25, true, true);
        jLabel4 = CreateComponents.getjLabel("Ход штока (мм)", new Font("Times New Roman", 0, 18), 20, 104, 190, 25, true, true);
        jLabel5 = CreateComponents.getjLabel("Время разжатия (сек)", new Font("Times New Roman", 0, 18), 20, 138, 210, 25, true, true);
        comboBoxTypePushers = CreateComponents.getComboBox(new Font("Times New Roman", 0, 14), 220, 28, 190, 25,
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

        textForce = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", 0, 14), 220, 67, 170, 25,
                null, null, true, true);
        textMove = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", 0, 14), 220, 106, 170, 25,
                null, null, true, true);
        textUnclenching = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", 0, 14), 220, 140, 170, 25,
                null, null, true, true);
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
        /*textName.setText(editTypePusher.loggerTypePusher.nameType);
        textForce.setText(String.valueOf(editTypePusher.loggerTypePusher.forceNominal));
        textMove.setText(String.valueOf(editTypePusher.loggerTypePusher.moveNominal));
        textUnclenching.setText(String.valueOf(editTypePusher.loggerTypePusher.unclenchingTime));
        editTypePusher = typePushers[tableTypePushers.getSelectedRow()];*/
    }
    // -----------
    private void callComboBoxTypePushers(ItemEvent itemEvent) {
        if (itemEvent != null) {
            if (itemEvent.getStateChange() == ItemEvent.DESELECTED) return;
        }
        try {
            TypePusher tp = typePusherSelectComboBox2Table.getResultFist();
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

    }
}
