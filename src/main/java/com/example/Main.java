package com.example;

import com.example.dao.DBInitializer;
import com.example.ui.MainDashboard;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the Medicine Expiry Tracker application.
 * Ensures database setup before launching the main dashboard UI.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // ✅ Step 1: Initialize database and ensure tables exist
            DBInitializer.initialize();

            // ✅ Step 2: Launch the main dashboard UI
            MainDashboard dashboard = new MainDashboard();
            Scene scene = new Scene(dashboard.getRoot(), 900, 600);
            primaryStage.setTitle("💊 Medicine Expiry Tracker");
            primaryStage.setScene(scene);
            primaryStage.show();

            // ✅ Step 3: Start background expiry/stock monitoring threads
            dashboard.startBackgroundChecks();

            // ✅ Step 4: Gracefully stop background threads on exit
            primaryStage.setOnCloseRequest(event -> dashboard.stopBackgroundChecks());

        } catch (Exception e) {
            System.err.println("⚠️ Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
