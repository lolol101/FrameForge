package frameforge.model;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

public class LoginModel {
    public Property<ClientCommands> clientCommand;
    public Property<ViewActions> viewAction;


    // Data:
    public String username;
    public String password;

    public enum ViewActions {
        // VM commands:
        authBtnClicked,
        switchToRegistrationBtnClicked,
        zero
    }

    public enum ClientCommands {
        show,
        close,
        zero
    }

    public LoginModel() {
        viewAction = new SimpleObjectProperty<>();
        clientCommand = new SimpleObjectProperty<>();
        viewAction.setValue(ViewActions.zero);
        clientCommand.setValue(ClientCommands.zero);
    }
}
