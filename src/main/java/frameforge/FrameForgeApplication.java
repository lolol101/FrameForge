package frameforge;

import frameforge.client.Client;
import frameforge.view.LoginController;
import frameforge.view.MainPageController;
import frameforge.view.RegistrationController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FrameForgeApplication extends Application {
    RegistrationController registrationView;
    LoginController loginView;
    MainPageController mainPageView;

    @Override
    public void start(Stage stage) throws IOException {
        Client client = new Client();
        client.regModel.username = "I123";
        client.regModel.password = "123123123";
        client.registration();
        client.connectModels();
        stage.setTitle("frameforge");

        FXMLLoader fxmlLoaderRegistration = new FXMLLoader(getClass().getResource("view/RegistrationView.fxml"));
        Scene sceneRegistration = new Scene(fxmlLoaderRegistration.load(), 640, 480);
        registrationView = fxmlLoaderRegistration.getController();
        registrationView.setModel(client.regModel); // TODO: move to client.connectModels(...)
        registrationView.passStageAndScene(stage, sceneRegistration);
//        System.out.println("debug: stage is " + stage.hashCode() + "; reg scene is " + sceneRegistration.hashCode());

        FXMLLoader fxmlLoaderLogin = new FXMLLoader(getClass().getResource("view/LoginView.fxml"));
        Scene sceneLogin = new Scene(fxmlLoaderLogin.load(), 640, 480);
        loginView = fxmlLoaderLogin.getController();
        loginView.setModel(client.loginModel); // TODO: move to client.connectModels(...)
        loginView.passStageAndScene(stage, sceneLogin);

        FXMLLoader fxmlLoaderMain = new FXMLLoader(getClass().getResource("view/MainPageView.fxml"));
        Scene sceneMain = new Scene(fxmlLoaderMain.load(), 640, 480);
        mainPageView = fxmlLoaderMain.getController();
        mainPageView.setModel(client.mainPageModel); // TODO: move to client.connectModels(...)
        mainPageView.passStageAndScene(stage, sceneMain);

//        System.out.println("debug: stage is " + stage.hashCode() + "; login scene is " + sceneLogin.hashCode() + "; reg scene is " + sceneRegistration.hashCode());




////        FXMLLoader fxmlLoaderMain = new FXMLLoader(getClass().getResource("view/MainPageView.fxml"));
////        Scene sceneRegistration = new Scene(fxmlLoaderMain.load(), 640, 480);
//
        stage.setScene(sceneRegistration);
        stage.show();
//
//        client.openRegistrationMenu();
//        client.closeRegistrationMenu();
//        client.openLoginMenu();
//        client.closeLoginMenu();
//        client.openMainPageMenu();
//        client.closeMainPageMenu();
    }

    public static void main(String[] args) {
        launch();
    }
}
