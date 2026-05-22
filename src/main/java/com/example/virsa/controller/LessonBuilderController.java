package com.example.virsa.controller;

import com.example.virsa.model.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;

public class LessonBuilderController {

    @FXML private ComboBox<String> languageComboBox;
    @FXML private TextField unitField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField promptField;
    @FXML private TextField correctField;
    @FXML private TextField wrong1Field;
    @FXML private TextField wrong2Field;
    @FXML private Label feedbackLabel;
    @FXML private Label audioHintLabel;
    @FXML private Label questionCountLabel;
    
    @FXML private Button audioFileButton;
    @FXML private Button finishButton;
    @FXML private TextArea correctArea;
    @FXML private VBox wrongOptionsContainer;

    private int currentLessonNumber = 1;
    private int questionCount = 0;
    private String absoluteAudioPath = null;

    public void setLessonNumber(int lessonNumber) {
        this.currentLessonNumber = lessonNumber;
    }

    public void setUnitNumber(int unitNumber) {
        unitField.setText(String.valueOf(unitNumber));
        unitField.setDisable(true);
    }

    @FXML
    public void initialize() {
        languageComboBox.getSelectionModel().selectFirst();
        typeComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleTypeChange() {
        String type = typeComboBox.getValue();
        
        audioHintLabel.setVisible(false);
        audioHintLabel.setManaged(false);
        audioFileButton.setVisible(false);
        audioFileButton.setManaged(false);
        promptField.setVisible(true);
        promptField.setManaged(true);
        correctField.setVisible(true);
        correctField.setManaged(true);
        correctArea.setVisible(false);
        correctArea.setManaged(false);
        wrongOptionsContainer.setVisible(true);
        wrongOptionsContainer.setManaged(true);

        if ("Audio".equals(type)) {
            promptField.setVisible(false);
            promptField.setManaged(false);
            audioHintLabel.setVisible(true);
            audioHintLabel.setManaged(true);
            audioFileButton.setVisible(true);
            audioFileButton.setManaged(true);
        } else if ("Paragraph".equals(type)) {
            wrongOptionsContainer.setVisible(false);
            wrongOptionsContainer.setManaged(false);
            correctField.setVisible(false);
            correctField.setManaged(false);
            correctArea.setVisible(true);
            correctArea.setManaged(true);
        }
    }
    
    @FXML
    private void handleChooseAudio() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Audio File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3")
        );
        java.io.File selectedFile = fileChooser.showOpenDialog(audioFileButton.getScene().getWindow());
        if (selectedFile != null) {
            absoluteAudioPath = selectedFile.getAbsolutePath();
            audioHintLabel.setText("Selected: " + selectedFile.getName());
        }
    }

    @FXML
    private void handleAddQuestion(ActionEvent event) {
        String lang = languageComboBox.getValue();
        String unitStr = unitField.getText().trim();
        String type = typeComboBox.getValue();
        
        String prompt = "Audio".equals(type) ? absoluteAudioPath : promptField.getText().trim();
        String correct = "Paragraph".equals(type) ? correctArea.getText().trim() : correctField.getText().trim();
        String wrong1 = "Paragraph".equals(type) ? "" : wrong1Field.getText().trim();
        String wrong2 = "Paragraph".equals(type) ? "" : wrong2Field.getText().trim();

        if (lang == null || type == null || unitStr.isEmpty() || prompt == null || prompt.isEmpty() || correct.isEmpty()) {
            feedbackLabel.setText("Please fill out all required fields!");
            feedbackLabel.setStyle("-fx-text-fill: #FF4B4B;");
            return;
        }
        
        if (!"Paragraph".equals(type) && (wrong1.isEmpty() || wrong2.isEmpty())) {
            feedbackLabel.setText("Please provide two wrong options!");
            feedbackLabel.setStyle("-fx-text-fill: #FF4B4B;");
            return;
        }

        int unit;
        try {
            unit = Integer.parseInt(unitStr);
        } catch (NumberFormatException e) {
            feedbackLabel.setText("Unit must be a valid number!");
            feedbackLabel.setStyle("-fx-text-fill: #FF4B4B;");
            return;
        }

        UserDAO dao = new UserDAO();
        boolean success = dao.insertQuestion(lang, unit, currentLessonNumber, type, prompt, correct, wrong1, wrong2);

        if (success) {
            questionCount++;
            questionCountLabel.setText("Questions added: " + questionCount + (questionCount < 3 ? " (need " + (3 - questionCount) + " more)" : " ✓"));
            feedbackLabel.setText("Question " + questionCount + " saved!");
            feedbackLabel.setStyle("-fx-text-fill: #58CC02;");
            clearFields();

            
            languageComboBox.setDisable(true);
            unitField.setDisable(true);

            
            if (questionCount >= 3) {
                finishButton.setDisable(false);
                finishButton.setStyle("-fx-background-color: #58CC02; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 15; -fx-background-radius: 15; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 5, 0, 0, 4);");
            }
        } else {
            feedbackLabel.setText("Database error. Could not save.");
            feedbackLabel.setStyle("-fx-text-fill: #FF4B4B;");
        }
    }

    @FXML
    private void handleFinishLesson(ActionEvent event) {
        if (questionCount < 3) {
            feedbackLabel.setText("You need at least 3 questions to finish!");
            feedbackLabel.setStyle("-fx-text-fill: #FF4B4B;");
            return;
        }
        handleBack(event);
    }

    private void clearFields() {
        promptField.clear();
        correctField.clear();
        correctArea.clear();
        wrong1Field.clear();
        wrong2Field.clear();
        absoluteAudioPath = null;
        audioHintLabel.setText("Selected File: None");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/virsa/view/contributor_dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
