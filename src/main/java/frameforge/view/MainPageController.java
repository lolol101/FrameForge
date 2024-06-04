package frameforge.view;

import frameforge.model.MainPageModel;
import frameforge.viewmodel.MainPageViewModel;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static java.lang.Thread.sleep;

public class MainPageController {
    private Stage stage; // single stage instance shared with some other menus
    private Scene scene; // unique scene used to avoid repeated loading of the same menu

    private MainPageViewModel viewModel;

    // TODO: preferred width & height corrections
    @FXML private MenuButton menuButton; // TODO: set text to nickname
    @FXML private MenuButton leaderboardButton;
    @FXML private Button uploadImageButton; // TODO: do I really need these buttons as class members?
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
            case loadPost -> loadNextImageFromModel();
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
        viewModel.setModel(model);
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
        // TODO: separate .css files
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
        System.out.println(this.getClass().getName() + ": this.scene=" + scene.hashCode() + "; this.stage=" + stage.hashCode());
    }

    private void addScrollListener() {
        // TODO: redo
        scrollPane.setOnScroll(event -> {

            double scrollPosition = scrollPane.getVvalue();
            if (scrollPosition == 1.0) {
                sendRequestGetNextImage(); // TODO: switch to image butches
            }
        });
        sendRequestGetNextImage();
        try {
            sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        sendRequestGetNextImage(); // TODO: remove after fixing no-scroll bug with not enough images
    }

    private void sendRequestGetNextImage() {
        viewModel.getModel().viewAction.setValue(MainPageModel.ViewActions.reachedNextPostBox);
//        loadNextImageFromModel();
    }

    private void loadNextImageFromModel() {
        // TODO: is it really async?
        Image image;
        try {
            // TODO: stretch scrollPane over entire screen or find a way for scroll to register when mouse is not pointed to scrollPane
            // TODO: fix the bug with spaces between imagePanes growing over time (how? why?)
            Pair<String, List<Image>> nextIdAndImage = getNextImage();
            List<Image> images = nextIdAndImage.getValue();
//            image = nextIdAndImage.getValue().getFirst();
            ImageView imageView = new ImageView(images.getFirst());
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(600);

            StackPane imagePane = new StackPane(imageView);
            imagePane.setMaxWidth(imageView.getFitWidth());
            imagePane.setMaxHeight(imageView.getFitHeight());
//            imagePane.setPrefSize(600, 300); // Adjust size if needed

            Button likeButton = new Button("Like");
            Button commentButton = new Button("Comment");
            Button shareButton = new Button("Share");
            Button saveButton = new Button("Save");


            // TODO: styles.css color update
            likeButton.getStyleClass().add("post-button");
            commentButton.getStyleClass().add("post-button");
            shareButton.getStyleClass().add("post-button");
            saveButton.getStyleClass().add("post-button");

            Button nextImageButton = new Button("->"); // TODO: switch to free-use icons
            nextImageButton.getStyleClass().add("post-button");
            Button previousImageButton = new Button("<-"); // TODO: switch to free-use icons
            previousImageButton.getStyleClass().add("post-button");

            // TODO: position image switch buttons on mid-height
            // TODO: set them visible if courser is on the sides of image
//            Region region = new Region();
//            HBox.setHgrow(region, Priority.ALWAYS);
//            HBox switchButtonContainer = new HBox(previousImageButton, region, nextImageButton);
//            switchButtonContainer.setAlignment(Pos.CENTER);
//            imagePane.getChildren().add(switchButtonContainer);

            nextImageButton.setOnAction(event -> {
                int currentImageIndex = images.indexOf(imageView.getImage());
                int nextImageIndex = (currentImageIndex + 1) % images.size();
                imageView.setImage(images.get(nextImageIndex));
            });
            previousImageButton.setOnAction(event -> {
                int currentImageIndex = images.indexOf(imageView.getImage());
                int nextImageIndex = (currentImageIndex - 1) % images.size();
                if (nextImageIndex < 0) nextImageIndex += images.size();
                imageView.setImage(images.get(nextImageIndex));
            });

            HBox contextButtonContainer = new HBox(20, previousImageButton, likeButton, commentButton, shareButton, saveButton, nextImageButton);
            contextButtonContainer.setAlignment(Pos.BOTTOM_CENTER);
            contextButtonContainer.setTranslateY(-10); // Position buttons slightly above lower image border

            imagePane.setOnMouseEntered(event -> {
                contextButtonContainer.setVisible(true);
                System.out.println("mouse entered imagePane, show context actions");
            });
            imagePane.setOnMouseExited(event -> {
                contextButtonContainer.setVisible(false);
                System.out.println("mouse exited imagePane, hide context actions");
            });

            likeButton.setOnMouseEntered(event -> {
                System.out.println("mouse entered likeButton");
            });
            likeButton.setOnMouseExited(event -> {
                System.out.println("mouse exited likeButton");
            });
            commentButton.setOnMouseEntered(event -> {
                System.out.println("mouse entered commentButton");
            });
            commentButton.setOnMouseExited(event -> {
                System.out.println("mouse exited commentButton");
            });
            shareButton.setOnMouseEntered(event -> {
                System.out.println("mouse entered shareButton");
            });
            shareButton.setOnMouseExited(event -> {
                System.out.println("mouse exited shareButton");
            });
            saveButton.setOnMouseEntered(event -> {
                System.out.println("mouse entered saveButton");
            });
            saveButton.setOnMouseExited(event -> {
                System.out.println("mouse exited saveButton");
            });

            imagePane.getChildren().add(contextButtonContainer);
            contextButtonContainer.setVisible(false);

            tilePane.getChildren().add(imagePane);

            loadedImageCount++;
            System.out.println("Next post loaded");
        } catch (NullPointerException e) {
            System.err.println("error when trying to load an image: please check path settings and model methods' errors");
        }
    }

    private Pair<String, List<Image>> getNextImage() {
        return viewModel.getNextImage();
        // TODO: work with image getters, names & anything else goes here
    }

    @FXML private void sendRequestQuitToLogin() {
        viewModel.quit();
    }

    @FXML private void sendRequestOpenPostCreationMenu() {
        viewModel.openPostCreationMenu();
    }

    private void sendRequestLikePost(String postID) {

    }

    private void sendRequestSharePost(String postID) {

    }

    private void sendRequestCommentOnPost(String postID) {

    }

    private void sendRequestSavePost(String postID) {

    }

    public void openInView() throws IOException {
        System.out.println("mainPageView: open-in-view request received");
        System.out.println("mainPageView: setting scene=" + scene.hashCode() + " to stage=" + stage.hashCode());
        sendRequestGetNextImage();
        addScrollListener(); // TODO: should be here or no?
        stage.setScene(scene);
        stage.show();
    }

    public void hideInView() {
        System.out.println("mainPageView: close-in-view request received");
    }
}
