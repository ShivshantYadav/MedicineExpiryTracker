package com.example.models;

import javafx.beans.property.*;

public class Alert {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty message = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final StringProperty createdAt = new SimpleStringProperty();

    public Alert(int id, String message, String type, String createdAt) {
        this.id.set(id);
        this.message.set(message);
        this.type.set(type);
        this.createdAt.set(createdAt);
    }

    public int getId() { return id.get(); }
    public String getMessage() { return message.get(); }
    public String getType() { return type.get(); }
    public String getCreatedAt() { return createdAt.get(); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty messageProperty() { return message; }
    public StringProperty typeProperty() { return type; }
    public StringProperty createdAtProperty() { return createdAt; }
}
