package com.example.virsa.controller;

import com.example.virsa.model.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class PracticeDashboardController {

    @FXML
    private void handleStartPractice(ActionEvent event) {
        if (UserSession.getInstance().getCurrentLearningLanguage() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Course Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a language from the Languages tab first!");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/virsa/view/active_lesson_view.fxml"));
            Parent root = loader.load();

            
            ActiveLessonController controller = loader.getController();
            controller.setPracticeMode(true);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            
            root.getStylesheets().clear();
            String themeFile = UserSession.getInstance().isDarkMode() ? "dark.css" : "light.css";
            root.getStylesheets().add(getClass().getResource("/com/example/virsa/styles/" + themeFile).toExternalForm());

            stage.setScene(new Scene(root, 1000, 700));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
