package frameforge.model;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

public class LoginModel {
    public Property<ClientCommands> clientCommand;
    public Property<ViewActions> viewAction;

    public String username;
    public String password;

    public enum ViewActions {
        authBtnClicked,
        switchToRegistrationBtnClicked,
        zero
    }

    public enum ClientCommands {
        open,
        close,
        zero
    }

    public LoginModel() {
        username = "";
        password = "";
        clientCommand = new SimpleObjectProperty<>();
        viewAction = new SimpleObjectProperty<>();
    }
}
