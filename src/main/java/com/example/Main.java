package com.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.ui.MainDashboard;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            MainDashboard dashboard = new MainDashboard();
            Scene scene = new Scene(dashboard.getRoot(), 900, 600);
            primaryStage.setTitle("Medicine Expiry Tracker");
            primaryStage.setScene(scene);
            primaryStage.show();

            // start background daily checks (for demo runs every minute)
            dashboard.startBackgroundChecks();

            // stop background checks on close
            primaryStage.setOnCloseRequest(event -> dashboard.stopBackgroundChecks());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
