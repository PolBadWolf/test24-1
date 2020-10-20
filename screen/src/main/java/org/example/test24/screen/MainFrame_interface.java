package org.example.test24.screen;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import org.example.test24.bd.usertypes.Pusher;
import org.example.test24.bd.usertypes.TypePusher;

public interface MainFrame_interface {
    interface CallBack {
        void buttonExit_onAction();
    }
    void canvas_Clear(Color backColor);
    void canvas_Line(Color lineColor, double lineWidth, double x0, double y0, double x, double y);
    void label1_txt(String text);
    double getHeightCanvas();
    Canvas getCanvas();
    void label2_txt(String text);
    void setCallBack(CallBack callBack);
    void setFieldsSamplePusher(Pusher pusher);
    void setFieldsMeasuredPusher(int n_cicle, int ves, int move, int timeUnClenching);
}
