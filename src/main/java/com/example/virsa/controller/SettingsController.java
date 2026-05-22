package com.example.virsa.controller;

import com.example.virsa.model.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SettingsController {

    @FXML private ToggleButton themeToggle;

    @FXML
    private void handleThemeToggle() {
        
        Scene scene = themeToggle.getScene();
        Parent rootContainer = scene.getRoot();

        
        String lightTheme = Objects.requireNonNull(getClass().getResource("/com/example/virsa/styles/light.css")).toExternalForm();
        String darkTheme = Objects.requireNonNull(getClass().getResource("/com/example/virsa/styles/dark.css")).toExternalForm();

        if (themeToggle.isSelected()) {
            
            themeToggle.setText("ON");
            themeToggle.setStyle("-fx-background-color: #58CC02; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 20 8 20; -fx-cursor: hand;");

            UserSession.getInstance().setDarkMode(true);

            rootContainer.getStylesheets().remove(lightTheme);
            if (!rootContainer.getStylesheets().contains(darkTheme)) {
                rootContainer.getStylesheets().add(darkTheme);
            }
        } else {
            
            themeToggle.setText("OFF");
            themeToggle.setStyle("-fx-background-color: #E5E5E5; -fx-text-fill: #777777; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 20 8 20; -fx-cursor: hand;");

            
            UserSession.getInstance().setDarkMode(false);

            rootContainer.getStylesheets().remove(darkTheme);
            if (!rootContainer.getStylesheets().contains(lightTheme)) {
                rootContainer.getStylesheets().add(lightTheme);
            }
        }
    }

    @FXML
    private void handleSignOut(ActionEvent event) {
        UserSession.getInstance().logout();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/virsa/view/login_view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}