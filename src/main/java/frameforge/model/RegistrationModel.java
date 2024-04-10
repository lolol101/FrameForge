package frameforge.model;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

public class RegistrationModel {
    public Property<ClientCommands> clientCommand;
    public Property<ViewActions> viewAction;

    // Data:
    public String username;
    public String password;

    public enum ViewActions {
        regBtnClicked,
        switchToLoginBtnClicked,
        zero
    }

    public enum ClientCommands {
        show,
        close,
        zero
    }

    public RegistrationModel() {
        username = "";
        password = "";
        clientCommand = new SimpleObjectProperty<>();
        viewAction = new SimpleObjectProperty<>();
    }
}
