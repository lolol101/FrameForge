package frameforge.viewmodel;

import frameforge.model.MainPageModel;
import javafx.scene.image.Image;
import javafx.util.Pair;

import java.io.File;

public class MainPageViewModel {
    private MainPageModel model;

    public MainPageViewModel(MainPageModel model) {
        this.model = model;
    }

    public void setModel(MainPageModel model) {
        this.model = model;
    }

    public MainPageModel getModel() {
        return model;
    } // TODO: solve getter&setter abundance

    public Pair<String, Image> getNextImage() throws NullPointerException {
        return model.getLastLoadedImage();
    }

    public void uploadFile(File file) {
        model.fileToUpload = file;
        model.viewAction.setValue(MainPageModel.ViewActions.uploadNewFile);
        System.out.println("mainPageViewModel: sending signal to upload a file " + model.fileToUpload.getName());
    }

    public void quit() {
        model.viewAction.setValue(MainPageModel.ViewActions.returnToLoginBtnClicked);
    }
}
