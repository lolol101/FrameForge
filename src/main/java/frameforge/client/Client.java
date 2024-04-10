package frameforge.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import frameforge.model.LoginModel;
import frameforge.model.RegistrationModel;

public class Client {

    private final LoginModel loginModel;
    public final RegistrationModel regModel;
    public SocketManager socketManager;


    private final ObjectMapper jsMapper;

    public Client() {
        loginModel = new LoginModel();
        regModel = new RegistrationModel();
        jsMapper = new ObjectMapper();
        socketManager = new SocketManager();
    }

    public void connectListeners() {
        regModel.viewAction.addListener((obs, oldCommand, newCommand) -> {
            if (newCommand == RegistrationModel.ViewActions.regBtnClicked)
                registration();
        });

        socketManager.clientCommand.addListener((obs, oldCommand, newCommand) -> {
            if (newCommand == SocketManager.ClientCommands.sendJson)
                socketManager.sendJson();
        });

        socketManager.socketAction.addListener((obs, oldCommand, newCommand) -> {
            if (newCommand == SocketManager.SocketActions.acceptJson)
                handleRequest();
        });

    }

    // Listeners:
    public void handleRequest() {
        ObjectNode json = socketManager.acceptedData.remove();
        ServerCommands.RESPONSE_TYPE type = ServerCommands.RESPONSE_TYPE.valueOf(json.get("type").textValue());
        ServerCommands.STATUS status = ServerCommands.STATUS.valueOf(json.get("status").textValue());
        switch (type) {
            case ServerCommands.RESPONSE_TYPE.REGISTER_BACK:
                if (status == ServerCommands.STATUS.OK) {
                    // TODO
                }
                else if (status == ServerCommands.STATUS.USERNAME_EXIST) {
                    // TODO
                }
                break;
            case ServerCommands.RESPONSE_TYPE.AUTHORIZATION_BACK:
                break;
        }
    }

    public void registration() {
        ObjectNode json = jsMapper.createObjectNode();
        json.put("username", regModel.username);
        json.put("password", regModel.password);
        json.put("type", ServerCommands.ACTIONS.REGISTRATION.toString());
        socketManager.sendingData.add(json);
        socketManager.clientCommand.setValue(SocketManager.ClientCommands.sendJson);
        regModel.clientCommand.setValue(RegistrationModel.ClientCommands.zero);
        // net working
    }

    public void authorization() {
        // net working
    }
}