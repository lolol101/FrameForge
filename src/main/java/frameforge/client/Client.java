package frameforge.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import frameforge.model.LoginModel;
import frameforge.model.MainPageModel;
import frameforge.model.RegistrationModel;

public class Client {
    public final LoginModel loginModel;
    public final RegistrationModel regModel;
    public final MainPageModel mainPageModel;
    public SocketManager socketManager;


    private final ObjectMapper jsMapper;

    public Client() {
        loginModel = new LoginModel();
        regModel = new RegistrationModel();
        mainPageModel = new MainPageModel();

        jsMapper = new ObjectMapper();
        socketManager = new SocketManager();
    }

    public void connectListeners() {
        regModel.viewAction.addListener((obs, oldCommand, newCommand) -> {
            switch (newCommand) {
                case regBtnClicked -> {
                    registration();
                }
                case switchToLoginBtnClicked -> {
                    regModel.clientCommand.setValue(RegistrationModel.ClientCommands.close);
                    loginModel.clientCommand.setValue(LoginModel.ClientCommands.show);
                }
            }
            regModel.viewAction.setValue(RegistrationModel.ViewActions.zero);
        });

        loginModel.viewAction.addListener((obs, oldCommand, newCommand) -> {
            switch (newCommand) {
                case authBtnClicked -> {
                    authorization();
                }
                case switchToRegistrationBtnClicked -> {
                    System.out.println("client: switch-to-login-menu request processing");
                    loginModel.clientCommand.setValue(LoginModel.ClientCommands.close);
                    regModel.clientCommand.setValue(RegistrationModel.ClientCommands.show);
                }
            }
            loginModel.viewAction.setValue(LoginModel.ViewActions.zero);
        });

        socketManager.clientCommand.addListener((obs, oldCommand, newCommand) -> {
            if (newCommand == SocketManager.ClientCommands.sendJson)
                socketManager.sendJson();
        });

        socketManager.socketAction.addListener((obs, oldCommand, newCommand) -> {
            if (newCommand == SocketManager.SocketActions.acceptJson)
                handleRequest();
        });

        mainPageModel.viewAction.addListener((obs, oldCommand, newCommand) -> {
            switch (newCommand) {
                case returnToLoginBtnClicked -> {
                    mainPageModel.clientCommand.setValue(MainPageModel.ClientCommands.close);
                    loginModel.clientCommand.setValue(LoginModel.ClientCommands.show);
                }
                case reachedNextPostBox -> {
                    // TODO: get images from server or locally
                }
            }
            mainPageModel.viewAction.setValue(MainPageModel.ViewActions.zero);
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
                    // regModel.clientCommand.setValue(RegistrationModel.ClientCommands.close);
                    mainPageModel.clientCommand.setValue(MainPageModel.ClientCommands.show);
                }
                else if (status == ServerCommands.STATUS.USERNAME_EXIST) {
                    // TODO
                }
                break;
            case ServerCommands.RESPONSE_TYPE.AUTHORIZATION_BACK:
                break;
        }
    }

    private void registration() {
        ObjectNode json = jsMapper.createObjectNode();
        json.put("username", regModel.username);
        json.put("password", regModel.password);
        json.put("type", ServerCommands.ACTIONS.REGISTRATION.toString());
        socketManager.sendingData.add(json);
        socketManager.clientCommand.setValue(SocketManager.ClientCommands.sendJson);
        regModel.clientCommand.setValue(RegistrationModel.ClientCommands.zero);
    }

    private void authorization() {
        ObjectNode json = jsMapper.createObjectNode();
        json.put("username", regModel.username);
        json.put("password", regModel.password);
        json.put("type", ServerCommands.ACTIONS.AUTHORIZATION.toString());
        socketManager.sendingData.add(json);
        socketManager.clientCommand.setValue(SocketManager.ClientCommands.sendJson);
        regModel.clientCommand.setValue(RegistrationModel.ClientCommands.zero);
    }
}