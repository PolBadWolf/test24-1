package ru.yandex.fixcolor.tests.spc.screen;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class ScreenClass extends Application implements ScreenFx {
    private static Closer closer;
    public static Stage stage;

    public ScreenClass(Closer closer) {
        this.closer = closer;
    }

    public ScreenClass() {
    }

    @Override
    public void main() {
        new Thread(()-> {
            launch(new String[0]);
        }, "Screen-Fx").start();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainFrame.fxml"));
        Parent root = loader.load();

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("График испытания гидротолкателей");
        primaryStage.show();
        stage = primaryStage;
        //stage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            primaryStage.show();
        });
    }

    @Override
    public void exitApp() {
        MainFrame.mainFrame = null;
        Platform.exit();
    }

    @Override
    public void stop() throws Exception {
        closer.close();
        super.stop();
    }

    @Override
    public void setRootFocus() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.requestFocus();
            }
        });
    }
}
