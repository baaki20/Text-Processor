package com.dataflow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the main FXML view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dataflow/view/RegexAppView.fxml"));
        Parent root = loader.load();

        // Create and show the JavaFX scene
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/dataflow/view/style.css").toExternalForm());

        primaryStage.setTitle("Regex & Text Processing Tool");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}