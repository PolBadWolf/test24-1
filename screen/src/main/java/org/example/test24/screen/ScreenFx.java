package org.example.test24.screen;

import java.util.function.Consumer;

public interface ScreenFx {
    interface Closer {
        void close();
    }
    static ScreenFx init(Consumer closer) {
        return new ScreenClass(closer);
    }
    void main();
    void exitApp();
}
