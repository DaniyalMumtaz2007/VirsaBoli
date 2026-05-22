package com.example.virsa.controller;

import com.example.virsa.model.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import java.io.IOException;

public class MainDashboardController {

    @FXML private Label topLangLabel;
    @FXML private Label topStreakLabel;
    @FXML private Label topXpLabel;
    @FXML private Label topHeartsLabel;
    @FXML private BorderPane mainContainer;
    @FXML private AnchorPane contentArea;
    @FXML private Button btnLessons, btnLanguages, btnProfile, btnPractice, btnSettings;

    private Button activeScanner;

    public void refreshTopBar() {
        UserSession session = UserSession.getInstance();

        topStreakLabel.setText(String.valueOf(session.getStreak()));
        topXpLabel.setText(String.valueOf(session.getXp()));
        topHeartsLabel.setText(String.valueOf(session.getHearts()));

        String currentLang = session.getCurrentLearningLanguage();
        if (currentLang != null) {
            topLangLabel.setText("Learning: " + currentLang);
        } else {
            topLangLabel.setText("Pick a Course!");
        }
    }

    @FXML
    public void initialize() {
        refreshTopBar(); 
        loadView("lessons_view.fxml", btnLessons);
    }
    @FXML
    private void handleNavLessons() {
        loadView("lessons_view.fxml", btnLessons);
    }

    @FXML
    private void handleNavLanguages() {
        loadView("languages_view.fxml", btnLanguages);
    }

    @FXML
    private void handleNavProfile() {
        loadView("profile_view.fxml", btnProfile);
    }
    
    @FXML
    private void handleNavPractice() {
        loadView("practice_dashboard.fxml", btnPractice);
    }



    @FXML
    private void handleNavSettings() {
        loadView("settings_view.fxml", btnSettings);
    }

    private void loadView(String fxmlFile, Button clickedButton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/virsa/view/" + fxmlFile));
            Node view = loader.load();
            
            
            updateActiveButton(clickedButton);
            
            
            view.setOpacity(0);
            contentArea.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

            FadeTransition ft = new FadeTransition(Duration.millis(300), view);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateActiveButton(Button button) {
        if (activeScanner != null) {
            activeScanner.getStyleClass().remove("nav-button-active");
        }
        button.getStyleClass().add("nav-button-active");
        activeScanner = button;
    }
}
