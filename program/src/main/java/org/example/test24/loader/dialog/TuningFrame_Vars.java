package org.example.test24.loader.dialog;

import javax.swing.*;

class TuningFrame_Vars extends FrameParrent_Metods {
    // ===== компоненты JFrame =======
    protected JFrame frameTuning = null;
    protected EditUsers editUsers = null;

    protected JPanel panelCommPort = null;
    protected JLabel labelPortCurrent = null;
    protected JTextField textCommPortStatus = null;
    protected JComboBox<String> comboBoxCommPort = null;

    protected JPanel panelTypeBd = null;
    protected JTextField textTypeBdStatus = null;
    protected JComboBox<String> comboBoxTypeBd = null;

    protected JPanel panelParamSQL = null;
    protected JTextField fieldParamServerIP = null;
    protected JTextField fieldParamServerPort = null;
    protected JTextField fieldParamServerLogin = null;
    protected JTextField fieldParamServerPassword = null;
    protected JComboBox<String> comboBoxListBd = null;
    protected JButton buttonOk = null;
    protected JButton buttonSave = null;
    protected JButton buttonTest = null;

    protected JPanel panelSelectEdit = null;
    protected JButton buttonEditUsers = null;
    protected JButton buttonEditPushers = null;
    // ===============================================
    protected boolean lockBegin;


    // список доступных портов
    protected String[] commPortNameList;
    // список доступных баз
    protected String[] listBaseData;
}
