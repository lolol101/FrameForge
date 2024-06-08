package frameforge.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import frameforge.model.LoginModel;
import frameforge.model.MainPageModel;
import frameforge.model.PostCreationModel;
import frameforge.model.RegistrationModel;
import frameforge.serializable.JsonSerializable;
import javafx.util.Pair;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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
                    break;
                case reachedNextPostBox:
                    getMainPost();
                    break;
                case likeOrDislikePost:
                    String id = mainPageModel.likedPost;
                    setReactionOnPost(id);
                    break;
            }
            mainPageModel.viewAction.setValue(MainPageModel.ViewActions.zero);
        });

        postCreationModel.viewAction.addListener((obs, oldCommand, newCommand) -> {
            switch (newCommand) {
                case sendRequestCreatePost:
                    makePost();
                    postCreationModel.clientCommand.setValue(PostCreationModel.ClientCommands.close);
                    mainPageModel.clientCommand.setValue(MainPageModel.ClientCommands.show);
                    break;
                case sendRequestOpenMainPageMenu:
                    postCreationModel.clientCommand.setValue(PostCreationModel.ClientCommands.close);
                    mainPageModel.clientCommand.setValue(MainPageModel.ClientCommands.show);
            }
            postCreationModel.viewAction.setValue(PostCreationModel.ViewActions.zero);
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
                    json.put("reaction", MainPageModel.Post.REACTION.DISLIKE.toString());
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

    private String createHashString(String str) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        KeySpec spec = new PBEKeySpec(str.toCharArray(), salt, 65536, 128);
        String hash;
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hash = new String(factory.generateSecret(spec).getEncoded(), StandardCharsets.UTF_8);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return hash;
    }

    private void registration() {
        regModel.password = createHashString(regModel.password);
        if (regModel.password == null) {
            // TODO
        }
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
        loginModel.password = createHashString(loginModel.password);
        if (loginModel.password == null) {
            // TODO
        }
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
        json.put("username", loginModel.username);
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

        ArrayNode tagsNode = json.putArray("tags");
        ArrayNode extensionsNode = json.putArray("extensionOfImage");

        for (var tag : postCreationModel.chosenTags)
            tagsNode.add(tag);

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
            extensionsNode.add(p.getValue());
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

    private void setReactionOnPost(String id) {
        JsonSerializable data = new JsonSerializable();
        ObjectNode json = jsMapper.createObjectNode();
        json.put("username", loginModel.username);
        MainPageModel.Post.REACTION reaction = MainPageModel.Post.
                REACTION.valueOf(mainPageModel.currentPosts.get(id).json.get("reaction").textValue());
        json.put("type", ServerCommands.ACTIONS.SET_LIKE.toString());
        mainPageModel.currentPosts.get(id).json.put("reaction", (reaction ==
                MainPageModel.Post.REACTION.LIKE ? MainPageModel.Post.REACTION.DISLIKE.toString()
                : MainPageModel.Post.REACTION.LIKE.toString()));
        json.put("reaction",mainPageModel.currentPosts.get(id).json.get("reaction").textValue());
        json.put("id", mainPageModel.likedPost);
        data.setJson(json);
        socketManager.sendingData.add(data);
        socketManager.clientCommand.setValue(SocketManager.ClientCommands.sendData);
    }
}