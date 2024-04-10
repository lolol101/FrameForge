package frameForge.model;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

public class LoginModel {
    public Property<RegistrationModel.Commands> command;

    // Data:
    public String username;
    public String password;

    public enum Commands {
        // Client commands:
        show,
        close,

        // VM commands:
        authBtnClicked
    }

    public LoginModel() {
        username = "";
        password = "";
        command = new SimpleObjectProperty<>();
    }
}
