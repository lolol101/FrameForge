package frameforge.viewmodel;

import frameforge.model.PostCreationModel;
import frameforge.model.RegistrationModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.FileChooser;

import java.io.File;

import static frameforge.model.PostCreationModel.ViewActions.sendRequestCreatePost;
import static frameforge.model.PostCreationModel.ViewActions.sendRequestOpenMainPageMenu;

public class PostCreationViewModel {
    private PostCreationModel model; // TODO: is it OK if files are passed directly through controller method through viewModel to model, and aren't stored in model?
    public StringProperty postDescriptionProperty; // TODO:
    // TODO: move FileChooser from Controller here. Or not?

    public PostCreationViewModel(PostCreationModel model) {
        this.model = model;
        postDescriptionProperty = new SimpleStringProperty();
    }

    public void setModel(PostCreationModel model) {
        this.model = model;
    }

    public PostCreationModel getModel() {
        return model;
    } // TODO: solve getter&setter abundance

    public void addImageToPost() {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            model.attachedFiles.add(file);
            // TODO: add list of selected files or something
        }
    }
    // TODO: add removeImage method
    // TODO: file arrayList cleanup/reset when switching to main menu
    public void createPost() {
        // TODO: file counter check goes here: no empty posts!
        if (!model.attachedFiles.isEmpty()) {
            System.out.println("postCreationViewModel: createPost request passed");
            model.postDescription = postDescriptionProperty.getValue();
            model.viewAction.setValue(sendRequestCreatePost);
        } else {
            System.out.println("postCreationViewModel: attempting to create post with no attached files");
        }
    }

    public void SwitchToMainPage() {
        model.viewAction.setValue(sendRequestOpenMainPageMenu);
    }
}
