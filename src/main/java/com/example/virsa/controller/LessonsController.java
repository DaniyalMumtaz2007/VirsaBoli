package com.example.virsa.controller;

import com.example.virsa.model.UserDAO;
import com.example.virsa.model.UserSession;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LessonsController implements Initializable {

    @FXML private VBox lessonsContainer;

    private static final String[] UNIT_COLORS = {
        "#58CC02", "#1CB0F6", "#CE82FF", "#FF9600", "#FF4B4B"
    };
    private static final String[] UNIT_DARK_COLORS = {
        "#46A302", "#1480B3", "#A455DB", "#E08600", "#CC3B3B"
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserSession session = UserSession.getInstance();
        String currentLang = session.getCurrentLearningLanguage();

        if (currentLang == null) {
            showComingSoon("Pick a language first!");
            return;
        }

        UserDAO dao = new UserDAO();
        List<Integer> units = dao.getAvailableUnits(currentLang);

        if (units.isEmpty()) {
            showComingSoon("No lessons yet for " + currentLang + ". Coming soon!");
            return;
        }

        int currentLevel = session.getCurrentLessonLevel();

        for (int u = 0; u < units.size(); u++) {
            int unitNum = units.get(u);
            String color = UNIT_COLORS[u % UNIT_COLORS.length];
            String darkColor = UNIT_DARK_COLORS[u % UNIT_DARK_COLORS.length];

            
            String unitName = dao.getUnitName(currentLang, unitNum);
            VBox unitHeader = new VBox();
            unitHeader.setAlignment(Pos.CENTER_LEFT);
            unitHeader.setSpacing(5);
            unitHeader.setStyle("-fx-background-color: linear-gradient(to right bottom, " + color + ", " + darkColor + "); -fx-background-radius: 15; -fx-padding: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 5);");

            Label unitTitle = new Label("UNIT " + unitNum);
            unitTitle.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: 900; -fx-font-family: 'Arial Black';");

            Label unitSubtitle = new Label(unitName);
            unitSubtitle.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

            unitHeader.getChildren().addAll(unitTitle, unitSubtitle);
            lessonsContainer.getChildren().add(unitHeader);

            
            VBox pathContainer = new VBox();
            pathContainer.setAlignment(Pos.CENTER);
            pathContainer.setSpacing(25);

            List<Integer> lessons = dao.getAvailableLessons(currentLang, unitNum);

            for (int i = 0; i < lessons.size(); i++) {
                int lessonNum = lessons.get(i);
                Button btn = new Button(String.valueOf(lessonNum));
                btn.getStyleClass().add("lesson-chip");

                int translateX = (i % 2 == 1) ? 40 : -20;
                btn.setTranslateX(translateX);

                if (lessonNum < currentLevel) {
                    btn.setStyle("-fx-background-radius: 50; -fx-min-width: 70; -fx-min-height: 70; -fx-background-color: linear-gradient(to bottom, #FFD900 85%, #E5C300 85%); -fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 5, 0, 0, 4);");
                    btn.setOnAction(this::handleStartLesson);
                } else if (lessonNum == currentLevel) {
                    btn.setStyle("-fx-background-radius: 50; -fx-min-width: 70; -fx-min-height: 70; -fx-background-color: linear-gradient(to bottom, #58CC02 85%, #46A302 85%); -fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 5, 0, 0, 4);");
                    btn.setOnAction(this::handleStartLesson);
                } else {
                    btn.setStyle("-fx-background-radius: 50; -fx-min-width: 70; -fx-min-height: 70; -fx-background-color: #E5E5E5; -fx-text-fill: #AFBFC9; -fx-font-size: 24px; -fx-font-weight: bold;");
                    btn.setDisable(true);
                }

                pathContainer.getChildren().add(btn);

                
                btn.setOpacity(0);
                FadeTransition ft = new FadeTransition(Duration.millis(300), btn);
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.setDelay(Duration.millis(100 * i));
                ft.play();
            }

            lessonsContainer.getChildren().add(pathContainer);
        }
    }

    private void showComingSoon(String message) {
        VBox comingSoonBox = new VBox();
        comingSoonBox.setAlignment(Pos.CENTER);
        comingSoonBox.setSpacing(15);
        comingSoonBox.setStyle("-fx-padding: 60;");

        Label emoji = new Label("📚");
        emoji.setStyle("-fx-font-size: 64px;");

        Label msg = new Label(message);
        msg.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: -text-secondary;");
        msg.setWrapText(true);

        Label hint = new Label("Contributors can add lessons from the Contributor Dashboard.");
        hint.setStyle("-fx-font-size: 14px; -fx-text-fill: -text-secondary;");
        hint.setWrapText(true);

        comingSoonBox.getChildren().addAll(emoji, msg, hint);
        lessonsContainer.getChildren().add(comingSoonBox);
    }

    @FXML
    private void handleStartLesson(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/virsa/view/active_lesson_view.fxml"));
            Parent root = loader.load();

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