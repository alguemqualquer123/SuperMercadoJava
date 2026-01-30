package com.supermercado;

import com.supermercado.service.UsuarioService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Classe principal da aplicação JavaFX integrada com Spring Boot
 */
@SpringBootApplication
public class SuperMercadoApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(SuperMercadoApp.class);
    private static ConfigurableApplicationContext springContext;
    public static Stage primaryStage;

    @Override
    public void init() {
        logger.info("Iniciando contexto Spring Boot...");
        springContext = SpringApplication.run(SuperMercadoApp.class);
    }

    @Override
    public void start(Stage stage) {
        try {
            logger.info("Iniciando interface JavaFX...");
            primaryStage = stage;

            showLogin();

            primaryStage.setTitle("SuperMercado PDV");
            primaryStage.setMaximized(true);
            primaryStage.show();

            logger.info("Aplicação iniciada com sucesso");

        } catch (Exception e) {
            logger.error("Erro ao iniciar aplicação", e);
            e.printStackTrace();
        }
    }

    /**
     * Exibe a tela de login em modo Tela Cheia Exclusiva
     */
    public static void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(SuperMercadoApp.class.getResource("/fxml/Login.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(SuperMercadoApp.class.getResource("/css/style.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setTitle("Login - SuperMercado PDV");
            primaryStage.setFullScreenExitHint("");
            primaryStage.setFullScreen(true);
            primaryStage.show();

            logger.info("Tela de login exibida em Fullscreen");
        } catch (Exception e) {
            logger.error("Erro ao exibir login", e);
        }
    }

    /**
     * Exibe a tela principal em uma nova janela maximizada (não Fullscreen)
     */
    public static void showMain() {
        try {
            // Fecha a janela de login
            primaryStage.setFullScreen(false);
            primaryStage.hide();

            FXMLLoader loader = new FXMLLoader(SuperMercadoApp.class.getResource("/fxml/Main.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            Stage mainStage = new Stage();
            mainStage.setTitle("Painel Principal - SuperMercado PDV");

            // Define o tamanho preferencial
            Scene scene = new Scene(root, 1280, 800);
            scene.getStylesheets().add(SuperMercadoApp.class.getResource("/css/style.css").toExternalForm());

            mainStage.setScene(scene);
            mainStage.setMaximized(true);

            // Ao fechar a principal, encerra a aplicação
            mainStage.setOnCloseRequest(event -> Platform.exit());

            mainStage.show();
            logger.info("Painel principal exibido em janela separada");

        } catch (Exception e) {
            logger.error("Erro ao carregar tela principal", e);
        }
    }

    public static void openInNewWindow(String fxmlPath, String title, double width, double height) {
        try {
            FXMLLoader loader = new FXMLLoader(SuperMercadoApp.class.getResource(fxmlPath));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(SuperMercadoApp.class.getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            logger.error("Erro ao abrir janela: " + fxmlPath, e);
        }
    }

    @Override
    public void stop() {
        logger.info("Encerrando aplicação...");
        springContext.close();
        Platform.exit();
        logger.info("Aplicação encerrada");
    }

    public static void main(String[] args) {
        System.setProperty("javafx.preloader", SplashScreen.class.getCanonicalName());
        launch(args);
    }
}
