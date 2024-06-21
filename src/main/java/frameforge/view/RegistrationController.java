package frameforge.view;

import frameforge.model.RegistrationModel;
import frameforge.viewmodel.RegistrationViewModel;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import static frameforge.model.RegistrationModel.ClientCommands;

public class RegistrationController {
    private Stage stage; // single stage instance shared with some other menus
    private Scene scene; // unique scene used to avoid repeated loading of the same menu

    // TODO: standardise UI elements naming
    @FXML private TextField nicknameTextField;
    @FXML private PasswordField passwordTextField;

    @FXML private Button btnSubmitRequest; // TODO: grey out on click
    @FXML private Button btnSwitchToLogin;

    private RegistrationViewModel viewModel;

    private final ChangeListener<ClientCommands> clientCommandReceiver = (obs, oldCommand, newCommand) -> {
        System.out.println("regView: changeListener fired on client command reception");
        switch (newCommand) {
            case show -> {
                try {
                    openInView();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case close -> hideInView();
        }
        viewModel.getModel().clientCommand.setValue(ClientCommands.zero);
    };

    public RegistrationController() {
        RegistrationModel model = new RegistrationModel();
        viewModel = new RegistrationViewModel(model);
    }

    public void setModel(RegistrationModel model) {
        removeListeners();
        viewModel = new RegistrationViewModel(model);
        addListeners();
        System.out.println("registrationView: registration model set");
    }

    private void removeListeners() {
        nicknameTextField.textProperty().unbindBidirectional(viewModel.nicknameProperty);
        passwordTextField.textProperty().unbindBidirectional(viewModel.passwordProperty);
        viewModel.getModel().clientCommand.removeListener(clientCommandReceiver);
        System.out.println("registrationView: registration listeners removed");
    }

    private void addListeners() {
        nicknameTextField.textProperty().bindBidirectional(viewModel.nicknameProperty);
        passwordTextField.textProperty().bindBidirectional(viewModel.passwordProperty);
        viewModel.getModel().clientCommand.addListener(clientCommandReceiver);
        System.out.println("registrationView: registration listeners added");
    }


    @FXML public void initialize() {
        addListeners();
    }

    public void passStageAndScene(Stage stage, Scene scene) {
        this.stage = stage;
        this.scene = scene;
        System.out.println("regView: this.scene=" + scene.hashCode() + "; this.stage=" + stage.hashCode());
    }

    @FXML private void onBtnSubmitRequestClick() {
        // TODO: prevent multiple button clicks
        System.out.println("regView: registration request button pressed");
        sendRegistrationRequest();
    }

    @FXML private void onBtnSwitchToLoginWindowClick() {
        System.out.println("regView: switch-to-login-window button pressed");
        sendSwitchToLoginRequest();
    }

    private void sendRegistrationRequest() {
        viewModel.addUser();
    }

    private void sendSwitchToLoginRequest() {
        viewModel.switchToLogin();
    }

    public void openInView() throws IOException {
        System.out.println("regView: open-in-view request received");
        System.out.println("regView: setting scene" + scene.hashCode() + " to stage " + stage.hashCode());
        stage.setScene(scene);
        stage.show();
    }

    public void hideInView() {
        System.out.println("regView: close-in-view request received");
    }
}