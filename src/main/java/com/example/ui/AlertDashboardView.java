package com.example.ui;

import com.example.dao.AlertDAO;
import com.example.dao.DAOException;
import com.example.models.AlertModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AlertDashboardView extends Stage {

    private TableView<AlertModel> table;
    private ObservableList<AlertModel> alertList;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AlertDashboardView() {
        setTitle("⚠️ Alerts Dashboard");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Table setup
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<AlertModel, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<AlertModel, String> colMessage = new TableColumn<>("Message");
        colMessage.setCellValueFactory(new PropertyValueFactory<>("message"));

        TableColumn<AlertModel, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<AlertModel, String> colDate = new TableColumn<>("Created At");
        colDate.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDateCreated() != null) {
                return new SimpleStringProperty(
                        cellData.getValue().getDateCreated().format(formatter)
                );
            } else {
                return new SimpleStringProperty("N/A");
            }
        });

        table.getColumns().addAll(colId, colMessage, colType, colDate);

        alertList = FXCollections.observableArrayList();
        table.setItems(alertList);

        // Buttons
        Button btnRefresh = new Button("🔄 Refresh");
        Button btnClear = new Button("🗑️ Clear All");

        btnRefresh.setOnAction(e -> loadAlerts());
        btnClear.setOnAction(e -> clearAllAlerts());

        HBox topBox = new HBox(10, btnRefresh, btnClear);
        topBox.setPadding(new Insets(10));

        root.setTop(topBox);
        root.setCenter(table);

        Scene scene = new Scene(root, 900, 400);
        setScene(scene);

        // Initial load
        loadAlerts();
    }

    /** Load all alerts from DB */
    private void loadAlerts() {
        try {
            List<AlertModel> list = AlertDAO.getAll();
            alertList.setAll(list);
        } catch (DAOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load alerts: " + e.getMessage());
        }
    }

    /** Clear all alerts */
    private void clearAllAlerts() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Clear");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete all alerts?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                AlertDAO.clearAll();
                alertList.clear();
                showAlert(Alert.AlertType.INFORMATION, "Cleared", "All alerts cleared successfully.");
            } catch (DAOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to clear alerts: " + e.getMessage());
            }
        }
    }

    /** Utility popup for showing messages */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
