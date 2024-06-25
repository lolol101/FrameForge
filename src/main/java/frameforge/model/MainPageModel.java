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
import java.util.*;

public class MainPageModel {
    public Property<ClientCommands> clientCommand;
    public Property<ViewActions> viewAction;

    public HashMap<String, Post> currentPosts;
    public String currentPostId;
    public String likedPost;

    public static class Post {
        public ArrayList<byte[]> images;
        public ObjectNode json;

        public enum REACTION {
            LIKE, DISLIKE
        }

        public Post(ObjectNode json, ArrayList<byte[]> images) {
            this.json = json;
            this.images = images;
        }
    }

    public static class Person { // TODO: move Post & Person to appropriate places
        public Integer likeCount;
        public String username;
        public Person(String username, Integer likeCount) {
            this.likeCount = likeCount;
            this.username = username;
        }

        public Integer getLikeCount() {
            return likeCount;
        }
        public String getUsername() {
            return username;
        }
    }

    List<Person> leaderboardMembers;

    public enum ViewActions {
        reachedNextPostBox,
        returnToLoginBtnClicked,
        openPostCreationMenuBtnClicked,
        likeOrDislikePost,
        toggleLeaderboardRequest, zero
    }

    public enum ClientCommands {
        show,
        close,
        loadPost,
        deletePost,
        toggleLeaderBoard,
        zero
    }

    public MainPageModel() {
        currentPosts = new HashMap<>();
        viewAction = new SimpleObjectProperty<>();
        clientCommand = new SimpleObjectProperty<>();
        leaderboardMembers = new ArrayList<>();
        viewAction.setValue(ViewActions.zero);
        clientCommand.setValue(ClientCommands.zero);
    }

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

    public List<Person> getLeaderboardMembers() {
        // TODO: remove lines below after connecting to client
        leaderboardMembers.clear();
        leaderboardMembers.add(new Person("Test1", 100));
        leaderboardMembers.add(new Person("Test2", 120));
        leaderboardMembers.add(new Person("Test3", 1010));
        leaderboardMembers.add(new Person("Test4", 1300));
        leaderboardMembers.add(new Person("Test5", 1208));
        leaderboardMembers.add(new Person("Test6", 1014));
        leaderboardMembers.add(new Person("Test7", 1009));
        leaderboardMembers.add(new Person("Test8", 2203));
        leaderboardMembers.add(new Person("Test9", 11));
        leaderboardMembers.add(new Person("Test10", 57));
        leaderboardMembers.sort(Comparator.comparing(p -> p.likeCount)); // TODO: is there no better way?
        return leaderboardMembers;
    }
}
