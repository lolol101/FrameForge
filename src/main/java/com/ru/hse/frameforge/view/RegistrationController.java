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
    @FXML private TextField nameTextField;
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
        nameTextField.textProperty().bindBidirectional(viewModel.nameProperty);
        nicknameTextField.textProperty().bindBidirectional(viewModel.nicknameProperty);
        passwordTextField.textProperty().bindBidirectional(viewModel.passwordProperty);
    }

    @FXML private void onBtnSubmitRequestClick() {
        // TODO: prevent multiple button clicks;
        // TODO: request in separate thread
        sendRegistrationRequest();
    }

    @FXML private void onBtnSwitchToLoginWindowClick(ActionEvent event) throws IOException { // TODO: exception handling
        switchToLoginScene(event);
    }

    private void sendRegistrationRequest() {
        viewModel.addUser(); // TODO: associated methods naming conventions; model-view-viewmodel responsibility distribution arrangement
    }

    private void switchToLoginScene(ActionEvent event) throws IOException {
//        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
//        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("LoginView.fxml")));
//        window.getScene().setRoot(newRoot);
////         TODO: switch between already existing scenes! Very important
////        FXMLLoader fxmlLoaderRegistration = new FXMLLoader(getClass().getResource("LoginView.fxml"));
////        Scene loginScene = new Scene(fxmlLoaderRegistration.load(), 640, 480);
////        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
////        window.setScene(loginScene);
//        window.show();
        Stage stage = (Stage) btnSwitchToRegistration.getScene().getWindow();
        stage.close();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("LoginView.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
