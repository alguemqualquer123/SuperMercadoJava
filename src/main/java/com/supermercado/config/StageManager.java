package com.supermercado.config;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
public class StageManager {
    private Stage primaryStage;
    private final ApplicationContext context;

    private final Map<String, Scene> scenes = new HashMap<>();

    public StageManager(ApplicationContext context) {
        this.context = context;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public Stage switchScene(String fxml, String title, boolean fullscreen, String cssPath) {
        try {
            if (primaryStage == null) {
                throw new IllegalStateException("primaryStage ainda não foi inicializado. Chame setPrimaryStage(stage) primeiro!");
            }

            Scene scene = scenes.get(fxml);
            if (scene == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
                loader.setControllerFactory(context::getBean);
                Parent root = loader.load();

                scene = new Scene(root);
                if (cssPath != null && !cssPath.isEmpty()) {
                    scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
                }

                scenes.put(fxml, scene);
            }

            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.setFullScreen(fullscreen);
            primaryStage.show();

            return primaryStage;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao trocar de tela: " + fxml, e);
        }
    }

    public Scene getScene(String fxml) {
        return scenes.get(fxml);
    }

    public void closePrimaryStage() {
        if (primaryStage != null) {
            primaryStage.close();
        }
    }

    public Stage openNewWindow(String fxml, String title, double width, double height, String cssPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();

            Scene scene = new Scene(root, width, height);
            if (cssPath != null && !cssPath.isEmpty()) {
                scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
            } else {
                scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();

            scenes.put(fxml, scene);

            return stage;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao abrir nova janela: " + fxml, e);
        }
    }

    public void close() {
        primaryStage.close();
    }

    /**
     * Abre uma nova janela sem fechar a atual usando enum Screens
     */
    public Stage openInNewTab(Screens screen, boolean fullscreen) {
        return openNewWindow(
                screen.getFxmlPath(),
                screen.getName(),
                1280,
                800,
                screen.getCss()
        );
    }

    /**
     * Fecha a janela atual e abre a nova usando enum Screens
     */
    public void replaceCurrentTab(Screens screen, boolean fullscreen) {
        Stage newStage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(screen.getFxmlPath()));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();

            Scene scene = new Scene(root, 1280, 800);
            if (screen.getCss() != null && !screen.getCss().isEmpty()) {
                scene.getStylesheets().add(getClass().getResource(screen.getCss()).toExternalForm());
            }

            if (primaryStage != null) {
                primaryStage.close();
            }

            primaryStage = newStage;
            primaryStage.setScene(scene);
            primaryStage.setTitle(screen.getName());
            primaryStage.setFullScreen(fullscreen);
            primaryStage.show();

            scenes.put(screen.getFxmlPath(), scene);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao substituir janela: " + screen.getFxmlPath(), e);
        }
    }
}
