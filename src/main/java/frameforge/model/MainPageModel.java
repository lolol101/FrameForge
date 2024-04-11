package frameforge.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import frameforge.client.ImageHandler;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.util.HashMap;

public class MainPageModel {
    public Property<MainPageModel.ClientCommands> clientCommand;
    public Property<MainPageModel.ViewActions> viewAction;
    public HashMap<String, Post> currentPosts;
    public String currentPostId;

    public static class Post {
        public ImageHandler imageHandler;
        public ObjectNode json;

        public Post(ObjectNode json_, ImageHandler imageHandler_) {
            json = json_;
            imageHandler = imageHandler_;
        }
    }

    public enum ViewActions {
        reachedNextPostBox,
        returnToLoginBtnClicked,
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

    public Image getLastLoadedImage() throws NullPointerException {
        return SwingFXUtils.toFXImage(currentPosts.get(currentPostId).imageHandler.img, null);
    }
}
