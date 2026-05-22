package com.example.virsa.controller;

import com.example.virsa.model.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    @FXML
    private HBox activeCoursesBox;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label streakLabel;
    @FXML
    private Label xpLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserSession session = UserSession.getInstance();

        if (session.getUsername() != null) {
            userNameLabel.setText(session.getUsername());
        }
        if (session.getEmail() != null) {
            emailLabel.setText(session.getEmail());
        }

        streakLabel.setText(String.valueOf(session.getStreak()));
        xpLabel.setText(String.valueOf(session.getXp()));

        loadUserCourses(session);
    }

    private void loadUserCourses(UserSession session) {
        activeCoursesBox.getChildren().clear();

        for (String lang : session.getActiveLanguages()) {
            HBox coursePill = new HBox();
            coursePill.setAlignment(Pos.CENTER);
            coursePill.setSpacing(10.0);
            coursePill.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 10 15 10 15; -fx-border-color: #E5E5E5; -fx-border-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);");

            Circle colorCircle = new Circle(8.0, Color.web(session.getColorForLanguage(lang)));

            Label langLabel = new Label(lang);
            langLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #4B4B4B;");

            Button removeBtn = new Button("✕");
            removeBtn.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: #FF4B4B; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0 0 0 10;");

            removeBtn.setOnAction(event -> handleRemoveCourse(event, lang, coursePill));

            coursePill.getChildren().addAll(colorCircle, langLabel, removeBtn);
            activeCoursesBox.getChildren().add(coursePill);
        }
    }

    private void handleRemoveCourse(ActionEvent event, String langName, HBox coursePill) {
        Alert warning = new Alert(Alert.AlertType.WARNING);
        warning.setTitle("Remove Course");
        warning.setHeaderText("Wait, are you sure?");
        warning.setContentText("Removing " + langName + " will permanently delete all your progress.");

        Optional<ButtonType> result = warning.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            UserSession.getInstance().removeLanguage(langName);
            activeCoursesBox.getChildren().remove(coursePill);
        }
    }
}