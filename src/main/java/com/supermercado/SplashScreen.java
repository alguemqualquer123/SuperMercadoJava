package com.supermercado;

import javafx.application.Preloader;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.effect.DropShadow;

import java.io.InputStream;
import java.util.Properties;

public class SplashScreen extends Preloader {

    private Stage splashStage;

    @Override
    public void start(Stage stage) throws Exception {
        this.splashStage = stage;

        // Load App Name from properties
        String appName = "SuperMercado PDV";
        try (InputStream input = getClass().getResourceAsStream("/application.properties")) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);
                appName = props.getProperty("app.name", appName);
            }
        } catch (Exception e) {
            // Ignore, use default
        }

        // Layout
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle(
                "-fx-background-color: white; -fx-padding: 40; -fx-background-radius: 10; -fx-border-radius: 10;");

        // Logo
        ImageView logoView = new ImageView();
        try {
            Image logo = new Image(getClass().getResourceAsStream("/images/logo.png"));
            logoView.setImage(logo);
            logoView.setFitWidth(150);
            logoView.setPreserveRatio(true);
        } catch (Exception e) {
            // No logo found or error
            System.err.println("Logo not found: " + e.getMessage());
        }

        // Title
        Label titleLabel = new Label(appName);
        titleLabel.setFont(new Font("Arial", 24));
        titleLabel.setStyle("-fx-text-fill: #333333; -fx-font-weight: bold;");

        // Progress Bar
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(200);
        progressBar.setStyle("-fx-accent: #007bff;");

        root.getChildren().addAll(logoView, titleLabel, progressBar);

        // Scene with transparent background
        Scene scene = new Scene(root, 400, 300);
        scene.setFill(Color.TRANSPARENT);

        // Stage setup
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);

        // Add shadow for better look
        root.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.2)));

        stage.centerOnScreen();
        stage.show();
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        if (info.getType() == StateChangeNotification.Type.BEFORE_START) {
            splashStage.hide();
        }
    }
}
