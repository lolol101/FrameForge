package frameforge;

import frameforge.client.Client;
import frameforge.client.SocketManager;
import frameforge.view.RegistrationController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FrameForgeApplication extends Application {
     Client client  = new Client();
     RegistrationController registrationView;

    @Override
    public void start(Stage stage) throws IOException {
        Client client = new Client();

        client.connectListeners();
        client.socketManager.connect("188.225.82.247", 8080);

        Thread thread = new Thread(() -> {
           while (true) {
               client.socketManager.acceptJson();
           }
        });

        thread.start();

        stage.setTitle("frameforge");
        FXMLLoader fxmlLoaderRegistration = new FXMLLoader(getClass().getResource("view/RegistrationView.fxml"));
        Scene sceneRegistration = new Scene(fxmlLoaderRegistration.load(), 640, 480);
        registrationView = fxmlLoaderRegistration.getController();

        registrationView.viewModel.model = client.regModel;
        stage.setScene(sceneRegistration);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
