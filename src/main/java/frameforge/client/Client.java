package frameforge.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import frameforge.serializable.JsonSerializable;
import frameforge.model.PostCreationModel;
import frameforge.model.RegistrationModel;
import frameforge.model.MainPageModel;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import frameforge.model.LoginModel;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Objects;
import javafx.util.Pair;
import java.io.File;

public class Client {
    public PostCreationModel postCreationModel;
    public final MainPageModel mainPageModel;
    public final RegistrationModel regModel;
    public final LoginModel loginModel;
    public SocketManager socketManager;

    private final ObjectMapper jsMapper;

    public Client() {
        loginModel = new LoginModel();
        regModel = new RegistrationModel();
        mainPageModel = new MainPageModel();
        jsMapper = new ObjectMapper();
        socketManager = new SocketManager();
        postCreationModel = new PostCreationModel();
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
            if (newCommand == SocketManager.ClientCommands.sendData)
                socketManager.pool.execute(socketManager::sendData);
            socketManager.clientCommand.setValue(SocketManager.ClientCommands.zero);
        });

        socketManager.socketAction.addListener((obs, oldCommand, newCommand) -> {
            if (newCommand == SocketManager.SocketActions.acceptData)
                handleRequest();
            socketManager.socketAction.setValue(SocketManager.SocketActions.zero);
         });

        mainPageModel.viewAction.addListener((obs, oldCommand, newCommand) -> {
            switch (newCommand) {
                case returnToLoginBtnClicked:
                    mainPageModel.clientCommand.setValue(MainPageModel.ClientCommands.close);
                    loginModel.clientCommand.setValue(LoginModel.ClientCommands.show);
                    break;
                case openPostCreationMenuBtnClicked:
                    mainPageModel.clientCommand.setValue(MainPageModel.ClientCommands.close);
                    postCreationModel.clientCommand.setValue(PostCreationModel.ClientCommands.show);
                case reachedNextPostBox:
                    getMainPost();
                    break;
            }
            mainPageModel.viewAction.setValue(MainPageModel.ViewActions.zero);
        });

        postCreationModel.viewAction.addListener((obs, oldCommand, newCommand) -> {
            switch (newCommand) {
                case sendRequestCreatePost:
                    makePost();
                    mainPageModel.clientCommand.setValue(MainPageModel.ClientCommands.show);
                    break;
                case sendRequestOpenMainPageMenu:
                    postCreationModel.clientCommand.setValue(PostCreationModel.ClientCommands.close);
                    mainPageModel.clientCommand.setValue(MainPageModel.ClientCommands.show);
            }
            postCreationModel.clientCommand.setValue(PostCreationModel.ClientCommands.zero);
        });
    }

    // Listeners:
    public void handleRequest() {
        JsonSerializable data = (JsonSerializable) socketManager.acceptedData.remove();
        ObjectNode json = data.getJson();
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
                if (status == ServerCommands.STATUS.OK) {
                    json.remove("status");
                    json.remove("type");
                    ArrayList<byte[]> images = new ArrayList<>(data.getImages());
                    String id = json.get("id").textValue();
                    mainPageModel.currentPosts.put(id, new MainPageModel.Post(json, images));
                    mainPageModel.currentPostId = id;
                    mainPageModel.clientCommand.setValue(MainPageModel.ClientCommands.loadPost);
                }
                else if (status == ServerCommands.STATUS.ERROR)
                    System.out.println("Getting main post error");
                break;
            case SET_MAIN_POST_BACK:
                if (status == ServerCommands.STATUS.OK) {
                    mainPageModel.viewAction.setValue(MainPageModel.ViewActions.zero);
                    // TODO
                } else if (status == ServerCommands.STATUS.ERROR) {
                    // TODO
                }
                break;
        }
    }

    private void registration() {
        ObjectNode json = jsMapper.createObjectNode();
        json.put("username", regModel.username);
        json.put("password", regModel.password);
        json.put("type", ServerCommands.ACTIONS.REGISTRATION.toString());
        JsonSerializable data = new JsonSerializable();
        data.setJson(json);
        socketManager.sendingData.add(data);
        socketManager.clientCommand.setValue(SocketManager.ClientCommands.sendData);
    }

    private void authorization() {
        ObjectNode json = jsMapper.createObjectNode();
        json.put("username", loginModel.username);
        json.put("password", loginModel.password);
        json.put("type", ServerCommands.ACTIONS.AUTHORIZATION.toString());
        JsonSerializable data = new JsonSerializable();
        data.setJson(json);
        socketManager.sendingData.add(data);
        socketManager.clientCommand.setValue(SocketManager.ClientCommands.sendData);
    }

    private void getMainPost() {
        ObjectNode json = jsMapper.createObjectNode();
        json.put("type", ServerCommands.ACTIONS.GET_MAIN_POST.toString());
        json.put("typeImage", ServerCommands.ImgType.FULL.toString());
        JsonSerializable data = new JsonSerializable();
        data.setJson(json);
        socketManager.sendingData.add(data);
        socketManager.clientCommand.setValue(SocketManager.ClientCommands.sendData);
    }

    private void makePost() {
        JsonSerializable data = new JsonSerializable();
        ObjectNode json = jsMapper.createObjectNode();
        json.put("username", loginModel.username);
        json.put("type", ServerCommands.ACTIONS.SET_MAIN_POST.toString());

        ArrayNode arrNode = json.putArray("extensionOfImage");

        ArrayList<byte[]> byteImages = new ArrayList<>(postCreationModel.attachedFiles.stream().map((File file) -> {
            try {
                String extension = file.getName().
                        substring(file.getName().indexOf(".") + 1);
                return new Pair<>(ImageIO.read(file), extension);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }
        }).filter(Objects::nonNull).map((Pair<BufferedImage, String> p) -> {
            arrNode.add(p.getValue());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(p.getKey(), p.getValue(), baos);
                return baos.toByteArray();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }
        }).toList());

        data.setManyPhotos(byteImages);
        data.setJson(json);
        socketManager.sendingData.add(data);
        socketManager.clientCommand.setValue(SocketManager.ClientCommands.sendData);
    }
}