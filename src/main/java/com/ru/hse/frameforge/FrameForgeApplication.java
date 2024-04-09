package com.ru.hse.frameforge;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class FrameForgeApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("FrameForge");

        FXMLLoader fxmlLoaderRegistration = new FXMLLoader(getClass().getResource("view/RegistrationView.fxml"));
        Scene sceneRegistration = new Scene(fxmlLoaderRegistration.load(), 640, 480);

//        FXMLLoader fxmlLoaderMain = new FXMLLoader(getClass().getResource("view/MainPageView.fxml"));
//        Scene sceneRegistration = new Scene(fxmlLoaderMain.load(), 640, 480);

        stage.setScene(sceneRegistration);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
