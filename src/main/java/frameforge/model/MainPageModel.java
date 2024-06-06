package frameforge.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainPageModel {
    // TODO: command & action queues
    public Property<MainPageModel.ClientCommands> clientCommand;
    public Property<MainPageModel.ViewActions> viewAction;

    public HashMap<String, Post> currentPosts;
    public String currentPostId;
    public String likedPost;

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
        likeOrDislikePost,
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
    public Pair<String, List<Image>> getLastLoadedPostData() throws NullPointerException {
        List<Image> images = new ArrayList<>();
        ArrayList<byte[]> imageData = currentPosts.get(currentPostId).images;
        for (var imageBytes : imageData) {
            try {
                images.add(SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(imageBytes)), null));
            } catch (IOException e) {
                System.out.println("bytes -> image Main Page Model exception");
            }
        }
        return new Pair<>(currentPostId, images);
    }


}
