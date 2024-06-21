package frameforge.view;

import frameforge.model.LoginModel;
import frameforge.viewmodel.LoginViewModel;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import static frameforge.model.LoginModel.ClientCommands;

public class LoginController extends Controller<LoginModel, LoginViewModel> {
    // TODO: scene/stage cleanup: move from creating new stages to switching scenes in single stage: problem is hierarchy & scene dependency on main()
    @FXML private TextField nicknameTextField;
    @FXML private PasswordField passwordTextField;
    @FXML protected Button btnSwitchToRegistration;
    @FXML protected Button btnSubmitLoginRequest;
    // TODO: current textArea usage sucks! Blend into background, set text alignment, correct text position

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
        // TODO: is needed? need to prevent button mashing, but where?
        System.out.println("logView: reg request button pressed");
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
}
