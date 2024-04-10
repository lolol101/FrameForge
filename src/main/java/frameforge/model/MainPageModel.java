package frameforge.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;
import java.util.HashMap;

public class MainPageModel {
    public Property<MainPageModel.ClientCommands> clientCommand;
    public Property<MainPageModel.ViewActions> viewAction;
    public HashMap<Integer, ObjectNode> currentPosts;
    private int currentPostId;

    public File getNextPost() {
        // TODO: GUI method
        ++currentPostId;
        return null;
    }

    public enum ViewActions {
        // VM commands:
        reachedNextPostBox, // TODO: rename to action, not event - events stay in *Controller classes
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
}
