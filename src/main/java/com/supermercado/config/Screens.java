package com.supermercado.config;

import java.util.Optional;

public enum Screens {
    LOGIN("Login", "/fxml/login.fxml", Optional.of("/css/style.css")),
    MAIN("Main", "/fxml/Main.fxml", Optional.of("/css/style.css")),
    ADMINISTRACAO("Administracao", "/fxml/Administracao.fxml", Optional.of("/css/style.css")),
    BUSCAR_PRODUTO("BuscarProduto", "/fxml/BuscarProduto.fxml", Optional.of("/css/style.css")),
    RELATORIOS("Relatorios", "/fxml/Relatorios.fxml", Optional.of("/css/style.css")),
    DASHBOARD("Dashboard", "/fxml/Dashboard.fxml", Optional.of("/css/style.css"));

    private final String name;
    private final String fxmlPath;
    private final String css;

    Screens(String name, String fxmlPath, Optional<String> css) {
        this.name = name;
        this.fxmlPath = fxmlPath;
        this.css = css.orElse("");
    }

    public String getName() { return name; }
    public String getFxmlPath() { return fxmlPath; }
    public String getCss() { return css; }
}
