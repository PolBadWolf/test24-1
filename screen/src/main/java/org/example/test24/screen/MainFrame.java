package org.example.test24.screen;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.example.test24.allinterface.screen.MainFrame_interface;

import java.net.URL;
import java.util.ResourceBundle;

public class MainFrame implements Initializable, MainFrame_interface {
    public static MainFrame mainFrame = null;
    public Button buttonExit;
    public TextField state;
    public TextField get_Force;
    public TextField set_Force;
    public TextField get_TypePusher;
    public TextField set_TypePusher;
    public TextField get_Move;
    public TextField set_Move;
    public TextField get_Unclenching;
    public TextField set_Unclenching;

    private GraphicsContext gc = null;
    private CallBack callBack;
    public Canvas canvas;

    public Label label1;
    public Label label2;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainFrame = this;
        gc = canvas.getGraphicsContext2D();
        //Platform.runLater(()->);
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
    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    public void label2_txt(String text) {
        Platform.runLater( ()->label2.setText(text) );
    }

    public void exitOnAction(ActionEvent actionEvent) {

    }

    @Override
    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }
}
