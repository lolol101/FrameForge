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

public class LoginController {
    private Stage stage; // single stage instance shared with some other menus
    private Scene scene; // unique scene used to avoid repeated loading of the same menu

    // TODO: scene/stage cleanup: move from creating new stages to switching scenes in single stage: problem is hierarchy & scene dependency on main()
    @FXML private TextField nicknameTextField;
    @FXML private PasswordField passwordTextField;
    @FXML protected Button btnSwitchToRegistration;
    @FXML protected Button btnSubmitLoginRequest;
    // TODO: current textArea usage sucks! Blend into background, set text alignment, correct text position
    private LoginViewModel viewModel;

    private final ChangeListener<LoginModel.ClientCommands> clientCommandReceiver = (obs, oldCommand, newCommand) -> {
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
        viewModel.getModel().clientCommand.setValue(LoginModel.ClientCommands.zero);
    };

    public LoginController() {
        LoginModel model = new LoginModel();
        viewModel = new LoginViewModel(model);
    }

    public void setModel(LoginModel model) {
        removeListeners();
        viewModel = new LoginViewModel(model);
        addListeners();
        System.out.println("logView: log model set");
    }

    private void removeListeners() {
        nicknameTextField.textProperty().unbindBidirectional(viewModel.nicknameProperty);
        passwordTextField.textProperty().unbindBidirectional(viewModel.passwordProperty);
        viewModel.getModel().clientCommand.removeListener(clientCommandReceiver);
        System.out.println("logView: log listeners removed");
    }

    private void addListeners() {
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

    public void openInView() throws IOException {
        System.out.println("logView: open-in-view request received");
        System.out.println("logView: setting scene" + scene.hashCode() + " to stage " + stage.hashCode());
        stage.setScene(scene);
        stage.show();
    }

    public void hideInView() {
        System.out.println("logView: close-in-view request received");
//        Stage stage = (Stage) btnSwitchToRegistration.getScene().getWindow();
//        stage.close();
    }
}
