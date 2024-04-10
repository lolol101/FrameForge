package frameforge.view;

import frameforge.model.LoginModel;
import frameforge.viewmodel.LoginViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginController {
    // TODO: scene/stage cleanup: move from creating new stages to switching scenes in single stage: problem is hierarchy & scene dependency on main()
    @FXML private TextField nickname;
    @FXML private PasswordField password;
    @FXML protected Button btnSwitchToRegistration;
    @FXML protected Button btnSubmitLoginRequest;
    protected BooleanProperty isLoaded; // TODO: is to be connected to client's BooleanProperty in single direction; due client implementation

    // TODO: current textArea usage sucks! Blend into background, set text alignment, correct text position
    private final LoginViewModel viewModel;

    public LoginController() {
        LoginModel model = new LoginModel();
        viewModel = new LoginViewModel(model);
        isLoaded = new SimpleBooleanProperty(false);

        isLoaded.addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                try {
                    showInView();
                } catch (IOException e) {
                    System.out.println("Error: unable to open login menu"); // TODO:
                    throw new RuntimeException(e);
                }
            } else {
                hideView();
            }
        });
    }

    @FXML public void initialize() {
        nickname.textProperty().bindBidirectional(viewModel.nicknameProperty);
        password.textProperty().bindBidirectional(viewModel.passwordProperty);
        // TODO: isLoaded need to be bound to a client' booleanProperty
    }

    @FXML private void onBtnSubmitLoginRequestClick() {
        // TODO: is needed? need to prevent button mashing, but where?
        sendLoginRequest();
    }

    @FXML private void onBtnSwitchToRegistrationClick(ActionEvent event) throws IOException { // TODO: exception handling
        switchToRegistrationScene(event);
    }

    private void sendLoginRequest() {
        viewModel.sendLoginRequest();
    }

    @FXML private void switchToRegistrationScene(ActionEvent event) throws IOException { // TODO: exception handling
        // TODO: move from button to event reference?
        Stage stage = (Stage) btnSwitchToRegistration.getScene().getWindow();
        stage.close();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("RegistrationView.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void showInView() throws IOException {
        Stage stage = (Stage) btnSwitchToRegistration.getScene().getWindow();
        stage.close();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("LoginView.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void hideView() {
        Stage stage = (Stage) btnSwitchToRegistration.getScene().getWindow();
        stage.close();
    }
}
