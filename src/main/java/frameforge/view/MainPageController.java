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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static java.lang.Thread.sleep;

public class MainPageController {
    @FXML
    public HBox fullSizePane; // TODO: edit position & qualifiers
    @FXML
    public ImageView fullSizeImageView;
    @FXML
    public HBox scrollMode;
    @FXML
    public HBox leaderBoardBox; // TODO: switch after testing
    @FXML public VBox leaderBoardContainer;
    private Stage stage; // single stage instance shared with some other menus
    private Scene scene; // unique scene used to avoid repeated loading of the same menu

    private MainPageViewModel viewModel;

    // TODO: preferred width & height corrections
    @FXML
    private MenuButton menuButton; // TODO: set text to nickname
    @FXML
    private Button leaderboardButton;
    @FXML
    private Button uploadImageButton; // TODO: do I really need these buttons as class members?
    @FXML
    private TilePane tilePane;
    @FXML
    private ScrollPane scrollPane;
    private int loadedImageCount = 0;

    private final ChangeListener<MainPageModel.ClientCommands> clientCommandReceiver = (obs, oldCommand, newCommand) -> {
        System.out.println("mainPageView: changeListener fired on client command reception: oldCommand=" + oldCommand + ", newCommand=" + newCommand);
        switch (newCommand) {
            case show -> {
                try {
                    openInView();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case close -> hideInView();
            case loadPost -> loadNextImageButchFromModel();
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
        // TODO: is it required to add this to scene?
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
        System.out.println(this.getClass().getName() + ": this.scene=" + scene.hashCode() + "; this.stage=" + stage.hashCode());
    }

    boolean isLoadingMore = false;

    private void addScrollListener() {
        // TODO: redo
        scrollPane.setOnScroll(event -> {
            double scrollPosition = scrollPane.getVvalue();
            if (scrollPosition >= 0.95) {
                System.out.println("Reached end of scrollPane");
                if (!isLoadingMore) {
                    isLoadingMore = true;
                    sendRequestGetNextImage();
                    isLoadingMore = false;
                }
            }
        });
        sendRequestGetNextImage();
        try {
            sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        sendRequestGetNextImage(); // TODO: update this method after choosing optimal number of posts to load first
    }

    private void sendRequestGetNextImage() { // TODO: check if it works at all
//        viewModel.getModel().viewAction.setValue(MainPageModel.ViewActions.reachedNextPostBox);
        loadNextImageButchFromModel();
    }

    private void setFitCustom(ImageView imageView, double width, double height) { // TODO: is needed?
        Image image = imageView.getImage();
        if (image != null) {
            if (image.getHeight() / image.getWidth() > height / width) {
                imageView.setFitHeight(height);
            } else {
                imageView.setFitWidth(width);
            }
        }
    }

    private void loadNextImageButchFromModel() {
        // TODO: rewrite to use CompletableFuture!
        try {
            // TODO: stretch scrollPane over entire screen or find a way for scroll to register when mouse is not pointed to scrollPane
            Pair<String, List<Image>> nextIdAndImage = getNextImage();
            List<Image> images = nextIdAndImage.getValue();
            ImageView imageView = new ImageView(images.getFirst());
            imageView.setPreserveRatio(true);

            double imageViewWidth = 600; // TODO: to static const?
            double imageViewHeight = 400;

            setFitCustom(imageView, imageViewWidth, imageViewHeight);

            StackPane imagePane = new StackPane(imageView);
            imagePane.setPrefSize(imageViewWidth, imageViewHeight);
            imagePane.setStyle("-fx-background-color: #d1e1d4;");

            Button nextImageButton = new Button("->"); // TODO: switch to free-use icons
            nextImageButton.getStyleClass().add("post-button");
            Button previousImageButton = new Button("<-"); // TODO: switch to free-use icons
            previousImageButton.getStyleClass().add("post-button");

            // TODO: position image switch buttons on mid-height
            // TODO: set them visible if courser is on the sides of image

            nextImageButton.setOnAction(event -> {
                int currentImageIndex = images.indexOf(imageView.getImage());
                int nextImageIndex = (currentImageIndex + 1) % images.size();
                imageView.setImage(images.get(nextImageIndex));
                setFitCustom(imageView, imageViewWidth, imageViewHeight);
            });
            previousImageButton.setOnAction(event -> {
                int currentImageIndex = images.indexOf(imageView.getImage());
                int nextImageIndex = (currentImageIndex - 1) % images.size();
                if (nextImageIndex < 0) nextImageIndex += images.size();
                imageView.setImage(images.get(nextImageIndex));
                setFitCustom(imageView, imageViewWidth, imageViewHeight);
            });

            Button likeButton = new Button("Like");
            Button commentButton = new Button("Comment");
            Button shareButton = new Button("Share");
            Button saveButton = new Button("Save");


            // TODO: styles.css color update
            likeButton.getStyleClass().add("post-button");
            commentButton.getStyleClass().add("post-button");
            shareButton.getStyleClass().add("post-button");
            saveButton.getStyleClass().add("post-button");

            HBox contextButtonContainer = new HBox(20, previousImageButton, likeButton, commentButton, shareButton, saveButton, nextImageButton);
            contextButtonContainer.setAlignment(Pos.BOTTOM_CENTER);
            contextButtonContainer.setTranslateY(-10); // a tad higher than image border

            imagePane.setOnMouseEntered(event -> {
                contextButtonContainer.setVisible(true);
                System.out.println("mouse entered imagePane, show context actions");
            });
            imagePane.setOnMouseExited(event -> {
                contextButtonContainer.setVisible(false);
                System.out.println("mouse exited imagePane, hide context actions");
            });

            likeButton.setOnAction(event -> sendRequestLikePost(nextIdAndImage.getKey()));
            saveButton.setOnAction(event -> sendRequestSavePic(nextIdAndImage.getKey(), images.indexOf(imageView.getImage())));

            imagePane.getChildren().add(contextButtonContainer);
            contextButtonContainer.setVisible(false);

            imagePane.setOnMouseClicked(event -> {
                System.out.println("Clicked on an image!");
                Image imageToLoad = imageView.getImage();
                fullSizeImageView.setImage(imageToLoad);

                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                double screenWidth = screenBounds.getWidth();
                double screenHeight = screenBounds.getHeight();

                double imageWidth = imageToLoad.getWidth();
                double imageHeight = imageToLoad.getHeight();

                double aspectRatio = imageWidth / imageHeight;
                double optimalWidth = screenWidth * 0.9; // TODO: magic number 0.9 to class constants
                double optimalHeight = optimalWidth / aspectRatio;

                if (optimalHeight > screenHeight * 0.9) {
                    optimalHeight = screenHeight * 0.9;
                    optimalWidth = optimalHeight * aspectRatio;
                }

                fullSizeImageView.setFitWidth(optimalWidth);
                fullSizeImageView.setFitHeight(optimalHeight);

                fullSizePane.setPrefSize(optimalWidth, optimalHeight);
                fullSizePane.getChildren().clear();
                fullSizePane.getChildren().add(fullSizeImageView);
                fullSizePane.setVisible(true);
            });

            fullSizePane.setOnMouseClicked(event -> {
                if (!event.getTarget().equals(imageView)) {
                    fullSizePane.setVisible(false);
                }
            });

            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    fullSizePane.setVisible(false);
                }
            });

            tilePane.getChildren().add(imagePane);

            loadedImageCount++;
            System.err.println("Next post loaded");
        } catch (NullPointerException e) {
            System.err.println("error when trying to load an image: please check path settings and model methods' errors");
        }
    }

    private Pair<String, List<Image>> getNextImage() {
        return viewModel.getNextImage();
        // TODO: work with image getters, names & anything else goes here
    }

    @FXML
    private void sendRequestQuitToLogin() {
        viewModel.quit();
    }

    @FXML
    private void sendRequestOpenPostCreationMenu() {
        viewModel.openPostCreationMenu();
    }

    private void sendRequestLikePost(String postID) {
        viewModel.like(postID);
    }

    private void sendRequestSharePost(String postID) {

    }

    private void sendRequestCommentOnPost(String postID) {

    }

    private void sendRequestSavePic(String postID, Integer picNum) {
        // TODO: move to model and add signals
        handleSignalSavePic(postID, picNum); // TODO: remove when connecting to client
    }

    private void handleSignalSavePic(String postID, Integer picNum) {
        viewModel.savePic(postID, picNum);
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

    public void toggleLeaderboardMode() {
        if (scrollMode.isVisible()) {
            scrollMode.setVisible(false);
            leaderBoardBox.setVisible(true);
            leaderboardButton.setText("Resume scrolling");

            // TODO: signals for leaderboard and normal mode
            int place = 1;
            for (var person : viewModel.getLeaderboard()) {
                HBox personContainer = new HBox();

                Label placeLabel = new Label(Integer.toString(place));
                place++;
                placeLabel.setFont(Font.font(14));

                Label usernameLabel = new Label(person.getUsername());
                usernameLabel.setFont(Font.font(14));

                Label likeCountLabel = new Label("Likes: " + person.getLikeCount());
                likeCountLabel.setFont(Font.font(14));

                personContainer.getChildren().addAll(placeLabel, usernameLabel, likeCountLabel);

                personContainer.setSpacing(10);
                personContainer.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10px;"); // TODO: use styles.css

                leaderBoardContainer.getChildren().add(personContainer);
            }

            leaderBoardBox.getChildren().clear();
            leaderBoardBox.getChildren().add(leaderBoardContainer);
        } else {
            scrollMode.setVisible(true);
            leaderBoardBox.setVisible(false);
            leaderboardButton.setText("Leaderboard");
        }
    } // TODO: make it send requests
}
