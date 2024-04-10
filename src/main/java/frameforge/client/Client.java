package frameforge.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import frameforge.model.LoginModel;
import frameforge.model.MainPageModel;
import frameforge.model.RegistrationModel;

public class Client {
    private final String ip = "188.225.82.247";
    private final int port = 8080;
    public final LoginModel loginModel;
    public final RegistrationModel regModel;
    public final MainPageModel mainPageModel;
    private SocketManager socketManager;

    private final ObjectMapper jsMapper;

    public Client() {
        loginModel = new LoginModel();
        regModel = new RegistrationModel();
        mainPageModel = new MainPageModel();

        jsMapper = new ObjectMapper();
        socketManager = new SocketManager();
        socketManager.connect(ip, port);
    }

    public void connectModels() {
        regModel.viewAction.addListener((obs, oldCommand, newCommand) -> {
            switch (newCommand) {
                case regBtnClicked -> {
                    System.out.println("client: reg-and-open-main-menu request processing");
                    closeRegistrationMenu();
                    openMainPageMenu();
                }
                case switchToLoginBtnClicked -> {
                    System.out.println("client: switch-to-login-menu request processing");
                    closeRegistrationMenu();
                    openLoginMenu();
                }
            }
            regModel.viewAction.setValue(RegistrationModel.ViewActions.zero);
        });
        loginModel.viewAction.addListener((obs, oldCommand, newCommand) -> {
            switch (newCommand) {
                case authBtnClicked -> {
                    System.out.println("client: log-in-and-open-main-menu request processing");
                    closeLoginMenu();
                    openMainPageMenu();
                }
                case switchToRegistrationBtnClicked -> {
                    System.out.println("client: switch-to-login-menu request processing");
                    closeLoginMenu();
                    openRegistrationMenu();
                }
            }
            loginModel.viewAction.setValue(LoginModel.ViewActions.zero);
        });
        mainPageModel.viewAction.addListener((obs, oldCommand, newCommand) -> {
            switch (newCommand) {
                case returnToLoginBtnClicked -> {
                    System.out.println("client: quit-from-main-to-login-menu request processing");
                    closeMainPageMenu();
                    openLoginMenu();
                }
                case reachedNextPostBox -> {
                    // TODO: get images from server or locally
                }
            }
            mainPageModel.viewAction.setValue(MainPageModel.ViewActions.zero);
        });
    }

    // Listeners:
    public void registration() {
        // TODO: rn this method fires on initialization - fix it
        System.out.println("client: reg request processing");
        ObjectNode json = jsMapper.createObjectNode();
        json.put("username", regModel.username);
        json.put("password", regModel.password);
        json.put("type", ServerCommands.ACTIONS.REGISTRATION.toString());
        socketManager.sendJson(json);
        // TODO: если вставить сюда открытие меню, то оно не происходит
        // net working
    }

    public void authorization() {
        System.out.println("client: auth request processing");
        // net working
    }

    public void closeLoginMenu() {
        System.out.println("client: close-login-menu-request sent");
        loginModel.clientCommand.setValue(LoginModel.ClientCommands.close);
    }

    public void openLoginMenu() {
        System.out.println("client: open-login-menu-request sent");
        loginModel.clientCommand.setValue(LoginModel.ClientCommands.show);
    }

    public void closeRegistrationMenu() {
        System.out.println("client: close-reg-menu-request sent");
        regModel.clientCommand.setValue(RegistrationModel.ClientCommands.close);
    }

    public void openRegistrationMenu() {
        System.out.println("client: open-reg-menu-request sent");
        regModel.clientCommand.setValue(RegistrationModel.ClientCommands.show);
    }

    public void closeMainPageMenu() {
        System.out.println("client: close-main-menu-request sent");
        mainPageModel.clientCommand.setValue(MainPageModel.ClientCommands.close);
    }

    public void openMainPageMenu() {
        System.out.println("client: open-main-menu-request sent to model " + mainPageModel.hashCode());
        mainPageModel.clientCommand.setValue(MainPageModel.ClientCommands.show);
    }
}