package gui;

import client.Client;
import gui.view.RegistrationController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FrameForgeApplication extends Application {
     Client client  = new Client();
     RegistrationController registrationView;

    @Override
    public void start(Stage stage) throws IOException {
        client.connectModels();
        stage.setTitle("FrameForge");

        FXMLLoader fxmlLoaderRegistration = new FXMLLoader(getClass().getResource("view/RegistrationView.fxml"));
        Scene sceneRegistration = new Scene(fxmlLoaderRegistration.load(), 640, 480);

        registrationView = fxmlLoaderRegistration.getController();
        registrationView.viewModel.model = client.regModel;

//        FXMLLoader fxmlLoaderMain = new FXMLLoader(getClass().getResource("view/MainPageView.fxml"));
//        Scene sceneRegistration = new Scene(fxmlLoaderMain.load(), 640, 480);

        stage.setScene(sceneRegistration);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}