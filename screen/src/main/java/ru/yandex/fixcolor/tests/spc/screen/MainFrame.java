package ru.yandex.fixcolor.tests.spc.screen;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import ru.yandex.fixcolor.tests.spc.bd.usertypes.Pusher;
import ru.yandex.fixcolor.tests.spc.lib.MyLogger;
import ru.yandex.fixcolor.tests.spc.lib.fx.LabelTextFlash;
import ru.yandex.fixcolor.tests.spc.lib.fx.TextControl;
import ru.yandex.fixcolor.tests.spc.lib.fx.TextUtils;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class MainFrame implements Initializable, MainFrame_interface {
    public static MainFrame mainFrame = null;
    public Label alarmMessage;
    public TextControl alarmMessageFlashText;
    private GraphicsContext gc = null;
    private CallBack callBack;

    public Label state;
    public Button buttonExit;
    public Button buttonArchive;
    public Canvas canvas;
    public Label set_Pusher;
    public Label set_TypePusher;
    public Label get_Force;
    public Label set_Force;
    public Label get_Move;
    public Label set_Move;
    public Label get_Unclenching;
    public Label set_Unclenching;
    public Label set_Clenching;
    public Label get_Clenching;
    public TextField s_nCicle;
    public Label l_nCicle;
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
    public void outStatusWork(String text) {
        Platform.runLater( ()->state.setText(text) );
    }

    @Override
    public double getHeightCanvas() {
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
        callBack.buttonExit_onAction();
    }

    @Override
    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void setFieldsSamplePusher(Pusher pusher) {
        Platform.runLater(()->{
            set_Pusher.setText(pusher.loggerPusher.namePusher);
            set_TypePusher.setText(pusher.loggerPusher.typePusher.loggerTypePusher.nameType);
            set_Force.setText(String.valueOf(pusher.loggerPusher.typePusher.loggerTypePusher.forceNominal));
            set_Move.setText(String.valueOf(pusher.loggerPusher.typePusher.loggerTypePusher.moveNominal));
            set_Unclenching.setText(String.valueOf(pusher.loggerPusher.typePusher.loggerTypePusher.unclenchingTime));
        });
    }

    @Override
    public void setFieldsMeasuredPusher(int n_cycle, int forceMeasure, int moveMeasure, float timeUnClenching, float timeClenching) {
        Platform.runLater(()->{
            l_nCicle.setText(String.valueOf(n_cycle));
            get_Force.setText(String.valueOf(forceMeasure));
            get_Move.setText(String.valueOf(moveMeasure));
            get_Unclenching.setText(String.valueOf(timeUnClenching));
            get_Clenching.setText(String.valueOf(timeClenching));
        });
    }

    @Override
    public void setFieldCurrentCycle(int n_cycle) {
        Platform.runLater(()->{
            l_nCicle.setText(String.valueOf(n_cycle));
        });
    }

    public void archiveOnAction(ActionEvent actionEvent) {
        callBack.startViewArchive();
    }

    @Override
    public TextControl getLabelAlarm() {
        if (alarmMessageFlashText == null) {
            alarmMessageFlashText = new LabelTextFlash(alarmMessage);
        }
        return alarmMessageFlashText;
    }

    public void on_cycleMax(ActionEvent actionEvent) {
        callBack.send_nMax(s_nCicle.getText());
    }

    @Override
    public void setFieldMaxNcycle(int maxNcycle) {
        s_nCicle.setText(String.valueOf(maxNcycle));
    }

    @Override
    public int getFieldMaxNcycle() {
        int nMax = 1;
        try {
            nMax = Integer.parseInt(s_nCicle.getText());
        } catch (Exception e) {
            MyLogger.myLog.log(Level.WARNING, "ошибка чтения параметра", e);
            nMax = 1;
        } finally {
            if (nMax < 1 || nMax > 32) {
                nMax = 1;
            }
        }
        return nMax;
    }
}
