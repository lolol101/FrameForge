package frameforge.model;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import javax.swing.text.View;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostCreationModel {
    public Property<ClientCommands> clientCommand;
    public Property<ViewActions> viewAction;

    public String postDescription;
    public List<File> attachedFiles;

    public List<String> chosenTags;

    public static List<String> allowedTags;

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

        allowedTags = new ArrayList<>(Arrays.asList("Cars",
                "Nature",
                "Animals",
                "Abstract",
                "Music",
                "Art",
                "Technic",
                "Fantasy",
                "Aesthetics",
                "Clothes",
                "Anime",
                "People",
                "Realism",
                "Space",
                "Games",
                "Martial art",
                "Design",
                "Utopia",
                "Journey",
                "Animation",
                "Movie",
                "Relaxation",
                "Mood",
                "Geometry",
                "Sadness",
                "Joy",
                "Madness",
                "Other"
        ));
    }
}
