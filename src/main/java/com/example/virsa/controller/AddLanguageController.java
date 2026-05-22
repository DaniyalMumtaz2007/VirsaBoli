package com.example.virsa.controller;

import com.example.virsa.model.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AddLanguageController {

    private LanguagesController parentController;

    
    public void setParentController(LanguagesController parentController) {
        this.parentController = parentController;
    }

    @FXML private void startPunjabi(ActionEvent event) { addAndClose(event, "Punjabi", "#FF9600"); }
    @FXML private void startSindhi(ActionEvent event) { addAndClose(event, "Sindhi", "#1CB0F6"); }
    @FXML private void startPashto(ActionEvent event) { addAndClose(event, "Pashto", "#CE82FF"); }
    @FXML private void startBalochi(ActionEvent event) { addAndClose(event, "Balochi", "#FF4B4B"); }

    private void addAndClose(ActionEvent event, String langName, String colorHex) {
        
        UserSession session = UserSession.getInstance();
        session.addLanguage(langName);
        session.setCurrentLearningLanguage(langName); 

        
        if (parentController != null) {
            parentController.refreshLanguagesView();
        }

        
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }
}