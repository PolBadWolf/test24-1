package ru.yandex.fixcolor.tests.spc.screen;

public interface ScreenFx {
    interface Closer {
        void close();
        int loadMaxNcycle();
    }
    static ScreenFx init(Closer closer) {
        return new ScreenClass(closer);
    }
    void main();
    void exitApp();
    void setRootFocus();
}
