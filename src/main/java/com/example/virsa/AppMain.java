package com.example.virsa;

import com.example.virsa.model.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class AppMain extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DatabaseManager.initializeDatabase();
        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("/com/example/virsa/view/login_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        stage.setTitle("VirsaBoli");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}