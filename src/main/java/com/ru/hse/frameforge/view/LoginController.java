package com.ru.hse.frameforge.view;

import com.ru.hse.frameforge.model.LoginModel;
import com.ru.hse.frameforge.viewmodel.LoginViewModel;
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
    @FXML private TextField nickname;
    @FXML private PasswordField password;
    @FXML protected Button btnSwitchToRegistration;
    @FXML protected Button btnSubmitLoginRequest;

    // TODO: current textArea usage sucks! Blend into background, set text alignment, correct text position
    private final LoginViewModel viewModel;

    public LoginController() {
        LoginModel model = new LoginModel();
        viewModel = new LoginViewModel(model);
    }

    @FXML public void initialize() {
        nickname.textProperty().bindBidirectional(viewModel.nicknameProperty);
        password.textProperty().bindBidirectional(viewModel.passwordProperty);
    }

    @FXML private void onBtnSubmitLoginRequestClick() {
        // TODO: is needed a different method here because of future button mashing prevention implementation?
        sendLoginRequest();
    }

    @FXML private void onBtnSwitchToRegistrationClick(ActionEvent event) throws IOException { // TODO: exception handling
        switchToRegistrationScene(event);
    }

    private void sendLoginRequest() {
        viewModel.sendLoginRequest();
    }

    @FXML private void switchToRegistrationScene(ActionEvent event) throws IOException {
        // TODO: switch between existing scenes! Very important
//        FXMLLoader fxmlLoaderRegistration = new FXMLLoader(getClass().getResource("LoginView.fxml"));
//
//        Scene loginScene = new Scene(fxmlLoaderRegistration.load(), 640, 480);
//
//        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
//        window.setScene(loginScene);
//        window.show();
        // TODO: move from button to event reference
        Stage stage = (Stage) btnSwitchToRegistration.getScene().getWindow();
        stage.close();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("RegistrationView.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
