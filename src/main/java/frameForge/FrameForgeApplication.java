package frameForge;

import client.Client;
import frameForge.view.RegistrationController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class FrameForgeApplication extends Application {
     Client client  = new Client();
     RegistrationController registrationView;

    @Override
    public void start(Stage stage) throws IOException {
//        client.connectModels();
//        stage.setTitle("FrameForge");
//
//        FXMLLoader fxmlLoaderRegistration = new FXMLLoader(getClass().getResource("view/RegistrationView.fxml"));
//        Scene sceneRegistration = new Scene(fxmlLoaderRegistration.load(), 640, 480);
//
//        registrationView = fxmlLoaderRegistration.getController();
//        registrationView.viewModel.gui.model = client.regModel;
//
////        FXMLLoader fxmlLoaderMain = new FXMLLoader(getClass().getResource("view/MainPageView.fxml"));
////        Scene sceneRegistration = new Scene(fxmlLoaderMain.load(), 640, 480);
//
//        stage.setScene(sceneRegistration);
//        stage.show();
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.regModel.username = "I123";
        client.regModel.password = "123123123";
        client.registration();
        launch();
    }
}
