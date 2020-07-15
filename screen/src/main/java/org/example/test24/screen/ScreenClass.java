package org.example.test24.screen;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.test24.allinterface.Closer;

import java.io.IOException;

public class ScreenClass extends Application implements ScreenClass_impl, Runnable {

    public static void main(String[] args) {
        launch(args);
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
    }

    @Override
    public void exitApp() {
        Platform.exit();
    }

    @Override
    public void run() {
        ScreenClass.main(null);
    }

    @Override
    public void stop() throws Exception {
        Closer.getCloser().closeAll();
        super.stop();
    }
}
