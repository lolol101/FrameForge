package frameforge.viewmodel;

import frameforge.model.LoginModel;
import frameforge.model.RegistrationModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.swing.text.View;

public class LoginViewModel extends ViewModel<LoginModel> {
    public StringProperty nicknameProperty;
    public StringProperty passwordProperty;

    public LoginViewModel(LoginModel model) {
        this.model = model;
        nicknameProperty = new SimpleStringProperty();
        passwordProperty = new SimpleStringProperty();
    }

    public void sendLoginRequest() { // TODO: change to private?
        System.out.println("logViewModel: add-user request passed");
        model.username = nicknameProperty.getValue();
        model.password = passwordProperty.getValue();
        model.viewAction.setValue(LoginModel.ViewActions.authBtnClicked);
    }

    public void switchToRegistration() {
        System.out.println("logViewModel: switch-to-reg-window request passed");
        model.viewAction.setValue(LoginModel.ViewActions.switchToRegistrationBtnClicked);
    }
}
