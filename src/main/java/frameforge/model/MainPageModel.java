package frameforge.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public Pair<String, List<Image>> getLastLoadedPostData() throws NullPointerException {
        List<Image> images = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            images.add(
//                    new Image(Objects.requireNonNull(
//                            getClass()
//                            .getResource("pic_" + ++testPicNum + ".jpg"))
//                            .toString()
//                    ));
//        }
        ArrayList<byte[]> imageDatas = currentPosts.get(currentPostId).images;
        for (var imageBytes : imageDatas) {
            // TODO: add byte[] -> Image conversion here
        }
        return new Pair<>(currentPostId, images);
//        return new Pair<>(currentPostId, SwingFXUtils.toFXImage(currentPosts.get(currentPostId).imageHandler.img, null));
    } // returns a pair of post ID in string form and array of images to load; subsequent operations with post use passed ID as key


}
