package com.example.virsa.controller;

import com.example.virsa.model.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LanguagesController implements Initializable {

    @FXML private FlowPane languagesFlowPane;
    @FXML private VBox addCourseTile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshLanguagesView();
    }

    public void refreshLanguagesView() {
        
        languagesFlowPane.getChildren().clear();

        
        UserSession session = UserSession.getInstance();
        for (String lang : session.getActiveLanguages()) {
            boolean isCurrent = lang.equals(session.getCurrentLearningLanguage());
            addLanguageTile(lang, session.getColorForLanguage(lang), isCurrent);
        }

        
        languagesFlowPane.getChildren().add(addCourseTile);
    }

    private void addLanguageTile(String languageName, String colorHex, boolean isCurrent) {
        VBox newTile = new VBox();
        newTile.setAlignment(javafx.geometry.Pos.CENTER);
        newTile.setSpacing(15.0);
        newTile.setPrefHeight(160.0);
        newTile.setPrefWidth(140.0);

        
        if (isCurrent) {
            newTile.setStyle("-fx-background-color: #F0FFEB; -fx-border-color: #58CC02; -fx-border-width: 4; -fx-border-radius: 15; -fx-background-radius: 15; -fx-cursor: hand;");
        } else {
            newTile.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5); -fx-cursor: hand;");
        }

        Circle circle = new Circle(25.0, Color.web(colorHex));
        Label nameLabel = new Label(languageName);
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4B4B4B;");

        newTile.getChildren().addAll(circle, nameLabel);

        
        newTile.setOnMouseClicked(event -> {
            UserSession session = UserSession.getInstance();
            session.setCurrentLearningLanguage(languageName);
            
            
            com.example.virsa.model.UserDAO dao = new com.example.virsa.model.UserDAO();
            int currentLevel = dao.getCurrentLessonLevel(session.getUserId(), languageName);
            session.setCurrentLessonLevel(currentLevel);
            
            refreshLanguagesView(); 
        });

        languagesFlowPane.getChildren().add(newTile);
    }

    @FXML
    private void handleAddLanguageClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/virsa/view/add_language_view.fxml"));
            Parent root = loader.load();

            AddLanguageController addController = loader.getController();
            addController.setParentController(this);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Add Course");
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}