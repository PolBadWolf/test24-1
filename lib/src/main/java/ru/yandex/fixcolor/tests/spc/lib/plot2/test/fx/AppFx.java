package ru.yandex.fixcolor.tests.spc.lib.plot2.test.fx;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ru.yandex.fixcolor.tests.spc.lib.plot2.Plot;

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
        Plot.Parameters parameters = new Plot.Parameters();
        Plot plot = Plot.createFx(parameters, canvas);
        plot.clear();
    }
}
