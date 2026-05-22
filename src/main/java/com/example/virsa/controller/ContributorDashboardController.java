package com.example.virsa.controller;

import com.example.virsa.model.UserDAO;
import com.example.virsa.model.UserSession;
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
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ContributorDashboardController implements Initializable {

    @FXML private VBox unitsContainer;

    private String currentLang;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserSession session = UserSession.getInstance();
        currentLang = session.getCurrentLearningLanguage();
        if (currentLang == null) currentLang = "Punjabi";

        loadUnits();
    }

    private void loadUnits() {
        unitsContainer.getChildren().clear();

        UserDAO dao = new UserDAO();
        List<Integer> units = dao.getAvailableUnits(currentLang);

        
        int nextUnit = dao.getNextUnitNumber(currentLang);

        if (units.isEmpty()) {
            Label emptyLabel = new Label("No units yet. Create your first unit below!");
            emptyLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: -text-secondary; -fx-font-weight: bold; -fx-padding: 40;");
            unitsContainer.getChildren().add(emptyLabel);
        }

        for (int unitNum : units) {
            String unitName = dao.getUnitName(currentLang, unitNum);

            
            VBox unitHeader = new VBox();
            unitHeader.setAlignment(Pos.CENTER_LEFT);
            unitHeader.setSpacing(5);
            unitHeader.setStyle("-fx-background-color: linear-gradient(to right bottom, #1CB0F6, #1480B3); -fx-background-radius: 15; -fx-padding: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 5);");

            Label title = new Label("UNIT " + unitNum + " — " + unitName);
            title.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: 900; -fx-font-family: 'Arial Black';");

            Label subtitle = new Label("Click a lesson to edit, or add a new one below.");
            subtitle.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

            unitHeader.getChildren().addAll(title, subtitle);
            unitsContainer.getChildren().add(unitHeader);

            
            VBox pathContainer = new VBox();
            pathContainer.setAlignment(Pos.CENTER);
            pathContainer.setSpacing(25);

            List<Integer> lessons = dao.getAvailableLessons(currentLang, unitNum);
            int nextLessonNum = 1;

            for (int i = 0; i < lessons.size(); i++) {
                int lessonNum = lessons.get(i);
                Button btn = new Button(String.valueOf(lessonNum));
                btn.getStyleClass().add("lesson-chip");

                int translateX = (i % 2 == 1) ? 40 : -20;
                btn.setTranslateX(translateX);

                btn.setStyle("-fx-background-radius: 50; -fx-min-width: 70; -fx-min-height: 70; -fx-background-color: linear-gradient(to bottom, #1CB0F6 85%, #1480B3 85%); -fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 5, 0, 0, 4);");

                int finalLessonNum = lessonNum;
                int finalUnitNum = unitNum;
                btn.setOnAction(e -> openLessonBuilder(e, finalUnitNum, finalLessonNum));

                pathContainer.getChildren().add(btn);
                nextLessonNum = Math.max(nextLessonNum, lessonNum + 1);
            }

            unitsContainer.getChildren().add(pathContainer);

            
            int finalNextLesson = nextLessonNum;
            int finalUnit = unitNum;
            Button addLessonBtn = new Button("+ Add Lesson to Unit " + unitNum);
            addLessonBtn.setStyle("-fx-background-color: #58CC02; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 12 25; -fx-background-radius: 12; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 5, 0, 0, 4);");
            addLessonBtn.setOnAction(e -> openLessonBuilder(e, finalUnit, finalNextLesson));
            unitsContainer.getChildren().add(addLessonBtn);
        }

        
        Button addUnitBtn = new Button("+ Add New Unit");
        addUnitBtn.setStyle("-fx-background-color: #CE82FF; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-padding: 15 30; -fx-background-radius: 15; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 5, 0, 0, 4);");
        addUnitBtn.setOnAction(this::handleAddUnit);
        unitsContainer.getChildren().add(addUnitBtn);
    }

    private void openLessonBuilder(ActionEvent event, int unitNumber, int lessonNumber) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/virsa/view/lesson_builder_view.fxml"));
            Parent root = loader.load();

            LessonBuilderController controller = loader.getController();
            controller.setLessonNumber(lessonNumber);
            controller.setUnitNumber(unitNumber);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAddUnit(ActionEvent event) {
        UserDAO dao = new UserDAO();
        int nextUnit = dao.getNextUnitNumber(currentLang);

        
        String defaultName = (nextUnit == 1) ? "Introduction" : "";

        TextInputDialog dialog = new TextInputDialog(defaultName);
        dialog.setTitle("New Unit");
        dialog.setHeaderText("Create Unit " + nextUnit + " for " + currentLang);
        dialog.setContentText("Unit name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String unitName = result.get().trim();
            dao.createUnit(currentLang, nextUnit, unitName);
            loadUnits();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        UserSession.getInstance().logout();

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
