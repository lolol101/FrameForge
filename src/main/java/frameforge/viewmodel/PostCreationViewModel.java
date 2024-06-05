package frameforge.viewmodel;

import frameforge.model.PostCreationModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.FileChooser;

import java.io.File;

import static frameforge.model.PostCreationModel.ViewActions.sendRequestCreatePost;
import static frameforge.model.PostCreationModel.ViewActions.sendRequestOpenMainPageMenu;

public class PostCreationViewModel {
    private PostCreationModel model; // TODO: is it OK if files are passed directly through controller method through viewModel to model, and aren't stored in model?
    public StringProperty postDescriptionProperty; // TODO:

    static int maxFileCountInSinglePost = 5;
    static int maxFileSizeInKilobytes = 5*1024;
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

    public File addImageToPost() throws PostCreationException {
        // TODO: mention it doesn't need to be async: client handles async upload
        FileChooser fileChooser = new FileChooser();

//        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
//        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
//        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            if (model.attachedFiles.size() >= maxFileCountInSinglePost) {
                throw new PostCreationException("too many files added");
            } else if (file.length() / 1024 > maxFileSizeInKilobytes) {
                throw new PostCreationException("chosen file too big");
            } else {
                model.attachedFiles.add(file);
                return file;
            }
        }
        return null;
    }

    public void removeFile(File file) throws PostCreationException {
        if (model.attachedFiles.contains(file)) {
            model.attachedFiles.remove(file);
        } else {
            throw new PostCreationException("attempting to remove non-added file, please debug");
        }

    }
    public void createPost() {
        // TODO: add file counter check here: no empty posts!
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

    public void reset() {
        model.reset();
    }
}
