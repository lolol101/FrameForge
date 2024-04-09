package com.ru.hse.frameforge.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainPageController {
    // TODO: preferred width & height corrections
    @FXML private MenuButton menuButton; // TODO: set text to nickname
    @FXML private MenuButton leaderboardButton;
    @FXML private TilePane tilePane;
    @FXML private HBox hbox;
    @FXML private ScrollPane scrollPane;
    private int loadedImageCount = 0;

    public int scrollWidth = 800;

    public void initialize() {
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        loadNextImage();
        loadImages();
    }

    private void loadImages() {
        // TODO: redo
        scrollPane.setOnScroll(event -> {
            double scrollPosition = scrollPane.getVvalue();
            if (scrollPosition == 1.0) {
                loadNextImage(); // TODO: switch to image butches: small edit of loadNextImage()
            }
        });
        loadNextImage();
    }

    private void loadNextImage() {
        // TODO: move from testing to client interactions & split this method in two
        Image image = getNextImage(getClass().getResource("test/images/pic_" + loadedImageCount + ".jpg").toExternalForm());
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

    private Image getNextImage(String name) {
        // TODO: work with image getters, names & anything else goes here
        return new Image(name);
    }

    public void showInView() throws IOException {
        Stage stage = (Stage) menuButton.getScene().getWindow();
        stage.close();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("LoginView.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void hideInView() {
        Stage stage = (Stage) menuButton.getScene().getWindow();
        stage.close();
    }
}
