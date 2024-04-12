package frameforge.viewmodel;

import frameforge.model.RegistrationModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class RegistrationViewModel {
    // ViewModel class for view-gui.model interactions; contains gui.model as a member; is a member of Controller class which
    private RegistrationModel model;
    public StringProperty nicknameProperty;
    public StringProperty passwordProperty;

    public RegistrationViewModel(RegistrationModel model) {
        this.model = model;
        nicknameProperty = new SimpleStringProperty();
        passwordProperty = new SimpleStringProperty();
    }

    public void setModel(RegistrationModel model) {
        this.model = model;
    }

    public RegistrationModel getModel() {
        return model;
    } // TODO: solve getter&setter abundance

    public void addUser() { // TODO: change to private?
        System.out.println("regViewModel: add-user request passed");
        model.username = nicknameProperty.getValue();
        model.password = passwordProperty.getValue();
        model.viewAction.setValue(RegistrationModel.ViewActions.regBtnClicked);
    }

    public void switchToLogin() {
        System.out.println("regViewModel: switch-to-login-window request passed");
        model.viewAction.setValue(RegistrationModel.ViewActions.switchToLoginBtnClicked);
    }
}
