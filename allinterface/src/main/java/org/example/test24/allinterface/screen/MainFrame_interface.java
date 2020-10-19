package org.example.test24.allinterface.screen;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

public interface MainFrame_interface {
    interface CallBack {
        void buttonExit_onAction();
    }
    void canvas_Clear(Color backColor);
    void canvas_Line(Color lineColor, double lineWidth, double x0, double y0, double x, double y);
    void label1_txt(String text);
    double getHeighCanvas();
    Canvas getCanvas();
    void label2_txt(String text);
    void setCallBack(CallBack callBack);
}
