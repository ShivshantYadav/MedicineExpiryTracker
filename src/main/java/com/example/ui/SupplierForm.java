package com.example.ui;

import com.example.models.Supplier;
import com.example.dao.SupplierDAO;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SupplierForm extends Stage {

    public SupplierForm() {
        setTitle("Add Supplier");
        initModality(Modality.APPLICATION_MODAL);

        // Create grid layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Form fields
        TextField tfName = new TextField();
        TextField tfContact = new TextField();
        TextField tfEmail = new TextField();
        TextArea taAddress = new TextArea();
        taAddress.setPrefRowCount(3);

        // Add labels and fields
        grid.add(new Label("Name:"), 0, 0);
        grid.add(tfName, 1, 0);

        grid.add(new Label("Contact:"), 0, 1);
        grid.add(tfContact, 1, 1);

        grid.add(new Label("Email:"), 0, 2);
        grid.add(tfEmail, 1, 2);

        grid.add(new Label("Address:"), 0, 3);
        grid.add(taAddress, 1, 3);

        // Save button
        Button btnSave = new Button("💾 Save");
        btnSave.setDefaultButton(true);
        grid.add(btnSave, 1, 4);

        // Handle Save button click
        btnSave.setOnAction(e -> {
            String name = tfName.getText().trim();
            String contact = tfContact.getText().trim();
            String email = tfEmail.getText().trim();
            String address = taAddress.getText().trim();

            // Basic validation
            if (name.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Name is required!");
                return;
            }

            Supplier supplier = new Supplier(name, contact, email, address);
            int id = SupplierDAO.add(supplier);

            if (id > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "✅ Supplier added successfully!");
                close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "❌ Failed to add supplier. Check console logs.");
            }
        });

        // Set scene
        Scene scene = new Scene(grid, 400, 300);
        setScene(scene);
    }

    // Utility for showing alerts
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
