package com.example.models;

import java.time.LocalDateTime;

public class AlertModel {
    private int id;
    private String message;
    private String type; // 'expiry' or 'stock'
    private LocalDateTime dateCreated;

    public AlertModel() {}
    public AlertModel(int id, String message, String type, LocalDateTime dateCreated) {
        this.id = id; this.message = message; this.type = type; this.dateCreated = dateCreated;
    }
    // getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDateTime getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDateTime dateCreated) { this.dateCreated = dateCreated; }
}
