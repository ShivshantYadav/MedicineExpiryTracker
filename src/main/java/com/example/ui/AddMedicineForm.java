package com.example.ui;

import com.example.models.Medicine;
import com.example.dao.MedicineDAO;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class AddMedicineForm extends Stage {

    private static final String BARCODE_FOLDER = System.getProperty("user.home") + "/MedicineExpiryTracker/barcode";

    public AddMedicineForm() {
        setTitle("Add Medicine");
        initModality(Modality.APPLICATION_MODAL);

        // --- Grid Setup ---
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        // --- Fields ---
        TextField tfName = new TextField();
        TextField tfBatch = new TextField();
        DatePicker dpExpiry = new DatePicker(LocalDate.now().plusMonths(6));
        TextField tfQty = new TextField("0");
        TextField tfMin = new TextField("0");
        TextField tfBarcode = new TextField();

        // QR ImageView
        ImageView qrView = new ImageView();
        qrView.setFitWidth(200);
        qrView.setFitHeight(200);

        // --- Labels & Fields ---
        grid.add(new Label("Name:"), 0, 0);
        grid.add(tfName, 1, 0);
        grid.add(new Label("Batch:"), 0, 1);
        grid.add(tfBatch, 1, 1);
        grid.add(new Label("Expiry:"), 0, 2);
        grid.add(dpExpiry, 1, 2);
        grid.add(new Label("Quantity:"), 0, 3);
        grid.add(tfQty, 1, 3);
        grid.add(new Label("Min Quantity:"), 0, 4);
        grid.add(tfMin, 1, 4);
        grid.add(new Label("Barcode:"), 0, 5);
        grid.add(tfBarcode, 1, 5);
        grid.add(new Label("QR Preview:"), 0, 6);
        grid.add(qrView, 1, 6);

        // --- Buttons ---
        Button btnGenerateBarcode = new Button("Generate QR");
        Button btnSave = new Button("Save");
        Button btnClear = new Button("Clear Fields");
        grid.add(btnGenerateBarcode, 0, 7);
        grid.add(btnSave, 1, 7);
        grid.add(btnClear, 2, 7);

        // --- Generate QR Button Action ---
        btnGenerateBarcode.setOnAction(e -> {
            try {
                String data = tfBarcode.getText().isEmpty()
                        ? tfBatch.getText().trim() + "-" + tfName.getText().trim()
                        : tfBarcode.getText().trim();

                if (data.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Please enter Batch and Name or Barcode");
                    return;
                }

                // Ensure barcode folder exists
                Path folder = Path.of(BARCODE_FOLDER);
                if (!Files.exists(folder)) {
                    Files.createDirectories(folder);
                }

                // Generate QR Code
                BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 200, 200);
                Path path = folder.resolve(data.replaceAll("\\s+", "_") + ".png");
                MatrixToImageWriter.writeToPath(matrix, "PNG", path);

                // Display QR in ImageView
                BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);
                Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
                qrView.setImage(fxImage);

                // Set barcode field
                tfBarcode.setText(data);

                showAlert(Alert.AlertType.INFORMATION, "QR generated at: " + path.toAbsolutePath());

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Failed to generate QR code.");
            }
        });

        // --- Save Button Action ---
        btnSave.setOnAction(e -> {
            try {
                // Validate numeric inputs
                int qty = Integer.parseInt(tfQty.getText().trim());
                int minQty = Integer.parseInt(tfMin.getText().trim());

                // Validate expiry date
                LocalDate expiry = dpExpiry.getValue();
                if (expiry.isBefore(LocalDate.now())) {
                    showAlert(Alert.AlertType.WARNING, "Expiry date cannot be in the past.");
                    return;
                }

                Medicine m = new Medicine();
                m.setName(tfName.getText().trim());
                m.setBatchNo(tfBatch.getText().trim());
                m.setExpiryDate(expiry);
                m.setQuantity(qty);
                m.setMinQuantity(minQty);
                m.setBarcode(tfBarcode.getText().trim());

                boolean ok = MedicineDAO.add(m);
                showAlert(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR,
                        ok ? "Medicine saved successfully!" : "Failed to save medicine");

                if (ok)
                    clearFields(tfName, tfBatch, dpExpiry, tfQty, tfMin, tfBarcode, qrView);

            } catch (NumberFormatException nfe) {
                showAlert(Alert.AlertType.ERROR, "Quantity and Min Quantity must be integers.");
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Invalid data entered.");
            }
        });

        // --- Clear Fields Button ---
        btnClear.setOnAction(e -> clearFields(tfName, tfBatch, dpExpiry, tfQty, tfMin, tfBarcode, qrView));

        setScene(new Scene(grid));
    }

    // --- Helper Alert Method ---
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }

    // --- Helper Method to Clear Fields ---
    private void clearFields(TextField tfName, TextField tfBatch, DatePicker dpExpiry,
            TextField tfQty, TextField tfMin, TextField tfBarcode, ImageView qrView) {
        tfName.clear();
        tfBatch.clear();
        dpExpiry.setValue(LocalDate.now().plusMonths(6));
        tfQty.setText("0");
        tfMin.setText("0");
        tfBarcode.clear();
        qrView.setImage(null);
    }
}
