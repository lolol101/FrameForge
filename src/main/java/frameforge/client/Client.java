package frameforge.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import frameforge.model.LoginModel;
import frameforge.model.MainPageModel;
import frameforge.model.RegistrationModel;
import frameforge.serializable.JsonSerializable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javafx.scene.image.Image;

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

                case reachedNextPostBox:
                    getMainPost();
                    break;
                case uploadNewFile:
                    makePost();
                    break;
            }
            mainPageModel.viewAction.setValue(MainPageModel.ViewActions.zero);
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
                    ArrayList<BufferedImage> images = new ArrayList<>(data.getImages().stream().map((byte[] b) -> {
                            try {
                                return ImageIO.read(new ByteArrayInputStream(b));
                            } catch (IOException e) {
                                System.out.println("GET_MAIN_POST_BACK exp");
                            }
                            return null;
                        }).toList());

                    String id = json.get("id").textValue();
                    mainPageModel.currentPosts.put(id, new MainPageModel.Post(json, images));
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
        json.put("typeImage", ImgType.SCALED.toString());
        JsonSerializable data = new JsonSerializable();
        data.setJson(json);
        socketManager.sendingData.add(data);
        socketManager.clientCommand.setValue(SocketManager.ClientCommands.sendData);
    }

    private void makePost() {
        JsonSerializable data = new JsonSerializable();
        ObjectNode json = jsMapper.createObjectNode();
        File imgFile = mainPageModel.fileToUpload.element();
        ArrayList<String> extensions = new ArrayList<>();
        ArrayNode arrNode = json.putArray("extensionOfImage");

        extensions.add(imgFile.getName().
                substring(imgFile.getName().indexOf(".") + 1));
        for (var item : extensions)
            arrNode.add(item);

        try {
            BufferedImage image = ImageIO.read(mainPageModel.fileToUpload.remove());
            ArrayList<byte[]> images = new ArrayList<>();
            images.add(((DataBufferByte)(image.getRaster().getDataBuffer())).getData());
            data.setManyPhotos(images);
        } catch (IOException e) {
            System.out.println("SET_MAIN_POST_BACK OI exp");
        }

        data.setJson(json);
        socketManager.sendingData.add(data);
        socketManager.clientCommand.setValue(SocketManager.ClientCommands.sendData);
    }
}