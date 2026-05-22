package com.example.virsa.controller;

import com.example.virsa.model.UserSession;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import com.example.virsa.model.UserDAO;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SignupController {

    @FXML private VBox step1Pane, step2Pane, step3Pane;
    @FXML private Label titleLabel, errorLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Button btnBack, btnNext;

    
    @FXML private TextField emailField;
    @FXML private TextField nameField;
    @FXML private PasswordField passField, confirmPassField;
    @FXML private DatePicker dobPicker;
    @FXML private CheckBox contributorCheckBox;

    
    @FXML private ComboBox<String> languageComboBox;

    
    @FXML private RadioButton reasonFamily, reasonTravel, reasonSchool, reasonBrain;
    private ToggleGroup reasonGroup;

    private int currentStep = 1;

    @FXML
    public void initialize() {
        
        languageComboBox.setItems(FXCollections.observableArrayList("Punjabi", "Sindhi", "Pashto", "Balochi"));

        
        reasonGroup = new ToggleGroup();
        reasonFamily.setToggleGroup(reasonGroup);
        reasonTravel.setToggleGroup(reasonGroup);
        reasonSchool.setToggleGroup(reasonGroup);
        reasonBrain.setToggleGroup(reasonGroup);
    }

    @FXML
    private void handleNext(ActionEvent event) {
        errorLabel.setText(""); 

        if (currentStep == 1) {
            
            if (emailField.getText().isBlank() || nameField.getText().isBlank() || passField.getText().isBlank()) {
                errorLabel.setText("Please fill in all fields.");
                return;
            }
            if (!emailField.getText().contains("@")) {
                errorLabel.setText("Please enter a valid email.");
                return;
            }
            if (!passField.getText().equals(confirmPassField.getText())) {
                errorLabel.setText("Passwords do not match.");
                return;
            }
            if (dobPicker.getValue() == null) {
                errorLabel.setText("Please enter your Date of Birth.");
                return;
            }
            
            currentStep = 2;
            updateUI();
        }
        else if (currentStep == 2) {
            
            if (languageComboBox.getValue() == null) {
                errorLabel.setText("Please select a language.");
                return;
            }
            
            
            if (contributorCheckBox.isSelected()) {
                completeRegistration(event, "Contributor");
                return;
            }

            
            currentStep = 3;
            updateUI();
        }
        else if (currentStep == 3) {
            
            if (reasonGroup.getSelectedToggle() == null) {
                errorLabel.setText("Please select a reason.");
                return;
            }

            completeRegistration(event, "Learner");
        }
    }

    @FXML
    private void handleBack() {
        errorLabel.setText("");
        if (currentStep > 1) {
            currentStep--;
            updateUI();
        }
    }

    private void updateUI() {
        
        step1Pane.setVisible(false);
        step2Pane.setVisible(false);
        step3Pane.setVisible(false);

        
        switch (currentStep) {
            case 1:
                step1Pane.setVisible(true);
                titleLabel.setText("Create your profile");
                progressBar.setProgress(0.33);
                btnBack.setVisible(false);
                btnNext.setText("NEXT STEP");
                break;
            case 2:
                step2Pane.setVisible(true);
                titleLabel.setText("Choose a course");
                progressBar.setProgress(0.66);
                btnBack.setVisible(true);
                btnNext.setText("NEXT STEP");
                break;
            case 3:
                step3Pane.setVisible(true);
                titleLabel.setText("Set your goals");
                progressBar.setProgress(1.0);
                btnBack.setVisible(true);
                btnNext.setText("FINISH & START LEARNING");
                break;
        }
    }



    private void completeRegistration(ActionEvent event, String dummyGoal) {
        String email = emailField.getText().trim();
        String fullName = nameField.getText().trim();
        String password = passField.getText();
        String dob = dobPicker.getValue().toString();
        String selectedLang = languageComboBox.getValue();
        
        boolean isContributor = contributorCheckBox.isSelected();

        UserDAO userDAO = new UserDAO();

        boolean isRegistered = userDAO.registerUser(email, fullName, password, fullName, dob, isContributor);

        if (isRegistered) {
            UserSession session = userDAO.authenticate(email, password);

            if (session != null) {
                userDAO.addLanguage(session.getUserId(), selectedLang);
                session.addLanguage(selectedLang);
                session.setCurrentLearningLanguage(selectedLang);

                
                try {
                    String viewFile = session.isContributor() ? "/com/example/virsa/view/contributor_dashboard.fxml" : "/com/example/virsa/view/main_dashboard.fxml";
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(viewFile));
                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root, 1000, 700));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            errorLabel.setText("That email is already registered. Try another!");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/virsa/view/login_view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}