package frameforge.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainPageModel {
    // TODO: command & action queues
    public Property<MainPageModel.ClientCommands> clientCommand;
    public Property<MainPageModel.ViewActions> viewAction;

    public HashMap<String, Post> currentPosts;
    public String currentPostId;

    public static class Post {
        public ArrayList<byte[]> images;
        public ObjectNode json;

        public Post(ObjectNode json, ArrayList<byte[]> images) {
            this.json = json;
            this.images = images;
        }
    }

    public enum ViewActions {
        reachedNextPostBox,
        returnToLoginBtnClicked,
        openPostCreationMenuBtnClicked,
        zero
    }

    public enum ClientCommands {
        show,
        close,
        loadPost,
        deletePost,
        zero
    }

    public MainPageModel() {
        currentPosts = new HashMap<>();
        viewAction = new SimpleObjectProperty<>();
        clientCommand = new SimpleObjectProperty<>();
        viewAction.setValue(ViewActions.zero);
        clientCommand.setValue(ClientCommands.zero);
    }

    int testPicNum = 0;
    public Pair<String, Image> getLastLoadedImage() throws NullPointerException {
        return new Pair<>(currentPostId, new Image(Objects.requireNonNull(getClass().getResource("pic_" + ++testPicNum + ".jpg")).toString()));
//        return new Pair<>(currentPostId, SwingFXUtils.toFXImage(currentPosts.get(currentPostId).imageHandler.img, null));
    }


}
