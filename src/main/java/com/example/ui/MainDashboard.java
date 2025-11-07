package com.example.ui;

import com.example.dao.AlertDAO;
import com.example.dao.MedicineDAO;
import com.example.models.AlertModel;
import com.example.models.Medicine;
import com.example.services.ExpiryChecker;
import com.example.services.StockManager;
import com.example.services.ReportGenerator;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainDashboard {
    private final BorderPane root;
    private final TableView<Medicine> table;
    private final ListView<String> alertsList;
    private ScheduledExecutorService scheduler;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public MainDashboard() {
        root = new BorderPane();
        root.setPadding(new Insets(10));

        // --- Top bar ---
        HBox top = new HBox(10);
        Button btnAdd = new Button("Add Medicine");
        Button btnSuppliers = new Button("Manage Suppliers");
        Button btnRefresh = new Button("Refresh");
        Button btnReports = new Button("Generate Expiry CSV");
        top.getChildren().addAll(btnAdd, btnSuppliers, btnRefresh, btnReports);
        root.setTop(top);

        // --- Center table ---
        table = new TableView<>();
        TableColumn<Medicine, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        TableColumn<Medicine, String> colBatch = new TableColumn<>("Batch");
        colBatch.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBatchNo()));

        TableColumn<Medicine, String> colExpiry = new TableColumn<>("Expiry");
        colExpiry.setCellValueFactory(c -> 
            new SimpleStringProperty(c.getValue().getExpiryDate().format(dateFormatter))
        );

        TableColumn<Medicine, String> colQty = new TableColumn<>("Qty");
        colQty.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getQuantity())));

        table.getColumns().addAll(colName, colBatch, colExpiry, colQty);
        table.setPlaceholder(new Label("No medicines available"));
        root.setCenter(table);

        // --- Right - Alerts ---
        VBox right = new VBox(10);
        right.setPadding(new Insets(5));
        Label lbl = new Label("Alerts");
        alertsList = new ListView<>();
        alertsList.setPlaceholder(new Label("No alerts"));
        right.getChildren().addAll(lbl, alertsList);
        root.setRight(right);

        // --- Button actions ---
        btnAdd.setOnAction(e -> new AddMedicineForm().showAndWait());
        btnSuppliers.setOnAction(e -> new SupplierForm().showAndWait());
        btnRefresh.setOnAction(e -> refreshData());
        btnReports.setOnAction(e -> {
            boolean ok = new ReportGenerator().generateExpiringCSV("expiring_report.csv", 30);
            Alert a = new Alert(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, 
                                ok ? "Report generated: expiring_report.csv" : "Failed to generate report");
            a.showAndWait();
        });

        // Initial load
        refreshData();
    }

    public Parent getRoot() {
        return root;
    }

    public void refreshData() {
        List<Medicine> meds = MedicineDAO.getAll();
        table.getItems().setAll(meds);

        List<AlertModel> alerts = AlertDAO.getAll();
        alertsList.getItems().clear();
        for (AlertModel a : alerts) {
            alertsList.getItems().add(a.getDateCreated().format(dateFormatter) + " - " + a.getMessage());
        }
    }

    // --- Background checks ---
    public void startBackgroundChecks() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            new ExpiryChecker().runCheck();
            new StockManager().checkLowStock();
            Platform.runLater(this::refreshData);
        };
        // Run every 60 seconds for demo; for real use: 24 hours = 86400 seconds
        scheduler.scheduleAtFixedRate(task, 0, 60, TimeUnit.SECONDS);
    }

    public void stopBackgroundChecks() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }
}
