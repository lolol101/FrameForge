package frameforge.view;

import frameforge.model.MainPageModel;
import frameforge.viewmodel.MainPageViewModel;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainPageController {
    private Stage stage; // single stage instance shared with some other menus
    private Scene scene; // unique scene used to avoid repeated loading of the same menu

    private MainPageViewModel viewModel;

    // TODO: preferred width & height corrections
    @FXML private MenuButton menuButton; // TODO: set text to nickname
    @FXML private MenuButton leaderboardButton;
    @FXML private TilePane tilePane;
    @FXML private ScrollPane scrollPane;
    private int loadedImageCount = 0;

    private final ChangeListener<MainPageModel.ClientCommands> clientCommandReceiver = (obs, oldCommand, newCommand) -> {
        System.out.println("mainPageView: changeListener fired on client command reception");
        switch (newCommand) {
            case show -> {
                try {
                    openInView();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case close -> hideInView();
        }
        viewModel.getModel().clientCommand.setValue(MainPageModel.ClientCommands.zero);
    };

    public MainPageController() {
        MainPageModel model = new MainPageModel();
        viewModel = new MainPageViewModel(model);
    }

    public void initialize() {
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    public void setModel(MainPageModel model) {
        removeListeners();
        viewModel = new MainPageViewModel(model);
        addListeners();
        System.out.println("mainPageView: mainPage model set to " + model.hashCode());
    }

    private void removeListeners() {
        viewModel.getModel().clientCommand.removeListener(clientCommandReceiver);
        System.out.println("mainPageView: listeners removed");
    }

    private void addListeners() {
        viewModel.getModel().clientCommand.addListener(clientCommandReceiver);
        System.out.println("mainPageView: listeners added to model " + viewModel.getModel().hashCode());
    }

    public void passStageAndScene(Stage stage, Scene scene) {
        this.stage = stage;
        this.scene = scene;
        System.out.println(this.getClass().getName() + ": this.scene=" + scene.hashCode() + "; this.stage=" + stage.hashCode());
    }

    private void loadImages() {
        // TODO: redo
        scrollPane.setOnScroll(event -> {
            double scrollPosition = scrollPane.getVvalue();
            if (scrollPosition == 1.0) {
                loadNextImage(); // TODO: switch to image butches
            }
        });
        loadNextImage();
        loadNextImage();
        loadNextImage(); // TODO: remove after fixing no-scroll bug with not enough images
    }

    private void loadNextImage() {
        // TODO: move from testing to client interactions & split this method in two
        Image image = getNextImage();
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(600);

        double desiredWidth = 300;
        double desiredHeight = 200;
        double centerX = (image.getWidth() - desiredWidth) / 2;
        double centerY = (image.getHeight() - desiredHeight) / 2;
        Rectangle2D viewport = new Rectangle2D(centerX, centerY, desiredWidth, desiredHeight);
        imageView.setViewport(viewport);

        HBox imageContainer = new HBox();
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.getChildren().add(imageView);

        tilePane.getChildren().add(imageContainer);
        tilePane.setAlignment(Pos.CENTER);

        loadedImageCount++;
        System.out.println("Next image loaded");
    }

    private Image getNextImage() {
        return viewModel.getNextImage();
        // TODO: work with image getters, names & anything else goes here
    }

    @FXML private void sendRequestQuitToLogin() {
        viewModel.quit();
    }

    public void openInView() throws IOException {
        System.out.println("mainPageView: open-in-view request received");
        System.out.println("mainPageView: setting scene" + scene.hashCode() + " to stage " + stage.hashCode());
        loadNextImage();
        loadImages(); // TODO: should be here or not?
        stage.setScene(scene);
        stage.show();
    }

    public void hideInView() {
        System.out.println("mainPageView: close-in-view request received");
    }
}
