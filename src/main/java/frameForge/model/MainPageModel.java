package frameForge.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.beans.property.Property;

import java.io.File;
import java.util.HashMap;

public class MainPageModel {
    public Property<Commands> command;
    public HashMap<Integer, ObjectNode> currentPosts;
    private int currentPostId;

    public File getNextPost() {
        // TODO: GUI method
        ++currentPostId;
        return null;
    }

    public enum Commands {
        // Client commands:
        show,
        close,
        loadPost,
        deletePost,

        // VM commands:
        reachedNextPostBox
    }

}
