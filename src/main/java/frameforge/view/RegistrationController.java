package frameforge.view;

import frameforge.model.RegistrationModel;
import frameforge.viewmodel.RegistrationViewModel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.io.IOException;

import static frameforge.model.RegistrationModel.ClientCommands;

public class RegistrationController extends Controller<RegistrationModel, RegistrationViewModel> {
    @FXML protected Button btnSubmitRegistrationRequest;
    @FXML private TextField nicknameTextField;
    @FXML private PasswordField passwordTextField;

    private Timeline registerBtnResetTimeline;
    private static final int SECONDS_FOR_REQUEST = 3;

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

    @Override
    public void setModel(RegistrationModel model) {
        removeListeners();
        viewModel = new RegistrationViewModel(model);
        addListeners();
        System.out.println("registrationView: registration model set");
    }

    void removeListeners() {
        nicknameTextField.textProperty().unbindBidirectional(viewModel.nicknameProperty);
        passwordTextField.textProperty().unbindBidirectional(viewModel.passwordProperty);
        viewModel.getModel().clientCommand.removeListener(clientCommandReceiver);
        System.out.println("registrationView: registration listeners removed");
    }

    void addListeners() {
        nicknameTextField.textProperty().bindBidirectional(viewModel.nicknameProperty);
        passwordTextField.textProperty().bindBidirectional(viewModel.passwordProperty);
        viewModel.getModel().clientCommand.addListener(clientCommandReceiver);
        System.out.println("registrationView: registration listeners added");
    }

    @FXML public void initialize() {
        addListeners();
    }

    @FXML private void onBtnSubmitRegistrationRequestClick() {
        System.out.println("regView: registration request button pressed");
        btnSubmitRegistrationRequest.setDisable(true);
        registerBtnResetTimeline = new Timeline(new KeyFrame(Duration.seconds(SECONDS_FOR_REQUEST), event -> btnSubmitRegistrationRequest.setDisable(false)));
        registerBtnResetTimeline.play();
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

    @Override void hideInView() {
        if (registerBtnResetTimeline != null && registerBtnResetTimeline.getStatus() == Timeline.Status.RUNNING) {
            registerBtnResetTimeline.stop();
        }
        System.out.println(this.getClass() + " hidden in view");
    }
}