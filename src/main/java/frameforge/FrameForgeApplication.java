package frameforge;

import frameforge.client.Client;
import frameforge.view.LoginController;
import frameforge.view.MainPageController;
import frameforge.view.RegistrationController;
import javafx.application.Application;
import javafx.concurrent.Task;
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

        client.connectListeners();
        client.socketManager.connect("188.225.82.247", 8080);

        try {
            new Thread(new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while (true) {
                        Thread.sleep(1);
                        client.socketManager.acceptJson();
                    }
                }
            }).start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        stage.setTitle("frameforge");

        // TODO: move to client method all 3+ of repeated code fragments. Or to an app method?
        //  Will be easier with better inheritance in *Controller classes
        FXMLLoader fxmlLoaderRegistration = new FXMLLoader(getClass().getResource("view/RegistrationView.fxml"));
        Scene sceneRegistration = new Scene(fxmlLoaderRegistration.load(), 640, 480);
        registrationView = fxmlLoaderRegistration.getController();
        registrationView.setModel(client.regModel);
        registrationView.passStageAndScene(stage, sceneRegistration);

        FXMLLoader fxmlLoaderLogin = new FXMLLoader(getClass().getResource("view/LoginView.fxml"));
        Scene sceneLogin = new Scene(fxmlLoaderLogin.load(), 640, 480);
        loginView = fxmlLoaderLogin.getController();
        loginView.setModel(client.loginModel);
        loginView.passStageAndScene(stage, sceneLogin);

        FXMLLoader fxmlLoaderMain = new FXMLLoader(getClass().getResource("view/MainPageView.fxml"));
        Scene sceneMain = new Scene(fxmlLoaderMain.load(), 640, 480);
        mainPageView = fxmlLoaderMain.getController();
        mainPageView.setModel(client.mainPageModel);
        mainPageView.passStageAndScene(stage, sceneMain);

        stage.setScene(sceneRegistration);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
