package frameforge.view;

import frameforge.model.LoginModel;
import frameforge.viewmodel.LoginViewModel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

import static frameforge.model.LoginModel.ClientCommands;

public class LoginController extends Controller<LoginModel, LoginViewModel> {
    @FXML private TextField nicknameTextField;
    @FXML private PasswordField passwordTextField;
    @FXML protected Button btnSwitchToRegistration;
    @FXML protected Button btnSubmitLoginRequest;

    private Timeline loginBtnResetTimeline;
    private static final int SECONDS_FOR_REQUEST = 3;

    private final ChangeListener<ClientCommands> clientCommandReceiver = (obs, oldCommand, newCommand) -> {
        System.out.println("logView: changeListener fired on client command reception");
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

    public LoginController() {
        LoginModel model = new LoginModel();
        viewModel = new LoginViewModel(model);
    }

    @Override
    public void setModel(LoginModel model) {
        removeListeners();
        viewModel.setModel(model);
        addListeners();
        System.out.println("logView: log model set");
    }

    void removeListeners() {
        nicknameTextField.textProperty().unbindBidirectional(viewModel.nicknameProperty);
        passwordTextField.textProperty().unbindBidirectional(viewModel.passwordProperty);
        viewModel.getModel().clientCommand.removeListener(clientCommandReceiver);
        System.out.println("logView: log listeners removed");
    }

    void addListeners() {
        nicknameTextField.textProperty().bindBidirectional(viewModel.nicknameProperty);
        passwordTextField.textProperty().bindBidirectional(viewModel.passwordProperty);
        viewModel.getModel().clientCommand.addListener(clientCommandReceiver);
        System.out.println("logView: log listeners added");
    }

    @FXML public void initialize() {
        addListeners();
    }

    public void passStageAndScene(Stage stage, Scene scene) {
        this.stage = stage;
        this.scene = scene;
        System.out.println("logView: this.scene=" + scene.hashCode() + "; this.stage=" + stage.hashCode());
    }

    @FXML private void onBtnSubmitLoginRequestClick() {
        System.out.println("logView: reg request button pressed");
        btnSubmitLoginRequest.setDisable(true);
        loginBtnResetTimeline = new Timeline(new KeyFrame(Duration.seconds(SECONDS_FOR_REQUEST), event -> btnSubmitLoginRequest.setDisable(false)));
        loginBtnResetTimeline.play();
        sendLoginRequest();
    }

    @FXML private void onBtnSwitchToRegistrationClick() {
        System.out.println("logView: switch-to-reg request button pressed");
        sendSwitchToRegistrationRequest();
    }

    private void sendSwitchToRegistrationRequest() {
        viewModel.switchToRegistration();
    }

    private void sendLoginRequest() {
        viewModel.sendLoginRequest();
    }

    @Override void hideInView() {
        if (loginBtnResetTimeline != null && loginBtnResetTimeline.getStatus() == Timeline.Status.RUNNING) {
            loginBtnResetTimeline.stop();
        }
        System.out.println(this.getClass() + " hidden in view");
    }
}
