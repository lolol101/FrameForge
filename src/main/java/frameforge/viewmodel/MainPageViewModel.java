package frameforge.viewmodel;

import frameforge.model.MainPageModel;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static frameforge.model.MainPageModel.ViewActions;

public class MainPageViewModel extends ViewModel<MainPageModel> {

    public MainPageViewModel(MainPageModel model) {
        this.model = model;
    }

    public Pair<String, List<Image>> getNextImage() throws NullPointerException {
        return model.getLastLoadedPostData();
    }

    public void quit() {
        System.out.println("mainPageViewModel: sending signal to open a login menu");
        model.viewAction.setValue(ViewActions.returnToLoginBtnClicked);
    }
    public void openPostCreationMenu() {
        System.out.println("mainPageViewModel: sending signal to open a post creation menu");
        model.viewAction.setValue(ViewActions.openPostCreationMenuBtnClicked);
    }

    public void like(String postID) {
        model.likedPost = postID;
        model.viewAction.setValue(ViewActions.likeOrDislikePost);
        System.out.println("mainPageViewModel: sending signal to like a picture");
    }

    public List<MainPageModel.Person> getLeaderboard() {
        return model.getLeaderboardMembers();
    }

    public void savePic(String postID, Integer picNum) {
        // TODO: decide which parts of code should be in View, ViewModel, Model
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(".png", "*.png"),
                new FileChooser.ExtensionFilter(".jpg", "*.jpg"),
                new FileChooser.ExtensionFilter(".jpeg", "*.jpeg")
        );

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                String fileExtension = Optional.of(file.getName())
                        .filter(f -> f.contains("."))
                        .map(f -> f.substring(file.getName().lastIndexOf(".") + 1)).orElse("");
                ImageIO.write(ImageIO.read(
                        new ByteArrayInputStream(model.currentPosts.get(postID).images.get(picNum))),
                        fileExtension,
                        file
                );
            } catch (IOException e) {
                System.err.println("Error saving image: " + e.getMessage());
            }
        }
    }

    public void toggleLeaderboard() {
        model.viewAction.setValue(ViewActions.toggleLeaderboardRequest);
    }
}
