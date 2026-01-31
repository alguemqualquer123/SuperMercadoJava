
package com.supermercado;

import com.supermercado.config.Screens;
import com.supermercado.config.StageManager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Classe principal da aplicação JavaFX integrada com Spring Boot
 */
@SpringBootApplication
public class SuperMercadoApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(SuperMercadoApp.class);
    private static ConfigurableApplicationContext springContext;
    @Autowired
    public static Stage primaryStage;
    public static StageManager stageManager;

    @Override
    public void init() {
        logger.info("Iniciando contexto Spring Boot...");
    }

    public static ConfigurableApplicationContext getSpringContext() {
        return springContext;
    }

    @Override
    public void start(Stage stage) {
        springContext = new SpringApplicationBuilder(SuperMercadoApp.class)
                .headless(false)
                .run();

        stageManager = springContext.getBean(StageManager.class);
        stageManager.setPrimaryStage(stage);
        stageManager.switchScene("/fxml/Login.fxml", "Login", true, "/css/style.css");
    }

    /**
     * Exibe a tela de login em modo Tela Cheia Exclusiva
     */
    public static void showLogin() {
        try {
            stageManager.replaceCurrentTab(Screens.LOGIN, true);
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
            stageManager.replaceCurrentTab(Screens.MAIN, true);

            logger.info("Painel principal exibido em janela separada");
        } catch (Exception e) {
            logger.error("Erro ao carregar tela principal", e);
        }
    }

    public static void openInNewWindow(String fxmlPath, String title, double width, double height) {
        stageManager.openNewWindow(fxmlPath,title,width,height, "");
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
//        ConfigurableApplicationContext context = new SpringApplicationBuilder(SuperMercadoApp.class)
//                .headless(false)
//                .run(args);
        launch(args);
    }
}
