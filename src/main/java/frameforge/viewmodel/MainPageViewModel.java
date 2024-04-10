package frameforge.viewmodel;

import frameforge.model.MainPageModel;
import javafx.scene.image.Image;

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

    public Image getNextImage() {
        return new Image(getClass()
                .getResource("images/pic_" + 0 + ".jpg")
                .toExternalForm()
        );
    }

    public void quit() {
        model.viewAction.setValue(MainPageModel.ViewActions.returnToLoginBtnClicked);
    }
}
