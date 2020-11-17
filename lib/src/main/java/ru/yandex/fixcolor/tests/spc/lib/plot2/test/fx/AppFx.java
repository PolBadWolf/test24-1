package ru.yandex.fixcolor.tests.spc.lib.plot2.test.fx;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ru.yandex.fixcolor.tests.spc.lib.plot2.Plot;
import ru.yandex.fixcolor.tests.spc.lib.plot2.PlotParent;
import ru.yandex.fixcolor.tests.spc.lib.plot2.test.CiclTest;

public class AppFx extends Application {

    public static void main(String[] args) {
        launch(args);
    }

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
        plotParameters.trend1_zeroY_min = 0;
        plotParameters.trend1_zeroY_max = 52;
        plotParameters.zeroX_zoom = 2;
        Plot plot = Plot.createFx(plotParameters, canvas);
        plot.clear();
        new Thread(new CiclTest(plot)).start();
    }
}
