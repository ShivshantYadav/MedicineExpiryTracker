package com.example.ui;

import com.example.dao.SupplierDAO;
import com.example.models.Supplier;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public class SupplierForm extends Stage {

    private Supplier supplier; // null = add, non-null = edit

    public SupplierForm() {
        this(null); // Add mode
    }

    public SupplierForm(Supplier supplier) {
        this.supplier = supplier;
        setTitle(supplier == null ? "Add Supplier" : "Edit Supplier");
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

        // Max lengths
        setTextFieldMaxLength(tfName, 100);
        setTextFieldMaxLength(tfContact, 15);
        setTextFieldMaxLength(tfEmail, 100);
        setTextAreaMaxLength(taAddress, 255);

        // Populate fields if editing
        if (supplier != null) {
            tfName.setText(supplier.getName());
            tfContact.setText(supplier.getContact());
            tfEmail.setText(supplier.getEmail());
            taAddress.setText(supplier.getAddress());
        }

        // Add labels and fields
        grid.add(new Label("Name:"), 0, 0);
        grid.add(tfName, 1, 0);

        grid.add(new Label("Contact:"), 0, 1);
        grid.add(tfContact, 1, 1);

        grid.add(new Label("Email:"), 0, 2);
        grid.add(tfEmail, 1, 2);

        grid.add(new Label("Address:"), 0, 3);
        grid.add(taAddress, 1, 3);

        // Buttons
        Button btnSave = new Button("💾 " + (supplier == null ? "Save" : "Update"));
        Button btnCancel = new Button("❌ Cancel");
        btnSave.setDefaultButton(true);
        btnCancel.setCancelButton(true);

        grid.add(btnSave, 1, 4);
        grid.add(btnCancel, 0, 4);

        // Save/Update handler
        btnSave.setOnAction(e -> {
            String name = tfName.getText().trim();
            String contact = tfContact.getText().trim();
            String email = tfEmail.getText().trim();
            String address = taAddress.getText().trim();

            // Validation
            if (name.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Name is required!");
                return;
            }

            if (!email.isEmpty() && !email.matches("^\\S+@\\S+\\.\\S+$")) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Invalid email format!");
                return;
            }

            if (!contact.isEmpty() && !contact.matches("\\d{10}")) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Contact must be 10 digits!");
                return;
            }

            if (supplier == null) {
                // Add mode
                Supplier newSupplier = new Supplier(name, contact, email, address);
                int id = SupplierDAO.add(newSupplier);
                if (id > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "✅ Supplier added successfully!");
                    clearFields(tfName, tfContact, tfEmail, taAddress);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "❌ Failed to add supplier. Check console logs.");
                }
            } else {
                // Edit mode
                supplier.setName(name);
                supplier.setContact(contact);
                supplier.setEmail(email);
                supplier.setAddress(address);

                if (SupplierDAO.update(supplier)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "✅ Supplier updated successfully!");
                    close();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "❌ Failed to update supplier. Check console logs.");
                }
            }
        });

        // Cancel button
        btnCancel.setOnAction(e -> close());

        // Set scene
        Scene scene = new Scene(grid, 450, 300);
        setScene(scene);
    }

    // Utility to show alerts
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Clear fields after adding
    private void clearFields(TextField name, TextField contact, TextField email, TextArea address) {
        name.clear();
        contact.clear();
        email.clear();
        address.clear();
        name.requestFocus();
    }

    // Limit TextField input
    private void setTextFieldMaxLength(TextField textField, int maxLength) {
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > maxLength) textField.setText(oldVal);
        });
    }

    // Limit TextArea input
    private void setTextAreaMaxLength(TextArea textArea, int maxLength) {
        textArea.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > maxLength) textArea.setText(oldVal);
        });
    }
}
