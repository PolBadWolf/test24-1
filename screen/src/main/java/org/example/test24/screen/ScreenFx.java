package org.example.test24.screen;

public interface ScreenFx {
    interface Closer {
        void close();
    }
    static ScreenFx init(Closer closer) {
        return new ScreenClass(closer);
    }
    void main();
    void exitApp();
}
