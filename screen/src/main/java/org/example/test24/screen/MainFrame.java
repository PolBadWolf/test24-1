package org.example.test24.screen;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class MainFrame implements Initializable, MainFrame_imp {
    public static MainFrame mainFrame = null;

    private GraphicsContext gc = null;
    public Canvas canvas;

    public Label label1;
    public Label label2;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainFrame = this;
        gc = canvas.getGraphicsContext2D();
    }

    @Override
    public void canvas_Clear(Color backColor) {
        Platform.runLater( ()->{
            gc.setFill(backColor);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.stroke();
        });
    }

    @Override
    public void canvas_Line(Color lineColor, double lineWidth, double x0, double y0, double x, double y) {
        Platform.runLater( ()->{
            gc.beginPath();
            gc.setStroke(lineColor);
            gc.setLineWidth(lineWidth);
            gc.moveTo(x0, y0);
            gc.lineTo(x, y);
            gc.stroke();
            gc.closePath();
        });
    }

    @Override
    public void label1_txt(String text) {
        Platform.runLater( ()->label1.setText(text) );
    }

    @Override
    public double getHeighCanvas() {
        return canvas.getHeight();
    }

    @Override
    public Canvas getCanvas1() {
        return canvas;
    }

    @Override
    public void label2_txt(String text) {
        Platform.runLater( ()->label2.setText(text) );
    }
}
