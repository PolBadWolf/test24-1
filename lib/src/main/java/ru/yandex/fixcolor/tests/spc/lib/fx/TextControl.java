package ru.yandex.fixcolor.tests.spc.lib.fx;

public interface TextControl {
    void setText(String text);
    void setVisible(boolean visible);
    boolean isVisible();
    void setFlash(int time);
    int getFlash();
}
