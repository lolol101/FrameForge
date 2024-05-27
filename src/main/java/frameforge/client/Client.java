package frameforge.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import frameforge.model.LoginModel;
import frameforge.model.MainPageModel;
import frameforge.model.RegistrationModel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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
            if (newCommand == RegistrationModel.ViewActions.regBtnClicked)
                registration();
            else if (newCommand == RegistrationModel.ViewActions.switchToLoginBtnClicked) {
                regModel.clientCommand.setValue(RegistrationModel.ClientCommands.close);
                loginModel.clientCommand.setValue(LoginModel.ClientCommands.show);
            }
            regModel.viewAction.setValue(RegistrationModel.ViewActions.zero);
        });

        loginModel.viewAction.addListener((obs, oldCommand, newCommand) -> {
            if (newCommand == LoginModel.ViewActions.authBtnClicked)
                authorization();
            else if (newCommand == LoginModel.ViewActions.switchToRegistrationBtnClicked) {
                    System.out.println("client: switch-to-login-menu request processing");
                    loginModel.clientCommand.setValue(LoginModel.ClientCommands.close);
                    regModel.clientCommand.setValue(RegistrationModel.ClientCommands.show);
            }
            loginModel.viewAction.setValue(LoginModel.ViewActions.zero);
        });

        socketManager.clientCommand.addListener((obs, oldCommand, newCommand) -> {
            if (newCommand == SocketManager.ClientCommands.sendJson)
                socketManager.sendJson();
            socketManager.clientCommand.setValue(SocketManager.ClientCommands.zero);
        });

        socketManager.socketAction.addListener((obs, oldCommand, newCommand) -> {
            if (newCommand == SocketManager.SocketActions.acceptJson)
                handleRequest();
            socketManager.socketAction.setValue(SocketManager.SocketActions.zero);
        });

        mainPageModel.viewAction.addListener((obs, oldCommand, newCommand) -> {
            switch (newCommand) {
                case returnToLoginBtnClicked:
                    mainPageModel.clientCommand.setValue(MainPageModel.ClientCommands.close);
                    loginModel.clientCommand.setValue(LoginModel.ClientCommands.show);
                    break;

                case reachedNextPostBox:
                    System.out.println("restart");
                    getMainPost();
                    break;
            }
            mainPageModel.viewAction.setValue(MainPageModel.ViewActions.zero);
        });
    }

    // Listeners:
    public void handleRequest() {
        ObjectNode json = socketManager.acceptedData.remove();
        ServerCommands.STATUS status = ServerCommands.STATUS.valueOf(json.get("status").textValue());
        ServerCommands.RESPONSE_TYPE type = ServerCommands.RESPONSE_TYPE.valueOf(json.get("type").textValue());
        switch (type) {
            case REGISTER_BACK:
                if (status == ServerCommands.STATUS.OK)
                    mainPageModel.clientCommand.setValue(MainPageModel.ClientCommands.show);
                else if (status == ServerCommands.STATUS.USERNAME_EXIST) {
                    // TODO
                }
                break;
            case AUTHORIZATION_BACK:
                if (status == ServerCommands.STATUS.OK)
                    mainPageModel.clientCommand.setValue(MainPageModel.ClientCommands.show);
                break;
            case GET_MAIN_POST_BACK:
                try {
                    if (status == ServerCommands.STATUS.OK) {
                        StringBuilder rawData = new StringBuilder();
                        rawData.append(json.get("arrayPhotos").textValue());
                        rawData.deleteCharAt(0);
                        rawData.deleteCharAt(rawData.length() - 1);
                        ArrayList<String> images = new ArrayList<>(List.of(rawData.toString().split(",")));
                        rawData = new StringBuilder(json.get("extensions").textValue());
                        rawData.deleteCharAt(0);
                        rawData.deleteCharAt(rawData.length() - 1);
                        ArrayList<String> extensions = new ArrayList<>(List.of(rawData.toString().split(",")));
                        String id = json.get("_id").toString();
                        for (int i = 0; i < images.size(); ++i) {
                            byte[] decodedBytes = Base64.getDecoder().decode(images.get(i));
                            BufferedImage bufImg = ImageIO.read(new ByteArrayInputStream(decodedBytes));
                            ImageHandler imageHandler = new ImageHandler(bufImg, ImageHandler.ImgType.valueOf(json.get("imageType").textValue()), extensions.get(i));
                            mainPageModel.currentPosts.put(id, new MainPageModel.Post(json, imageHandler));
                        }
                        mainPageModel.currentPostId = id;
                        mainPageModel.clientCommand.setValue(MainPageModel.ClientCommands.loadPost);
                    }
                    else if (status == ServerCommands.STATUS.ERROR)
                        System.out.println("Getting main post error");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
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
    }

    private void authorization() {
        ObjectNode json = jsMapper.createObjectNode();
        json.put("username", loginModel.username);
        json.put("password", loginModel.password);
        json.put("type", ServerCommands.ACTIONS.AUTHORIZATION.toString());
        socketManager.sendingData.add(json);
        socketManager.clientCommand.setValue(SocketManager.ClientCommands.sendJson);
    }

    private void getMainPost() {
        ObjectNode json = jsMapper.createObjectNode();
        json.put("type", ServerCommands.ACTIONS.GET_MAIN_POST.toString());
        json.put("typeImage", ImageHandler.ImgType.SCALED.toString());
        socketManager.sendingData.add(json);
        socketManager.clientCommand.setValue(SocketManager.ClientCommands.sendJson);
    }
}