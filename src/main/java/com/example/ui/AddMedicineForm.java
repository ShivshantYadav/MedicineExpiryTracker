package com.example.ui;

import com.example.models.Medicine;
import com.example.dao.MedicineDAO;
import com.example.models.Supplier;
import com.example.dao.SupplierDAO;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import javafx.collections.FXCollections;
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
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

public class AddMedicineForm extends Stage {

    private static final Path BARCODE_FOLDER =
            Paths.get(System.getProperty("user.home"), "MedicineExpiryTracker", "barcode");

    public AddMedicineForm() {
        setTitle("Add Medicine");
        initModality(Modality.APPLICATION_MODAL);

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

        // Supplier ComboBox
        ComboBox<Supplier> cbSupplier = new ComboBox<>();
        try {
            List<Supplier> suppliers = SupplierDAO.getAll();
            cbSupplier.setItems(FXCollections.observableArrayList(suppliers));
        } catch (Exception e) {
            cbSupplier.setPromptText("Unable to load suppliers");
        }
        cbSupplier.setPromptText("Select Supplier (Optional)");

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
        grid.add(new Label("Supplier:"), 0, 6);
        grid.add(cbSupplier, 1, 6);
        grid.add(new Label("QR Preview:"), 0, 7);
        grid.add(qrView, 1, 7);

        // --- Buttons ---
        Button btnGenerateBarcode = new Button("Generate QR");
        Button btnSave = new Button("Save");
        Button btnClear = new Button("Clear");
        grid.add(btnGenerateBarcode, 0, 8);
        grid.add(btnSave, 1, 8);
        grid.add(btnClear, 2, 8);

        // --- Generate QR ---
        btnGenerateBarcode.setOnAction(e -> {
            try {
                String data = tfBarcode.getText().trim();
                if (data.isEmpty()) {
                    if (tfName.getText().trim().isEmpty() || tfBatch.getText().trim().isEmpty()) {
                        showAlert(Alert.AlertType.WARNING, "Please enter Name and Batch first!");
                        return;
                    }
                    data = tfBatch.getText().trim() + "-" + tfName.getText().trim();
                    tfBarcode.setText(data);
                }

                if (!Files.exists(BARCODE_FOLDER)) Files.createDirectories(BARCODE_FOLDER);

                BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 200, 200);
                Path path = BARCODE_FOLDER.resolve(data.replaceAll("\\s+", "_") + ".png");
                MatrixToImageWriter.writeToPath(matrix, "PNG", path);

                BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);
                Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
                qrView.setImage(fxImage);

                showAlert(Alert.AlertType.INFORMATION, "QR Code generated at:\n" + path.toAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Failed to generate QR code.");
            }
        });

        // --- Save Button ---
        btnSave.setOnAction(e -> {
            try {
                if (tfName.getText().trim().isEmpty() || tfBatch.getText().trim().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Name and Batch are required!");
                    return;
                }

                int qty = Integer.parseInt(tfQty.getText().trim());
                int minQty = Integer.parseInt(tfMin.getText().trim());
                LocalDate expiry = dpExpiry.getValue();

                if (expiry.isBefore(LocalDate.now())) {
                    showAlert(Alert.AlertType.WARNING, "Expiry date cannot be in the past.");
                    return;
                }

                String barcode = tfBarcode.getText().trim();
                if (barcode.isEmpty()) {
                    barcode = tfBatch.getText().trim() + "-" + tfName.getText().trim();
                    tfBarcode.setText(barcode);
                }

                Medicine m = new Medicine();
                m.setName(tfName.getText().trim());
                m.setBatchNo(tfBatch.getText().trim());
                m.setExpiryDate(expiry);
                m.setQuantity(qty);
                m.setMinQuantity(minQty);
                m.setBarcode(barcode);
                if (cbSupplier.getValue() != null)
                    m.setSupplierId(cbSupplier.getValue().getSupplierId());

                boolean ok = MedicineDAO.add(m);
                showAlert(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR,
                        ok ? "Medicine saved successfully!" : "Failed to save medicine");

                if (ok) clearFields(tfName, tfBatch, dpExpiry, tfQty, tfMin, tfBarcode, qrView, cbSupplier);

            } catch (NumberFormatException nfe) {
                showAlert(Alert.AlertType.ERROR, "Quantity and Min Quantity must be numbers.");
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error saving medicine: " + ex.getMessage());
            }
        });

        // --- Clear Fields ---
        btnClear.setOnAction(e -> clearFields(tfName, tfBatch, dpExpiry, tfQty, tfMin, tfBarcode, qrView, cbSupplier));

        setScene(new Scene(grid, 600, 600));
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void clearFields(TextField tfName, TextField tfBatch, DatePicker dpExpiry,
                             TextField tfQty, TextField tfMin, TextField tfBarcode,
                             ImageView qrView, ComboBox<Supplier> cbSupplier) {
        tfName.clear();
        tfBatch.clear();
        dpExpiry.setValue(LocalDate.now().plusMonths(6));
        tfQty.setText("0");
        tfMin.setText("0");
        tfBarcode.clear();
        qrView.setImage(null);
        cbSupplier.getSelectionModel().clearSelection();
    }
}
