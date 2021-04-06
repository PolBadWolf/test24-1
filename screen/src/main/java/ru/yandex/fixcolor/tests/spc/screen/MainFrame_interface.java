package ru.yandex.fixcolor.tests.spc.screen;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import ru.yandex.fixcolor.tests.spc.bd.usertypes.Pusher;
import ru.yandex.fixcolor.tests.spc.lib.fx.TextControl;

public interface MainFrame_interface {
    interface CallBack {
        void buttonExit_onAction();
        void startViewArchive();
        void send_nMax(String textNmax);
    }
    void canvas_Clear(Color backColor);
    void canvas_Line(Color lineColor, double lineWidth, double x0, double y0, double x, double y);
    void outStatusWork(String text);
    double getHeightCanvas();
    Canvas getCanvas();
    void label2_txt(String text);
    void setCallBack(CallBack callBack);
    void setFieldsSamplePusher(Pusher pusher);
    void setFieldsMeasuredPusher(int n_cycle, int forceMeasure, int moveMeasure, float timeUnClenching, float timeClenching);
    void setFieldCurrentCycle(int n_cycle);
    TextControl getLabelAlarm();
    //
    void setFieldMaxNcycle(int maxNcycle);
    int getFieldMaxNcycle();
    //
    void setT_imp_up(String string);
}
