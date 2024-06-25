package frameforge.view;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class Controller<Model, ViewModel> {
    public ViewModel viewModel;
    Stage stage;
    Scene scene;

    abstract void setModel(Model model);
    abstract void addListeners();
    abstract void removeListeners();
    public void passStageAndScene(Stage stage, Scene scene) {
        this.stage = stage;
        this.scene = scene;
    }
    public void openInView() throws IOException { // TODO: un-make it being public when done with testing
        System.out.println("From " + this.getClass() + " : " + viewModel.getClass());
        System.out.println("\topen-in-view request received");
        System.out.println("\tsetting scene" + scene.hashCode() + " to stage " + stage.hashCode());
        stage.setScene(scene);
        stage.show();
    }
    void hideInView() {
        System.out.println("From " + this.getClass() + " : " + viewModel.getClass());
        System.out.println("\tclose-in-view request received");
    }
}
