package com.example.virsa.controller;
import com.example.virsa.model.*;
import com.example.virsa.util.SoundManager;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class ActiveLessonController implements Initializable {

    @FXML private ProgressBar lessonProgressBar;
    @FXML private Label heartsLabel, questionPromptLabel;
    @FXML private FlowPane optionsContainer;
    @FXML private TextArea paragraphInputField;

    @FXML private VBox bottomBanner, feedbackTextContainer;
    @FXML private Label feedbackTitle, feedbackSubtitle;
    @FXML private Button mainActionButton;
    @FXML private Button audioPlayButton;

    
    private List<Question> currentLesson;
    private int currentQuestionIndex = 0;
    private String selectedAnswer = null;
    private boolean isCheckingAnswer = true;
    @FXML private Label questionTypeLabel; 

    private int xpEarnedThisLesson = 0;
    private int totalOriginalQuestions = 0;
    private boolean isPracticeMode = false;

    public void setPracticeMode(boolean practiceMode) {
        this.isPracticeMode = practiceMode;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateHeartsUI();
        loadDatabaseData();
        loadQuestion();
    }

    private void loadDatabaseData() {
        String currentLang = UserSession.getInstance().getCurrentLearningLanguage();
        if (currentLang == null) currentLang = "Punjabi";

        UserDAO dao = new UserDAO();
        currentLesson = dao.getLessonQuestions(currentLang, 1);
        totalOriginalQuestions = currentLesson.size();
    }

    private void updateHeartsUI() {
        heartsLabel.setText(String.valueOf(UserSession.getInstance().getHearts()));
    }
    private void loadQuestion() {
        
        if (currentQuestionIndex >= currentLesson.size()) {
            finishLesson();
            return;
        }

        
        if (UserSession.getInstance().getHearts() <= 0 && !isPracticeMode) {
            triggerHeartLockout();
            return;
        }

        
        if (questionTypeLabel != null) {
            questionTypeLabel.setVisible(true);
            questionTypeLabel.setText("Translate this word");
        }
        selectedAnswer = null;
        isCheckingAnswer = true;
        mainActionButton.setText("CHECK");
        disableCheckButton();

        feedbackTextContainer.setVisible(false);
        feedbackTextContainer.setManaged(false);
        bottomBanner.setStyle("-fx-padding: 30 40 30 40; -fx-border-color: -border-color; -fx-border-width: 2 0 0 0; -fx-background-color: transparent;");

        
        double progress = (double) currentQuestionIndex / totalOriginalQuestions;
        lessonProgressBar.setProgress(Math.min(progress, 1.0));

        
        Question currentQ = currentLesson.get(currentQuestionIndex);

        
        if (currentQ instanceof AudioQuestion) {
            questionPromptLabel.setText("Translate what you hear");
            audioPlayButton.setVisible(true);
            audioPlayButton.setManaged(true);
            if (questionTypeLabel != null) {
                questionTypeLabel.setText("Listen carefully");
            }
            SoundManager.playSound(((AudioQuestion) currentQ).getAudioFilePath());
        } else {
            questionPromptLabel.setText(currentQ.getPrompt());
            audioPlayButton.setVisible(false);
            audioPlayButton.setManaged(false);
        }

        
        optionsContainer.setVisible(true);
        optionsContainer.setManaged(true);
        if (paragraphInputField != null) {
            paragraphInputField.setVisible(false);
            paragraphInputField.setManaged(false);
            paragraphInputField.clear();
        }

        if (currentQ instanceof ParagraphQuestion) {
            optionsContainer.setVisible(false);
            optionsContainer.setManaged(false);
            if (paragraphInputField != null) {
                paragraphInputField.setVisible(true);
                paragraphInputField.setManaged(true);
                
                
                paragraphInputField.textProperty().addListener((obs, oldV, newV) -> {
                    if (newV != null && !newV.trim().isEmpty()) {
                        selectedAnswer = newV.trim();
                        enableCheckButton();
                    } else {
                        selectedAnswer = null;
                        disableCheckButton();
                    }
                });
            }
            return;
        }

        List<String> options = new ArrayList<>();
        options.add(currentQ.getCorrectAnswer()); 

        if (currentQ.getWrongOption1() != null && !currentQ.getWrongOption1().trim().isEmpty()) {
            options.add(currentQ.getWrongOption1());
        }
        if (currentQ.getWrongOption2() != null && !currentQ.getWrongOption2().trim().isEmpty()) {
            options.add(currentQ.getWrongOption2());
        }

        Collections.shuffle(options);

        optionsContainer.getChildren().clear();
        
        for (String opt : options) {
            Button optBtn = new Button(opt);
            optBtn.setStyle("-fx-background-color: transparent; -fx-border-color: -border-color; -fx-border-width: 2; -fx-text-fill: -text-primary; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 15 30; -fx-background-radius: 15; -fx-border-radius: 15; -fx-cursor: hand;");
            optBtn.setOnAction(e -> handleOptionSelected(optBtn));
            optionsContainer.getChildren().add(optBtn);
        }

        
        animateQuestionIn(questionPromptLabel);
    }

    private void animateQuestionIn(Node target) {
        target.setTranslateX(60);
        target.setOpacity(0);
        TranslateTransition slide = new TranslateTransition(Duration.millis(300), target);
        slide.setFromX(60);
        slide.setToX(0);
        FadeTransition fade = new FadeTransition(Duration.millis(300), target);
        fade.setFromValue(0);
        fade.setToValue(1);
        slide.play();
        fade.play();
    }

    private void handleOptionSelected(Button clickedButton) {
        for (Node node : optionsContainer.getChildren()) {
            node.setStyle("-fx-background-color: transparent; -fx-border-color: -border-color; -fx-border-width: 2; -fx-text-fill: -text-primary; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 15 30; -fx-background-radius: 15; -fx-border-radius: 15; -fx-cursor: hand;");
        }
        clickedButton.setStyle("-fx-background-color: #DDF4FF; -fx-border-color: #1CB0F6; -fx-border-width: 2; -fx-text-fill: #1CB0F6; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 15 30; -fx-background-radius: 15; -fx-border-radius: 15; -fx-cursor: hand;");
        selectedAnswer = clickedButton.getText();
        enableCheckButton();
    }

    private void enableCheckButton() {
        mainActionButton.setStyle("-fx-background-color: #58CC02; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 15 30; -fx-background-radius: 15; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 5, 0, 0, 4);");
    }

    private void disableCheckButton() {
        mainActionButton.setStyle("-fx-background-color: #E5E5E5; -fx-text-fill: #AFBFC9; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 15 30; -fx-background-radius: 15; -fx-cursor: default;");
    }

    @FXML
    private void handleMainAction(ActionEvent event) {
        if (UserSession.getInstance().getHearts() <= 0 && !isPracticeMode) {
            handleClose(event); 
            return;
        }

        if (selectedAnswer == null) return;

        if (isCheckingAnswer) {
            checkAnswer();
        } else {
            currentQuestionIndex++;
            loadQuestion();
        }
    }

    private void checkAnswer() {
        isCheckingAnswer = false;
        mainActionButton.setText("CONTINUE");
        feedbackTextContainer.setVisible(true);
        feedbackTextContainer.setManaged(true);

        
        ScaleTransition bounce = new ScaleTransition(Duration.millis(200), bottomBanner);
        bounce.setFromX(0.95);
        bounce.setFromY(0.95);
        bounce.setToX(1.0);
        bounce.setToY(1.0);
        bounce.play();

        Question currentQ = currentLesson.get(currentQuestionIndex);

        if (currentQ.checkAnswer(selectedAnswer)) {
            
            bottomBanner.setStyle("-fx-background-color: #D7FFB8; -fx-padding: 30 40 30 40;");
            feedbackTitle.setText("Correct!");
            feedbackTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: #58CC02;");
            feedbackSubtitle.setText("Great job.");
            feedbackSubtitle.setStyle("-fx-text-fill: #46A302; -fx-font-weight: bold;");

            
            if (currentQuestionIndex < totalOriginalQuestions) {
                xpEarnedThisLesson += 5;
            } else {
                xpEarnedThisLesson += 2;
            }

        } else {
            
            UserSession session = UserSession.getInstance();
            
            
            if (!isPracticeMode) {
                session.decrementHeart();
                updateHeartsUI();
                UserDAO dao = new UserDAO();
                dao.saveUserProgress(session);
            }

            
            currentLesson.add(currentQ);

            bottomBanner.setStyle("-fx-background-color: #FFDFE0; -fx-padding: 30 40 30 40;");
            feedbackTitle.setText("Incorrect");
            feedbackTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: #EA2B2B;");
            feedbackSubtitle.setText("Correct answer: " + currentQ.getCorrectAnswer());
            feedbackSubtitle.setStyle("-fx-text-fill: #EA2B2B; -fx-font-weight: bold;");
            mainActionButton.setStyle("-fx-background-color: #FF4B4B; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 15 30; -fx-background-radius: 15; -fx-cursor: hand;");

            if (session.getHearts() <= 0) {
                mainActionButton.setText("EXIT LESSON");
            }
        }
    }

    private void triggerHeartLockout() {
        
        questionTypeLabel.setVisible(false);

        questionPromptLabel.setText("Out of Hearts!");
        optionsContainer.getChildren().clear();

        Label warning = new Label("You need to wait for your hearts to refill before continuing.");
        warning.setStyle("-fx-font-size: 16px; -fx-text-fill: -text-secondary;");
        optionsContainer.getChildren().add(warning);

        disableCheckButton();
        mainActionButton.setText("EXIT LESSON");
        isCheckingAnswer = false;
    }

    private void finishLesson() {
        UserSession session = UserSession.getInstance();
        boolean streakIncreased = false;

        if (isPracticeMode) {
            
            xpEarnedThisLesson = 5;
            session.addXp(xpEarnedThisLesson);
            session.setHearts(Math.min(5, session.getHearts() + 1));
        } else {
            
            session.addXp(xpEarnedThisLesson);
            session.incrementStreak();
            streakIncreased = true;

            if (session.getCurrentLessonLevel() < 3 && session.getCurrentLearningLanguage() != null) {
                UserDAO dao = new UserDAO();
                dao.incrementLessonLevel(session.getUserId(), session.getCurrentLearningLanguage());
                session.setCurrentLessonLevel(session.getCurrentLessonLevel() + 1);
            }
        }

        UserDAO dao = new UserDAO();
        dao.saveUserProgress(session);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/virsa/view/victory_view.fxml"));
            Parent root = loader.load();

            VictoryController victoryController = loader.getController();
            victoryController.setVictoryData(xpEarnedThisLesson, streakIncreased);

            Stage stage = (Stage) mainActionButton.getScene().getWindow();

            
            root.getStylesheets().clear();
            String themeFile = UserSession.getInstance().isDarkMode() ? "dark.css" : "light.css";
            root.getStylesheets().add(getClass().getResource("/com/example/virsa/styles/" + themeFile).toExternalForm());

            stage.setScene(new Scene(root, 1000, 700));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose(ActionEvent event) {
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

    @FXML
    public void handlePlayAudio(ActionEvent actionEvent) {
        Question currentQ = currentLesson.get(currentQuestionIndex);
        if (currentQ instanceof AudioQuestion) {
            SoundManager.playSound(((AudioQuestion) currentQ).getAudioFilePath());
        }
    }
}