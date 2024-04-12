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
        // VM commands:
        regBtnClicked, // TODO: rename to action, not event - events stay in *Controller classes
        switchToLoginBtnClicked,
        zero
    }

    public enum ClientCommands {
        show,
        close,
        zero
    }

    public RegistrationModel() {
        username = ""; // TODO: is needed?
        password = "";
        viewAction = new SimpleObjectProperty<>();
        clientCommand = new SimpleObjectProperty<>();
    }
}
