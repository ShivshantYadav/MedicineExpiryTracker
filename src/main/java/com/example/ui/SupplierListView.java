package com.example.ui;

import com.example.dao.DAOException;
import com.example.dao.SupplierDAO;
import com.example.models.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class SupplierListView extends Stage {

    private final TableView<Supplier> table = new TableView<>();
    private final ObservableList<Supplier> supplierList = FXCollections.observableArrayList();
    private final TextField searchField = new TextField();

    public SupplierListView() {
        setTitle("Supplier List");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        searchField.setPromptText("Search by name or contact...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> searchSuppliers(newVal));

        Button btnAdd = new Button("➕ Add");
        Button btnEdit = new Button("✏️ Edit");
        Button btnDelete = new Button("🗑️ Delete");
        Button btnRefresh = new Button("🔄 Refresh");

        // Button Actions
        btnAdd.setOnAction(e -> {
            new SupplierForm().showAndWait();
            loadSuppliers();
        });

        btnEdit.setOnAction(e -> {
            Supplier selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Please select a supplier to edit.");
                return;
            }
            new SupplierForm(selected).showAndWait();
            loadSuppliers();
        });

        btnDelete.setOnAction(e -> {
            Supplier selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Please select a supplier to delete.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete supplier: " + selected.getName() + "?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Confirm Delete");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        SupplierDAO.delete(selected.getSupplierId());
                        showAlert(Alert.AlertType.INFORMATION, "Supplier deleted successfully!");
                        loadSuppliers();
                    } catch (DAOException ex) {
                        showAlert(Alert.AlertType.ERROR, "Error deleting supplier:\n" + ex.getMessage());
                    }
                }
            });
        });

        btnRefresh.setOnAction(e -> loadSuppliers());

        HBox topBar = new HBox(10, new Label("Search:"), searchField, btnAdd, btnEdit, btnDelete, btnRefresh);
        root.setTop(topBar);

        // Table setup
        table.setPlaceholder(new Label("No suppliers found."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN); // ✅ replaces deprecated one

        TableColumn<Supplier, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("supplierId"));

        TableColumn<Supplier, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Supplier, String> contactCol = new TableColumn<>("Contact");
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));

        TableColumn<Supplier, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Supplier, String> addrCol = new TableColumn<>("Address");
        addrCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        table.getColumns().setAll(idCol, nameCol, contactCol, emailCol, addrCol);
        table.setItems(supplierList);

        // Double-click edit
        table.setRowFactory(tv -> {
            TableRow<Supplier> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    new SupplierForm(row.getItem()).showAndWait();
                    loadSuppliers();
                }
            });
            return row;
        });

        root.setCenter(table);

        loadSuppliers();

        Scene scene = new Scene(root, 950, 450);
        setScene(scene);
    }

    private void loadSuppliers() {
        try {
            supplierList.setAll(SupplierDAO.getAll());
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading suppliers:\n" + e.getMessage());
        }
    }

    private void searchSuppliers(String query) {
        if (query == null || query.isBlank()) {
            loadSuppliers();
            return;
        }
        try {
            List<Supplier> all = SupplierDAO.getAll();
            String lower = query.toLowerCase();
            List<Supplier> filtered = all.stream()
                    .filter(s -> s.getName().toLowerCase().contains(lower)
                            || s.getContact().toLowerCase().contains(lower))
                    .toList();
            supplierList.setAll(filtered);
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Error searching suppliers:\n" + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
