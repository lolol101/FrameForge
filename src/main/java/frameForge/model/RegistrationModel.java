package frameForge.model;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
//import com.github.msteinbeck.sig4j.signal.Signal1;

public class RegistrationModel {
    public Property<Commands> command;

    // Data:
    public String username;
    public String password;

    public enum Commands {
        // Client commands:
        show,
        close,

        // VM commands:
        regBtnClicked
    }

    public RegistrationModel() {
        username = "";
        password = "";
        command = new SimpleObjectProperty<>();
    }
}
