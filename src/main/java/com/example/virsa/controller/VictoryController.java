package com.example.virsa.controller;

import com.example.virsa.model.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class VictoryController {

    @FXML private Label xpLabel;
    @FXML private Label streakLabel;
    @FXML private Label celebrationLabel;
    @FXML private Button btnContinue;

    private int xpEarned = 0;
    private boolean streakIncreased = false;

    
    public void setVictoryData(int xpEarned, boolean streakIncreased) {
        this.xpEarned = xpEarned;
        this.streakIncreased = streakIncreased;
        
        xpLabel.setText("+" + xpEarned);
        streakLabel.setText(String.valueOf(UserSession.getInstance().getStreak()));
        
        if (streakIncreased) {
            celebrationLabel.setVisible(true);
            celebrationLabel.setText("You're on fire! Streak increased!");
        }
    }

    @FXML
    private void handleContinue(ActionEvent event) {
        try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/virsa/view/main_dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            
            root.getStylesheets().clear();
            String themeFile = UserSession.getInstance().isDarkMode() ? "dark.css" : "light.css";
            root.getStylesheets().add(getClass().getResource("/com/example/virsa/styles/" + themeFile).toExternalForm());

            stage.setScene(new Scene(root, 1000, 700));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
