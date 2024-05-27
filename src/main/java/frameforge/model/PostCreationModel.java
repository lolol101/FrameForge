package frameforge.model;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import javax.swing.text.View;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PostCreationModel {
    public Property<ClientCommands> clientCommand;
    public Property<ViewActions> viewAction;

    public String postDescription;
    public List<File> attachedFiles;
    public enum ViewActions {
        sendRequestCreatePost,
        sendRequestOpenMainPageMenu,
        zero
    }

    public enum ClientCommands { // TODO: add reset
        show,
        close,
        zero
    }

    public PostCreationModel() {
        attachedFiles = new ArrayList<>();
        viewAction = new SimpleObjectProperty<>();
        clientCommand = new SimpleObjectProperty<>();
    }
}
