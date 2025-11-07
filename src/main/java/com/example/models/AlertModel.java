package com.example.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AlertModel {

    private int alertId;
    private String message;
    private String type; // 'expiry' or 'stock'
    private LocalDateTime dateCreated;

    public AlertModel() {
        // Default constructor required for DAO or frameworks
    }

    public AlertModel(int alertId, String message, String type, LocalDateTime dateCreated) {
        this.alertId = alertId;
        this.message = message;
        this.type = type;
        this.dateCreated = dateCreated;
    }

    // --- Getters and Setters ---
    public int getAlertId() {
        return alertId;
    }

    public void setAlertId(int alertId) {
        this.alertId = alertId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message != null ? message.trim() : null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type != null ? type.trim().toLowerCase() : null;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    // --- Helper method for TableView column binding ---
    public String getFormattedDate() {
        if (dateCreated == null) return "";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        return dateCreated.format(fmt);
    }

    @Override
    public String toString() {
        return "[" + alertId + "] " + type.toUpperCase() + " - " + message;
    }
}
