package ru.yandex.fixcolor.tests.spc.lib.swing;

import javax.swing.*;

public class MyJTable extends JTable {
    public interface CallBack {
        void update();
    }

    private CallBack callBack = null;
    public String[] titles;

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void updateUI() {
        if (callBack != null) callBack.update();
        super.updateUI();
    }

    public MyJTable() {
    }
}
