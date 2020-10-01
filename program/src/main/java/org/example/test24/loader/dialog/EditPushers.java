package org.example.test24.loader.dialog;

import org.example.test24.bd.BaseData;
import org.example.test24.bd.BaseDataException;
import org.example.test24.bd.Status;
import org.example.test24.bd.usertypes.Pusher;
import org.example.test24.bd.usertypes.TypePusher;
import org.example.test24.lib.swing.CreateComponents;
import org.example.test24.lib.swing.MyTableModel;
import org.example.test24.lib.swing.MyUtil;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Level;

import static org.example.test24.lib.MyLogger.myLog;

public class EditPushers {
    interface CallBack {
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
    private JLabel jLabel6;
    private JComboBox<TypePusher> comboBoxTypePushers;
    private JTextField textFind;
    private JTextField textForce;
    private JTextField textMove;
    private JTextField textUnclenching;
    // ***************************
    private EditPushers.CallBack callBack;
    private BaseData connBD;
    private long currentId_loggerUserEdit;
    //
    private Pusher[] listPushers;
    private TypePusher[] listTypePushers;

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
                    null
            );
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
                new MyTableModel(
                        this::getRowCountTablePushers,
                        this::getColumnCountTablePushers,
                        this::getValueAtTablePushers
                ),
                new CreateComponents.ModelTableNameWidth[]{
                        new CreateComponents.ModelTableNameWidth("Рег. номер", -1),
                        new CreateComponents.ModelTableNameWidth("Тип толкателя", -1),
                        new CreateComponents.ModelTableNameWidth("Ном. усилие (кг)", 100),
                        new CreateComponents.ModelTableNameWidth("Ном.ход (мм)", 95),
                        new CreateComponents.ModelTableNameWidth("Время разж. (сек)", 110)
                },
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
        jLabel6 = CreateComponents.getjLabel("Поиск типа толкателя", new Font("Times New Roman", 0, 18), 20, 18, 190, 25, true, true);
        jLabel2 = CreateComponents.getjLabel("Тип толкателя", new Font("Times New Roman", 0, 18), 20, 48, 190, 25, true, true);
        jLabel3 = CreateComponents.getjLabel("Усилие на штоке (кг)", new Font("Times New Roman", 0, 18), 20, 78, 190, 25, true, true);
        jLabel4 = CreateComponents.getjLabel("Ход штока (мм)", new Font("Times New Roman", 0, 18), 20, 108, 190, 25, true, true);
        jLabel5 = CreateComponents.getjLabel("Время разжатия (сек)", new Font("Times New Roman", 0, 18), 20, 138, 210, 25, true, true);
        textFind = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", 0, 14), 220, 14, 170, 25,
                new FilterPushers(), null, true, true);
        comboBoxTypePushers = CreateComponents.getComboBox(new Font("Times New Roman", 0, 14), 220, 50, 190, 25,
              false, null, this::callComboBoxTypePushers, true, true);
        textForce = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", 0, 14), 220, 80, 170, 25,
                null, null, true, true);
        textMove = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", 0, 14), 220, 110, 170, 25,
                null, null, true, true);
        textUnclenching = CreateComponents.getTextField(CreateComponents.TEXTFIELD, new Font("Times New Roman", 0, 14), 220, 140, 170, 25,
                null, null, true, true);
        panelTypePushers.add(jLabel2);
        panelTypePushers.add(jLabel6);
        panelTypePushers.add(jLabel3);
        panelTypePushers.add(jLabel4);
        panelTypePushers.add(jLabel5);
        panelTypePushers.add(comboBoxTypePushers);
        panelTypePushers.add(textFind);
        panelTypePushers.add(textForce);
        panelTypePushers.add(textMove);
        panelTypePushers.add(textUnclenching);
        frame.add(panelTypePushers);
        //
        frame.pack();
        frame.setVisible(true);
        frame.requestFocus();
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
    private int getRowCountTablePushers() {
        int row = 0;
        if (listPushers != null) row = listPushers.length;
        return row;
    }
    private int getColumnCountTablePushers() { return 5; }
    private Object getValueAtTablePushers(int rowIndex, int columnIndex) {
        String text;
        Pusher pusher = listPushers[rowIndex];
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
    }
    // ----------- фильтр для типа толкателей
    class FilterPushers extends DocumentFilter {
        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            super.remove(fb, offset, length);
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            super.insertString(fb, offset, string, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            super.replace(fb, offset, length, text, attrs);
        }
    }
    // -----------
    private void callComboBoxTypePushers(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == 1) return;
    }
    private void callButtonAdd(ActionEvent actionEvent) {

    }
}
