package com.supermercado.config;

import javafx.scene.Scene;

@FunctionalInterface
public interface SceneAction {
    void execute(Scene scene);
}