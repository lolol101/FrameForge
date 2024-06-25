package frameforge;

import frameforge.client.Client;
import frameforge.view.LoginController;
import frameforge.view.MainPageController;
import frameforge.view.PostCreationController;
import frameforge.view.RegistrationController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class FrameForgeApplication extends Application {
    RegistrationController registrationView;
    LoginController loginView;
    MainPageController mainPageView;
    PostCreationController postCreationView;

    @Override
    public void start(Stage stage) throws IOException {
        Client client = new Client();

        client.connectListeners();
        client.socketManager.connect("212.113.122.236", 8080);

        stage.setTitle("Frameforge");
        stage.getIcons().add(new Image(Objects.requireNonNull(FrameForgeApplication.class.getResourceAsStream("app-icon.png"))));

        FXMLLoader fxmlLoaderRegistration = new FXMLLoader(getClass().getResource("view/RegistrationView.fxml"));
        Scene sceneRegistration = new Scene(fxmlLoaderRegistration.load(), 1024, 768);
        registrationView = fxmlLoaderRegistration.getController();
        registrationView.setModel(client.regModel);
        registrationView.passStageAndScene(stage, sceneRegistration);

        FXMLLoader fxmlLoaderLogin = new FXMLLoader(getClass().getResource("view/LoginView.fxml"));
        Scene sceneLogin = new Scene(fxmlLoaderLogin.load(), 1024, 768);
        loginView = fxmlLoaderLogin.getController();
        loginView.setModel(client.loginModel);
        loginView.passStageAndScene(stage, sceneLogin);

        FXMLLoader fxmlLoaderMain = new FXMLLoader(getClass().getResource("view/MainPageView.fxml"));
        Scene sceneMain = new Scene(fxmlLoaderMain.load(), 1024, 768);
        mainPageView = fxmlLoaderMain.getController();
        mainPageView.setModel(client.mainPageModel);
        mainPageView.passStageAndScene(stage, sceneMain);

        FXMLLoader fxmlLoaderPost = new FXMLLoader(getClass().getResource("view/PostCreation.fxml"));
        Scene scenePost = new Scene(fxmlLoaderPost.load(), 1024, 768);
        postCreationView = fxmlLoaderPost.getController();
        postCreationView.setModel(client.postCreationModel);
        postCreationView.passStageAndScene(stage, scenePost);

        stage.setScene(sceneLogin);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
