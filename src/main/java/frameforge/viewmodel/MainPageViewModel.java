package frameforge.viewmodel;

import frameforge.model.MainPageModel;
import frameforge.model.RegistrationModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;

public class MainPageViewModel {
    MainPageModel model;

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
