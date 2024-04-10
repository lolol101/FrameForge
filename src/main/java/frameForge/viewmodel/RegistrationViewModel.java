package frameForge.viewmodel;

import frameForge.model.RegistrationModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class RegistrationViewModel {
    // ViewModel class for view-gui.model interactions; contains gui.model as a member; is a member of Controller class which
    public RegistrationModel model;
    public BooleanProperty regBtnPressed;
    public StringProperty nicknameProperty;
    public StringProperty passwordProperty;

    public RegistrationViewModel(RegistrationModel model) {
        this.model = model;
        nicknameProperty = new SimpleStringProperty();
        passwordProperty = new SimpleStringProperty();
        regBtnPressed = new SimpleBooleanProperty();
    }

    public void addUser() {
        model.username = nicknameProperty.getValue();
        model.password = passwordProperty.getValue();
        model.command.setValue(RegistrationModel.Commands.regBtnClicked);
    }
}
