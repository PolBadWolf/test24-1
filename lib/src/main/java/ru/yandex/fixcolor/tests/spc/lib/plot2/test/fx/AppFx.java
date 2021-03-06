package ru.yandex.fixcolor.tests.spc.lib.plot2.test.fx;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ru.yandex.fixcolor.tests.spc.lib.plot2.Plot;
import ru.yandex.fixcolor.tests.spc.lib.plot2.test.CycleTest;

public class AppFx extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    CycleTest cycleTest = null;
    Thread threadCycle;

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas();
        canvas.setWidth(1000);
        canvas.setHeight(550);
        Parent root = new BorderPane(canvas);
        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setTitle("fx");
        primaryStage.setScene(scene);
        primaryStage.show();
        Plot.Parameters plotParameters = new Plot.Parameters();
        plotParameters.trend1_zeroY_min = 20;
        plotParameters.trend1_zeroY_max = 50;
        plotParameters.trend2_zeroY_min = 0;
        plotParameters.trend2_zeroY_max = 95;
        plotParameters.trend1_AutoZoomY = Plot.ZOOM_Y_FROM_SCALE;
        plotParameters.trend2_AutoZoomY = Plot.ZOOM_Y_FROM_VISUAL_DATA;
        plotParameters.scaleZero_zoomX = Plot.ZOOM_X_SHIFT;
        Plot plot = Plot.createFx(plotParameters, canvas);
        plot.clearScreen();
        plot.setPointBackMove_time(1_105);
        cycleTest = new CycleTest(plot);
        threadCycle = new Thread(cycleTest);
        threadCycle.start();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                cycleTest.flOnWork = false;
                try {
                    while (threadCycle.isAlive()) {
                        Thread.yield();
                        Thread.sleep(1);
                    }
                } catch (InterruptedException interruptedException) {
                }
                new Thread(() -> {
                    try {
                        plot.closeApp();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }).start();
                //event.consume();
//            primaryStage.show();
            }
        });
    }
}
