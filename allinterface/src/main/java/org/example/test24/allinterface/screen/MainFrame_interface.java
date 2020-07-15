package org.example.test24.allinterface.screen;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

public interface MainFrame_interface {
    void canvas_Clear(Color backColor);
    void canvas_Line(Color lineColor, double lineWidth, double x0, double y0, double x, double y);
    void label1_txt(String text);
    double getHeighCanvas();
    Canvas getCanvas1();
    void label2_txt(String text);
}
