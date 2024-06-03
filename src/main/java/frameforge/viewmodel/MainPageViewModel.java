package frameforge.viewmodel;

import frameforge.model.MainPageModel;
import javafx.scene.image.Image;
import javafx.util.Pair;

import java.util.List;

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

    public Pair<String, List<Image>> getNextImage() throws NullPointerException {
        return model.getLastLoadedPostData();
    }

    public void quit() {
        System.out.println("mainPageViewModel: sending signal to open a login menu");
        model.viewAction.setValue(MainPageModel.ViewActions.returnToLoginBtnClicked);
    }
    public void openPostCreationMenu() {
        System.out.println("mainPageViewModel: sending signal to open a post creation menu");
        model.viewAction.setValue(MainPageModel.ViewActions.openPostCreationMenuBtnClicked);
    }
}
