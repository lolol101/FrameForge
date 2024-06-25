package frameforge.view;

import frameforge.model.MainPageModel;
import frameforge.viewmodel.MainPageViewModel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
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
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static frameforge.model.MainPageModel.ClientCommands;
import static java.lang.Thread.sleep;

public class MainPageController extends Controller<MainPageModel, MainPageViewModel> {
    private static final double FULLSIZE_IMG_FACTOR = 0.9;
    private static final double SCROLLBAR_MAX_VALUE_BEFORE_UPDATE = 0.95;
    private static final double imageInFeedWidth = 600;
    private static final double imageInFeedHeight = 400;
    private static boolean postIsLoading = false;
    private Timeline postRequestResetTimeline;
    @FXML
    private HBox fullSizePane;
    @FXML
    private ImageView fullSizeImageView;
    @FXML
    private HBox scrollMode;
    @FXML
    private HBox leaderboardMode;
    @FXML
    private VBox leaderBoardContainer;

    @FXML
    private Button leaderboardButton;
    @FXML
    private TilePane postsTilePane;
    @FXML
    private ScrollPane scrollPane;

    private Task<Boolean> getNewPostTask;
    private final ChangeListener<ClientCommands> clientCommandReceiver = (obs, oldCommand, newCommand) -> {
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
            case loadPost -> loadNextImageBatchFromModel();
            case toggleLeaderBoard -> toggleLeaderboardMode();
        }
        viewModel.getModel().clientCommand.setValue(ClientCommands.zero);
    };

    public MainPageController() {
        MainPageModel model = new MainPageModel();
        viewModel = new MainPageViewModel(model);
    }

    public void initialize() {
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPane.setOnScroll(event -> {
            if (scrollPane.getVvalue() >= scrollPane.getVmax() - 1) {
                System.out.println("Reached end of scrollPane");
                if (!postIsLoading) {
                    postIsLoading = true;
                    Timeline postRequestResetTimeline = new Timeline(new KeyFrame(Duration.seconds(1), timelineEvent -> postIsLoading = false));
                    postRequestResetTimeline.play();

                    sendRequestGetNextImage();
                }
            }
        });
    }

    @Override
    public void setModel(MainPageModel model) {
        removeListeners();
        viewModel.setModel(model);
        addListeners();
        System.out.println("mainPageView: mainPage model set to " + model.hashCode());
    }

    void removeListeners() {
        viewModel.getModel().clientCommand.removeListener(clientCommandReceiver);
        System.out.println("mainPageView: listeners removed");
    }

    void addListeners() {
        viewModel.getModel().clientCommand.addListener(clientCommandReceiver);
        System.out.println("mainPageView: listeners added to model " + viewModel.getModel().hashCode());
    }

    public void passStageAndScene(Stage stage, Scene scene) {
        this.stage = stage;
        this.scene = scene;
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
        System.out.println(this.getClass().getName() + ": this.scene=" + scene.hashCode() + "; this.stage=" + stage.hashCode());

        scene.setOnScroll(event -> {
            double deltaY = event.getDeltaY();
            double coefficient = scrollPane.getHeight();
            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY / coefficient);

            if (scrollPane.getVvalue() >= scrollPane.getVmax() - 1) {
                System.out.println("Reached end of scrollPane");
                if (!postIsLoading) {
                    postIsLoading = true;
                    postRequestResetTimeline = new Timeline(new KeyFrame(Duration.seconds(1), timelineEvent -> postIsLoading = false));
                    postRequestResetTimeline.play();

                    sendRequestGetNextImage();
                }
            }
            // TODO: remove and bind to a StackPane? HBox? containing ScrollPane
        });
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                fullSizePane.setVisible(false);
            }
        });
    }

    private void sendRequestGetNextImage() {
        viewModel.getModel().viewAction.setValue(MainPageModel.ViewActions.reachedNextPostBox);
    }

    private void setFitCustom(ImageView imageView) {
        Image image = imageView.getImage();
        if (image != null) {
            if (image.getHeight() / image.getWidth() > imageInFeedHeight / imageInFeedWidth) {
                imageView.setFitHeight(imageInFeedHeight);
            } else {
                imageView.setFitWidth(imageInFeedWidth);
            }
        }
    }

    private void loadNextImageBatchFromModel() {
        if (getNewPostTask != null && getNewPostTask.isRunning()) {
            System.err.println("Next post is already being loaded");
            return;
        }
        getNewPostTask = new Task<>() {
            @Override
            protected Boolean call() {
                try {
                    Pair<String, List<Image>> nextIdAndImage = getNextPost();
                    List<Image> images = nextIdAndImage.getValue();
                    ImageView imageView = new ImageView(images.getFirst());
                    imageView.setPreserveRatio(true);

                    setFitCustom(imageView);

                    StackPane imagePane = new StackPane(imageView);
                    imagePane.setPrefSize(imageInFeedWidth, imageInFeedHeight);
                    imagePane.setStyle("-fx-background-color: #d1e1d4;");

                    Button nextImageButton = new Button("->"); // TODO: switch to free-use icons
                    nextImageButton.getStyleClass().add("post-button");
                    Button previousImageButton = new Button("<-"); // TODO: switch to free-use icons
                    previousImageButton.getStyleClass().add("post-button");

                    nextImageButton.setOnAction(event -> {
                        int currentImageIndex = images.indexOf(imageView.getImage());
                        int nextImageIndex = (currentImageIndex + 1) % images.size();
                        imageView.setImage(images.get(nextImageIndex));
                        setFitCustom(imageView);
                    });
                    previousImageButton.setOnAction(event -> {
                        int currentImageIndex = images.indexOf(imageView.getImage());
                        int nextImageIndex = (currentImageIndex - 1) % images.size();
                        if (nextImageIndex < 0) nextImageIndex += images.size();
                        imageView.setImage(images.get(nextImageIndex));
                        setFitCustom(imageView);
                    });

                    ToggleButton likeButton = new ToggleButton();
                    Button saveButton = new Button("Save");

                    likeButton.getStyleClass().add("like-button");
                    Image imgIsNotLiked = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icon-like.png")));
                    ImageView imgViewNotLiked = new ImageView(imgIsNotLiked);
                    imgViewNotLiked.setPreserveRatio(true);
                    imgViewNotLiked.setFitHeight(10);
                    likeButton.setGraphic(imgViewNotLiked);

                    Image imgIsLiked = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icon-unlike.png")));
                    ImageView imgViewLiked = new ImageView(imgIsLiked);
                    imgViewLiked.setPreserveRatio(true);
                    imgViewLiked.setFitHeight(10);
                    likeButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            likeButton.setGraphic(imgViewLiked);
                        } else {
                            likeButton.setGraphic(imgViewNotLiked);
                        }
                    });

                    saveButton.getStyleClass().add("post-button");

                    HBox contextButtonContainer = new HBox(20, previousImageButton, likeButton, saveButton, nextImageButton);
                    contextButtonContainer.setAlignment(Pos.BOTTOM_CENTER);
                    contextButtonContainer.setTranslateY(-10); // a tad higher than image border

                    imagePane.setOnMouseEntered(event -> contextButtonContainer.setVisible(true));
                    imagePane.setOnMouseExited(event -> contextButtonContainer.setVisible(false));

                    likeButton.setOnAction(event -> sendRequestLikeOrUnlikePost(nextIdAndImage.getKey()));
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
                        double optimalWidth = screenWidth * FULLSIZE_IMG_FACTOR;
                        double optimalHeight = optimalWidth / aspectRatio;

                        if (optimalHeight > screenHeight * FULLSIZE_IMG_FACTOR) {
                            optimalHeight = screenHeight * FULLSIZE_IMG_FACTOR;
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

                    Platform.runLater(() -> postsTilePane.getChildren().add(imagePane));
                    System.err.println("Next post loaded");
                    return true;
                } catch (NullPointerException e) {
                    System.err.println("error when trying to load an image: please check path settings and model methods' errors");
                    return false;
                } finally {
                    postIsLoading = false;
                }
            }
        };

        Thread thread = new Thread(getNewPostTask);
        thread.setDaemon(true);
        thread.start();
    }

    private Pair<String, List<Image>> getNextPost() {
        return viewModel.getNextPost();
    }

    @FXML
    private void sendRequestQuitToLogin() {
        viewModel.quit();
    }

    @FXML
    private void sendRequestOpenPostCreationMenu() {
        viewModel.openPostCreationMenu();
    }

    private void sendRequestLikeOrUnlikePost(String postID) {
        viewModel.like(postID);
    }

    private void sendRequestSavePic(String postID, Integer picNum) {
        handleSignalSavePic(postID, picNum); // TODO: remove when connecting to client
    }

    private void handleSignalSavePic(String postID, Integer picNum) {
        viewModel.savePic(postID, picNum);
    }

    public void openInView() throws IOException {
        System.out.println("mainPageView: open-in-view request received");
        System.out.println("mainPageView: setting scene=" + scene.hashCode() + " to stage=" + stage.hashCode());
        if (postsTilePane.getChildren().isEmpty()) {
            for (int i = 0; i < 3; i++) {
                sendRequestGetNextImage();
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        stage.setScene(scene);
        stage.setHeight(1000.0);
        stage.setWidth(1920.0);
        stage.show();
    }

    @Override void hideInView() {
        if (postRequestResetTimeline != null && postRequestResetTimeline.getStatus() == Timeline.Status.RUNNING) {
            postRequestResetTimeline.stop();
        }
        System.out.println(this.getClass() + " hidden in view");
    }

    public void sendRequestToggleLeaderBoardMode() {
        viewModel.toggleLeaderboard();
    }

    public void toggleLeaderboardMode() {
        if (scrollMode.isVisible()) {
            scrollMode.setVisible(false);
            leaderboardMode.setVisible(true);
            leaderboardButton.setText("Resume scrolling");

            leaderBoardContainer.getChildren().clear();
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
        } else {
            scrollMode.setVisible(true);
            leaderboardMode.setVisible(false);
            leaderboardButton.setText("Leaderboard");
        }
    }
}
