package org.example.test24.screen;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
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
        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainFrame.fxml"));

        try {
            root = (Parent) loader.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("title fx window");
        primaryStage.show();
        stage = primaryStage;
        stage.setResizable(false);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
                primaryStage.show();
            }
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
        //super.stop();
    }

    @Override
    public void setVisible(boolean visible) {
        boolean[] flWait = new boolean[] {true};
        Platform.runLater(()->{
            Stage window = (Stage) stage.getScene().getWindow();
            if (visible) {
                window.show();
            } else {
                window.hide();
            }
            flWait[0] = false;
        });
        while (flWait[0]) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
