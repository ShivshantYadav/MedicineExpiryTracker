package com.example;

import com.example.ui.MainDashboard;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            MainDashboard dashboard = new MainDashboard(); // custom dashboard UI
            Scene scene = new Scene(dashboard.getRoot(), 900, 600);
            primaryStage.setTitle("Medicine Expiry Tracker");
            primaryStage.setScene(scene);
            primaryStage.show();

            // Start background expiry/stock monitoring threads
            dashboard.startBackgroundChecks();

            // Stop background threads safely on close
            primaryStage.setOnCloseRequest(event -> dashboard.stopBackgroundChecks());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
