package frameforge.viewmodel;

import frameforge.model.PostCreationModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.FileChooser;

import java.io.File;

import static frameforge.model.PostCreationModel.ViewActions.sendRequestCreatePost;
import static frameforge.model.PostCreationModel.ViewActions.sendRequestOpenMainPageMenu;

public class PostCreationViewModel extends ViewModel<PostCreationModel> {
    public StringProperty postDescriptionProperty; // TODO:

    private final static int maxFileCountInSinglePost = 5;
    private final static int maxFileSizeInKilobytes = 5*1024;

    public PostCreationViewModel(PostCreationModel model) {
        this.model = model;
        postDescriptionProperty = new SimpleStringProperty();
    }

    public File addImageToPost() throws PostCreationException {
        // TODO: mention it doesn't need to be async: client handles async upload
        FileChooser fileChooser = new FileChooser();

//        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
//        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
//        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
        // TODO: why does Nikita have problems with these filters on his Linux setup?

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

    public void addTag(String tag) {
        model.chosenTags.add(tag);
    }

    public void removeTag(String tag) {
        model.chosenTags.remove(tag);
    }
    public void createPost() {
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
