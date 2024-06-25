package frameforge.viewmodel;

import frameforge.model.LoginModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import static frameforge.model.LoginModel.ViewActions;

public class LoginViewModel extends ViewModel<LoginModel> {
    public StringProperty nicknameProperty;
    public StringProperty passwordProperty;

    public LoginViewModel(LoginModel model) {
        this.model = model;
        nicknameProperty = new SimpleStringProperty();
        passwordProperty = new SimpleStringProperty();
    }

    public void sendLoginRequest() {
        System.out.println("logViewModel: add-user request passed");
        model.username = nicknameProperty.getValue();
        model.password = passwordProperty.getValue();
        model.viewAction.setValue(ViewActions.authBtnClicked);
    }

    public void switchToRegistration() {
        System.out.println("logViewModel: switch-to-reg-window request passed");
        model.viewAction.setValue(ViewActions.switchToRegistrationBtnClicked);
    }
}
