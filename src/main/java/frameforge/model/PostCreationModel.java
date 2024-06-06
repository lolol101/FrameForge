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

    public List<String> chosenTags;

    public List<String> allowedTags; // TODO: arrange file & hierarchy placement with Nikita; static?

    public void reset() {
        postDescription = "";
        attachedFiles.clear();
        chosenTags.clear();
    }

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
        chosenTags = new ArrayList<>();

        allowedTags = new ArrayList<>();
        allowedTags.add("clen");
        allowedTags.add("tren");
        allowedTags.add("anavar");
        allowedTags.add("anabolics");
        allowedTags.add("androsterone");
    }
}
