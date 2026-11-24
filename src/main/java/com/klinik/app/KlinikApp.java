package com.klinik.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class KlinikApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        URL resource = getClass().getResource("/fxml/LoginView.fxml");
        if (resource == null) {
            resource = getClass().getResource("/com/klinik/app/fxml/LoginView.fxml");
        }

        if (resource != null) {
            Parent root = FXMLLoader.load(resource);
            Scene scene = new Scene(root, 420, 520);

            // Terapkan stylesheet global
            URL cssResource = getClass().getResource("/css/style.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }

            stage.setTitle("Login - Sistem Informasi MYCLINIC");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();
        } else {
            System.err.println("File LoginView.fxml tidak ditemukan!");
        }
    }
}
