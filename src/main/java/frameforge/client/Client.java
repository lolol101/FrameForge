package frameforge.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import frameforge.model.LoginModel;
import frameforge.model.RegistrationModel;

public class Client {
    private final String ip = "188.225.82.247";
    private final int port = 8080;
    private final LoginModel loginModel;
    public final RegistrationModel regModel;
    private SocketManager socketManager;

    private final ObjectMapper jsMapper;

    public Client() {
        loginModel = new LoginModel();
        regModel = new RegistrationModel();
        jsMapper = new ObjectMapper();
        socketManager = new SocketManager();
        socketManager.connect(ip, port);
    }

    public void connectModels() {
        regModel.command.addListener((obs, oldCommand, newCommand) -> {
            if (newCommand == RegistrationModel.Commands.regBtnClicked) registration();
        });
    }

    // Listeners:
    public void registration() {
        ObjectNode json = jsMapper.createObjectNode();
        json.put("username", regModel.username);
        json.put("password", regModel.password);
        json.put("type", ServerCommands.ACTIONS.REGISTRATION.toString());
        socketManager.sendJson(json);
        // net working
    }

    public void authorization() {
        // net working
    }
}