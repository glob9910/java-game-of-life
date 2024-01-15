package com.example.javafxdemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
        @Override
        public void start(Stage stage) throws IOException {

            String[] params = getParameters().getRaw().toArray(new String[0]);

            var loader = new FXMLLoader(getClass().getResource("gridView.fxml"));
            loader.setControllerFactory((ignored) -> {
                return new GridController(params[0]);
            });
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }

    public static void main(String[] args) {
        launch(args);
    }
}