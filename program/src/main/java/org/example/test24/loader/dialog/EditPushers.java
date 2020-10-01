package org.example.test24.loader.dialog;

import org.example.test24.bd.usertypes.TypePusher;

import javax.swing.*;

public class EditPushers {
    interface CallBack {
        long getCurrentId_loggerUser();
    }

    // ***************************
    private JLabel jLabel1;
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

}
