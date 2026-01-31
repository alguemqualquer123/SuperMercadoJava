package com.supermercado.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Properties;

/**
 * Service para gerenciar configurações do sistema
 */
@Service
public class ConfigService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);
    private static final String CONFIG_FILE = "config.properties";
    private Properties properties;

    public ConfigService() {
        carregarConfiguracoes();
    }

    /**
     * Carrega as configurações do arquivo
     */
    private void carregarConfiguracoes() {
        properties = new Properties();
        File configFile = new File(CONFIG_FILE);
        
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
                logger.info("Configurações carregadas do arquivo");
            } catch (IOException e) {
                logger.error("Erro ao carregar configurações", e);
                // Usa valores padrão
                inicializarPadroes();
            }
        } else {
            inicializarPadroes();
        }
    }

    /**
     * Inicializa valores padrão
     */
    private void inicializarPadroes() {
        properties.setProperty("darkMode", "false");
        properties.setProperty("fullScreen", "false");
        logger.info("Configurações padrão inicializadas");
    }

    /**
     * Salva as configurações no arquivo
     */
    private void salvarConfiguracoes() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Configurações do SuperMercado PDV");
            logger.info("Configurações salvas com sucesso");
        } catch (IOException e) {
            logger.error("Erro ao salvar configurações", e);
        }
    }

    /**
     * Verifica se o dark mode está ativo
     */
    public boolean isDarkMode() {
        return Boolean.parseBoolean(properties.getProperty("darkMode", "false"));
    }

    /**
     * Define o dark mode
     */
    public void setDarkMode(boolean darkMode) {
        properties.setProperty("darkMode", String.valueOf(darkMode));
        salvarConfiguracoes();
        logger.info("Dark mode definido para: {}", darkMode);
    }

    /**
     * Verifica se está em tela cheia
     */
    public boolean isFullScreen() {
        return Boolean.parseBoolean(properties.getProperty("fullScreen", "false"));
    }

    /**
     * Define tela cheia
     */
    public void setFullScreen(boolean fullScreen) {
        properties.setProperty("fullScreen", String.valueOf(fullScreen));
        salvarConfiguracoes();
    }

    /**
     * Obtém uma propriedade genérica
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Define uma propriedade genérica
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
        salvarConfiguracoes();
    }
}

