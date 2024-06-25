package frameforge.viewmodel;

import frameforge.model.RegistrationModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import static frameforge.model.RegistrationModel.ViewActions;

public class RegistrationViewModel extends ViewModel<RegistrationModel> {
    public StringProperty nicknameProperty;
    public StringProperty passwordProperty;

    public RegistrationViewModel(RegistrationModel model) {
        this.model = model;
        nicknameProperty = new SimpleStringProperty();
        passwordProperty = new SimpleStringProperty();
    }

    public void addUser() {
        System.out.println("regViewModel: add-user request passed");
        model.username = nicknameProperty.getValue();
        model.password = passwordProperty.getValue();
        model.viewAction.setValue(ViewActions.regBtnClicked);
    }

    public void switchToLogin() {
        System.out.println("regViewModel: switch-to-login-window request passed");
        model.viewAction.setValue(ViewActions.switchToLoginBtnClicked);
    }
}
