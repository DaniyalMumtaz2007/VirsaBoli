package com.example.virsa.controller;

import com.example.virsa.model.UserDAO;
import com.example.virsa.model.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button learnerToggle;
    @FXML private Button contribToggle;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    private boolean isLearnerMode = true;

    @FXML
    private void handleToggleLearner() {
        isLearnerMode = true;
        learnerToggle.setStyle("-fx-background-color: #58CC02; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 30; -fx-background-radius: 10; -fx-cursor: hand;");
        contribToggle.setStyle("-fx-background-color: transparent; -fx-text-fill: #AFBFC9; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 30; -fx-background-radius: 10; -fx-cursor: hand;");
        loginButton.setStyle("-fx-background-color: #58CC02; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 15; -fx-background-radius: 15; -fx-cursor: hand;");
    }

    @FXML
    private void handleToggleContributor() {
        isLearnerMode = false;
        contribToggle.setStyle("-fx-background-color: #1CB0F6; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 30; -fx-background-radius: 10; -fx-cursor: hand;");
        learnerToggle.setStyle("-fx-background-color: transparent; -fx-text-fill: #AFBFC9; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 30; -fx-background-radius: 10; -fx-cursor: hand;");
        loginButton.setStyle("-fx-background-color: #1CB0F6; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 15; -fx-background-radius: 15; -fx-cursor: hand;");
    }

    @FXML
    private void handleGoToSignUp(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/virsa/view/signup_view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("System error loading signup screen.");
        }
    }
    
    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String pass = passwordField.getText();

        if (email == null || email.isBlank() || pass == null || pass.isBlank()) {
            errorLabel.setText("Please enter your email and password.");
            return;
        }

        UserDAO userDAO = new UserDAO();
        UserSession session = userDAO.authenticate(email, pass);

        if (session != null) {
            if (isLearnerMode) {
                if (session.isLearner()) {
                    loadDashboard(event, "/com/example/virsa/view/main_dashboard.fxml");
                } else {
                    errorLabel.setText("You do not have a Learner account!");
                }
            } else {
                if (session.isContributor()) {
                    loadDashboard(event, "/com/example/virsa/view/contributor_dashboard.fxml");
                } else {
                    errorLabel.setText("You do not have Contributor access!");
                }
            }
        } else {
            errorLabel.setText("Invalid email or password.");
        }
    }

    private void loadDashboard(ActionEvent event, String viewFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(viewFile));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("System error loading dashboard.");
        }
    }
}