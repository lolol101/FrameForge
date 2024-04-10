package com.ru.hse.frameforge.view;

import com.ru.hse.frameforge.model.RegistrationModel;
import com.ru.hse.frameforge.viewmodel.RegistrationViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class RegistrationController {
    // TODO: standardise UI elements naming
    @FXML private TextField nicknameTextField;
    @FXML private TextField passwordTextField;

    @FXML private Button btnSubmitRequest; // TODO: grey out on click
    @FXML private Button btnSwitchToRegistration;

    private final RegistrationViewModel viewModel;

    public RegistrationController() {
        RegistrationModel model = new RegistrationModel();
        viewModel = new RegistrationViewModel(model);
    }

    @FXML public void initialize() {
        nicknameTextField.textProperty().bindBidirectional(viewModel.nicknameProperty);
        passwordTextField.textProperty().bindBidirectional(viewModel.passwordProperty);
    }

    @FXML private void onBtnSubmitRequestClick() {
        // TODO: prevent multiple button clicks - goes here
        sendRegistrationRequest();
    }

    @FXML private void onBtnSwitchToLoginWindowClick(ActionEvent event) throws IOException { // TODO: exception handling
        switchToLoginScene(event);
    }

    private void sendRegistrationRequest() {
        viewModel.addUser(); // TODO: associated methods naming conventions; model-view-viewmodel responsibility distribution arrangement
    }

    private void switchToLoginScene(ActionEvent event) throws IOException {
        Stage stage = (Stage) btnSwitchToRegistration.getScene().getWindow();
        stage.close();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("LoginView.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
