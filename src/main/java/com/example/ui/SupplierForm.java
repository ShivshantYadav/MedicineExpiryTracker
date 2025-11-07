package com.example.ui;

import com.example.dao.SupplierDAO;
import com.example.models.Supplier;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SupplierForm extends Stage {

    private final Supplier supplier; // null = add, non-null = edit

    public SupplierForm() {
        this(null);
    }

    public SupplierForm(Supplier supplier) {
        this.supplier = supplier;

        setTitle(supplier == null ? "Add Supplier" : "Edit Supplier");
        initModality(Modality.APPLICATION_MODAL);

        // Layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(12);
        grid.setHgap(10);

        // Fields
        TextField tfName = new TextField();
        TextField tfContact = new TextField();
        TextField tfEmail = new TextField();
        TextArea taAddress = new TextArea();
        taAddress.setPrefRowCount(3);

        // Placeholders
        tfName.setPromptText("Supplier name");
        tfContact.setPromptText("e.g. +91 9876543210");
        tfEmail.setPromptText("e.g. supplier@example.com");
        taAddress.setPromptText("Enter supplier address...");

        // Character limits
        setTextFieldMaxLength(tfName, 100);
        setTextFieldMaxLength(tfContact, 20);
        setTextFieldMaxLength(tfEmail, 100);
        setTextAreaMaxLength(taAddress, 255);

        // Populate existing data if editing
        if (supplier != null) {
            tfName.setText(supplier.getName());
            tfContact.setText(supplier.getContact());
            tfEmail.setText(supplier.getEmail());
            taAddress.setText(supplier.getAddress());
        }

        // Labels + Fields
        grid.add(new Label("Name:"), 0, 0);
        grid.add(tfName, 1, 0);

        grid.add(new Label("Contact:"), 0, 1);
        grid.add(tfContact, 1, 1);

        grid.add(new Label("Email:"), 0, 2);
        grid.add(tfEmail, 1, 2);

        grid.add(new Label("Address:"), 0, 3);
        grid.add(taAddress, 1, 3);
        GridPane.setVgrow(taAddress, Priority.ALWAYS);

        // Buttons
        Button btnSave = new Button(supplier == null ? "💾 Save" : "💾 Update");
        Button btnCancel = new Button("❌ Cancel");
        btnSave.setDefaultButton(true);
        btnCancel.setCancelButton(true);

        HBox buttonBox = new HBox(12, btnCancel, btnSave);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        grid.add(buttonBox, 1, 4);

        // Save / Update Logic
        btnSave.setOnAction(e -> {
            String name = tfName.getText().trim();
            String contact = tfContact.getText().trim();
            String email = tfEmail.getText().trim();
            String address = taAddress.getText().trim();

            if (!validateInputs(name, contact, email)) return;

            try {
                if (supplier == null) {
                    Supplier newSupplier = new Supplier(name, contact, email, address);
                    int id = SupplierDAO.add(newSupplier);
                    showAlert(Alert.AlertType.INFORMATION, "Success",
                            "✅ Supplier added successfully!\nNew ID: " + id);
                    clearFields(tfName, tfContact, tfEmail, taAddress);
                } else {
                    supplier.setName(name);
                    supplier.setContact(contact);
                    supplier.setEmail(email);
                    supplier.setAddress(address);
                    SupplierDAO.update(supplier);
                    showAlert(Alert.AlertType.INFORMATION, "Updated",
                            "✅ Supplier details updated successfully!");
                    close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error",
                        "❌ Failed to save supplier: " + ex.getMessage());
            }
        });

        btnCancel.setOnAction(e -> close());

        Scene scene = new Scene(grid, 500, 320);
        setScene(scene);
    }

    // ---------------- UTILITY METHODS ----------------

    private boolean validateInputs(String name, String contact, String email) {
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Name is required!");
            return false;
        }

        if (!email.isEmpty() && !email.matches("^[\\w-.]+@[\\w-]+\\.[A-Za-z]{2,}$")) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Invalid email format!");
            return false;
        }

        if (!contact.isEmpty()) {
            String normalized = contact.replaceAll("[\\s-]", "");
            if (normalized.startsWith("+91")) {
                normalized = normalized.substring(3);
            }
            if (!normalized.matches("\\d{10}")) {
                showAlert(Alert.AlertType.WARNING, "Validation Error",
                        "Contact must be 10 digits (optional +91 allowed).");
                return false;
            }
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields(TextField name, TextField contact, TextField email, TextArea address) {
        name.clear();
        contact.clear();
        email.clear();
        address.clear();
        name.requestFocus();
    }

    private void setTextFieldMaxLength(TextField field, int maxLength) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > maxLength) field.setText(oldVal);
        });
    }

    private void setTextAreaMaxLength(TextArea field, int maxLength) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > maxLength) field.setText(oldVal);
        });
    }
}
