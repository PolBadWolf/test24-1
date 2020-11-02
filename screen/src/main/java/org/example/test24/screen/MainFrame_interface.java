package org.example.test24.screen;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import ru.yandex.fixcolor.tests.spc.bd.usertypes.Pusher;

public interface MainFrame_interface {
    interface CallBack {
        void buttonExit_onAction();
        void startViewArchive();
    }
    void canvas_Clear(Color backColor);
    void canvas_Line(Color lineColor, double lineWidth, double x0, double y0, double x, double y);
    void outStatusWork(String text);
    double getHeightCanvas();
    Canvas getCanvas();
    void label2_txt(String text);
    void setCallBack(CallBack callBack);
    void setFieldsSamplePusher(Pusher pusher);
    void setFieldsMeasuredPusher(int n_cicle, int forceMeasure, int moveMeasure, int timeUnClenching);
}
