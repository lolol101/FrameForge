package frameforge.viewmodel;

import frameforge.model.LoginModel;
import frameforge.model.RegistrationModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LoginViewModel {
    private LoginModel model;
    public StringProperty nicknameProperty;
    public StringProperty passwordProperty;

    public LoginViewModel(LoginModel model) {
        this.model = model;
        nicknameProperty = new SimpleStringProperty();
        passwordProperty = new SimpleStringProperty();
    }

    public void setModel(LoginModel model) {
        this.model = model;
    }

    public LoginModel getModel() {
        return model;
    } // TODO: solve getter&setter abundance

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
