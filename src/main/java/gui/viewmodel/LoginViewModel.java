package gui.viewmodel;

import gui.model.LoginModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LoginViewModel {
    LoginModel model;
    public StringProperty nicknameProperty;
    public StringProperty passwordProperty;

    public LoginViewModel(LoginModel model) {
        this.model = model;

        nicknameProperty = new SimpleStringProperty();
        passwordProperty = new SimpleStringProperty();
    }

    public void sendLoginRequest() {
        // dummy method
        // TODO: naming
    }
}
