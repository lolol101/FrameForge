package frameforge.view;

import frameforge.model.PostCreationModel;
import frameforge.viewmodel.PostCreationException;
import frameforge.viewmodel.PostCreationViewModel;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import static frameforge.model.PostCreationModel.ClientCommands;

public class PostCreationController {
    private Stage stage; // single stage instance shared with some other menus
    private Scene scene; // unique scene used to avoid repeated loading of the same menu

    @FXML private VBox uploadedFilesVBox;
    @FXML private VBox tagsVBox;
//    @FXML private TextField tagEntryTextField; // TODO: switch type
    @FXML private TilePane chosenTagsTilePane;

    private ArrayList<String> chosenFileNames;

    @FXML private TextArea postDescription;
    @FXML private Button addAnotherImageBtn;

    @FXML private Button createPostBtn;

    @FXML private Button switchToMainMenuBtn;

    @FXML private TextField suggestionsTextField;

    private PostCreationViewModel viewModel;

    // TODO: standardise UI elements naming
    // TODO: clean up member names
    // TODO: static logger method to interface

    private final ChangeListener<ClientCommands> clientCommandReceiver = (obs, oldCommand, newCommand) -> {
        System.out.println("postCreationView: changeListener fired on client command reception: oldCommand=" + oldCommand + ", newCommand=" + newCommand);
        switch (newCommand) {
            case show -> {
                try {
                    openInView();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case close -> hideInView();
            // TODO: case reset
        }
        viewModel.getModel().clientCommand.setValue(PostCreationModel.ClientCommands.zero);
    };

    public PostCreationController() {
        PostCreationModel model = new PostCreationModel();
        viewModel = new PostCreationViewModel(model);
    }

    public void setModel(PostCreationModel model) {
        removeListeners();
        viewModel = new PostCreationViewModel(model);
        addListeners();
        System.out.println("postCreationView: post creation model set");
    }

    private void removeListeners() {
        postDescription.textProperty().unbindBidirectional(viewModel.postDescriptionProperty);
        viewModel.getModel().clientCommand.removeListener(clientCommandReceiver);
        System.out.println("postCreationView: post creation listeners removed");
    }

    private void addListeners() {
        postDescription.textProperty().bindBidirectional(viewModel.postDescriptionProperty);
        viewModel.getModel().clientCommand.addListener(clientCommandReceiver);
        System.out.println("postCreationView: post creation listeners added");
    }

    @FXML public void initialize() {
        addListeners();
        System.out.println(this.getClass() + " attempting to create autoCompleteEntryChooser with list of suggestions: " + this.viewModel.getModel().allowedTags);
        new AutoCompleteEntryChooser(tagsVBox, suggestionsTextField,
                this.viewModel.getModel().allowedTags, this::sendRequestAddTag);
        // TODO: where to put? What to do with it? I don't need to address an object of this class

    }

    public void passStageAndScene(Stage stage, Scene scene) {
        this.stage = stage;
        this.scene = scene;
        System.out.println("regView: this.scene=" + scene.hashCode() + "; this.stage=" + stage.hashCode());
    }

    @FXML private void onBtnAddFile() {
        // TODO: prevent multiple button clicks
        System.out.println("postCreationView: add file button pressed");
        sendRequestAddFile();
    }

    @FXML private void onBtnCreatePost() {
        // TODO: prevent multiple button clicks
        System.out.println("postCreationView: create post button pressed");
        sendRequestCreatePost();
    }

    @FXML private void onBtnSwitchToMain() {
        // TODO: prevent multiple button clicks
        System.out.println("postCreationView: switch-to-main button pressed");
        sendSwitchToMainRequest();
    }

    private void sendRequestAddFile() {
        // TODO: make it async
        try {
            File addedFile = viewModel.addImageToPost(); // TODO wait here?

            if (addedFile != null){
                Label fileName = new Label(addedFile.getName());
                fileName.getStyleClass().add("rounded-label");

                Button removeFileBtn = new Button("X");
                removeFileBtn.setFocusTraversable(false);
                removeFileBtn.getStyleClass().add("symbol-button");

                HBox fileBox = new HBox(fileName, removeFileBtn);
                fileBox.getStyleClass().add("rounded-hbox");
                fileBox.setPadding(new Insets(5));

                removeFileBtn.setOnAction(event -> {
                    sendRequestRemoveFile(addedFile);
                    uploadedFilesVBox.getChildren().remove(fileBox);
                });
                // TODO: button style

                uploadedFilesVBox.getChildren().add(fileBox);
            }
        } catch (PostCreationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendRequestRemoveFile(File file) {
        try {
            viewModel.removeFile(file);
        } catch (PostCreationException e) {
            System.out.println(e.getMessage());
        }
    }

    // TODO: functions like that shouldn't be part of view! Make chosen tags & file list 4 bidirectionally bounded lists in view & viewmodel
    private void sendRequestAddTag(String tag) {
        if (tag != null){
            viewModel.addTag(tag); // TODO: change, remove, update, this shouldn't be here

            Label tagName = new Label(tag);
            tagName.getStyleClass().add("rounded-label");

            Button removeTagBtn = new Button("X");
            removeTagBtn.setFocusTraversable(false);
            removeTagBtn.getStyleClass().add("symbol-button");

            HBox tagBox = new HBox(tagName, removeTagBtn);
            tagBox.getStyleClass().add("rounded-hbox");
            tagBox.setPadding(new Insets(5));

            removeTagBtn.setOnAction(event -> {
                sendRequestRemoveTag(tag);
                chosenTagsTilePane.getChildren().remove(tagBox);
                System.out.println(viewModel.getModel().chosenTags); // TODO: remove line
            });
            chosenTagsTilePane.getChildren().add(tagBox);
        }
    }

    private void sendRequestRemoveTag(String tag) {
        viewModel.removeTag(tag);
    }
    private void sendRequestCreatePost() {
        viewModel.createPost();
    }

    private void sendSwitchToMainRequest() {
        viewModel.SwitchToMainPage();
    }

    public void openInView() throws IOException {
        System.out.println("postCreationView: open-in-view request received");
        System.out.println("postCreationView: setting scene" + scene.hashCode() + " to stage " + stage.hashCode());
        stage.setScene(scene);
        stage.show();
    }

    public void hideInView() {
        System.out.println("postCreationView: close-in-view request received");
        viewModel.reset();
        resetUI();

    }

    private void resetUI() {
        uploadedFilesVBox.getChildren().clear();
        chosenTagsTilePane.getChildren().clear();
    }
}
