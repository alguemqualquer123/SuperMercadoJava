package com.supermercado.controller;

import com.supermercado.SuperMercadoApp;
import com.supermercado.model.Usuario;
import com.supermercado.service.UsuarioService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Controller para a tela de login
 */
@Component
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel;

    private final UsuarioService usuarioService;
    private final com.supermercado.service.SessaoService sessaoService;

    @Autowired
    public LoginController(UsuarioService usuarioService, com.supermercado.service.SessaoService sessaoService) {
        this.usuarioService = usuarioService;
        this.sessaoService = sessaoService;
    }

    @FXML
    public void initialize() {
        // Enter para fazer login
        passwordField.setOnAction(event -> handleLogin());

        // Limpa campos ao abrir
        usernameField.clear();
        passwordField.clear();
        errorLabel.setVisible(false);

    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        errorLabel.setVisible(false);

        if (username.isEmpty() || password.isEmpty()) {
            showError("Por favor, preencha usuário e senha");
            return;
        }

        try {
            Usuario usuario = usuarioService.login(username, password);
            sessaoService.setUsuarioLogado(usuario);
            logger.info("Login bem-sucedido: {}", usuario.getNome());

            // Navega para tela principal
            SuperMercadoApp.showMain();

        } catch (Exception e) {
            logger.warn("Falha no login: {}", e.getMessage());
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
