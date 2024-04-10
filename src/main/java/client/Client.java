package client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ru.hse.frameforge.model.LoginModel;
import com.ru.hse.frameforge.model.RegistrationModel;

public class Client {
    private final LoginModel loginModel;
    public final RegistrationModel regModel;

    public Client() {
        loginModel = new LoginModel();
        regModel = new RegistrationModel();
    }

    public void connectModels() {
        regModel.command.addListener((obs, oldCommand, newCommand) -> {
            if (newCommand == RegistrationModel.Commands.regBtnClicked) registration();
        });
    }

    // Listeners:
    public void registration() {
        // net working
    }

    public void authorization() {
        // net working
    }
}