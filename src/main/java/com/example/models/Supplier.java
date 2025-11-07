package com.example.models;

public class Supplier {
    private int supplierId; // renamed for clarity
    private String name;
    private String contact;
    private String email;
    private String address;

    // Default constructor
    public Supplier() {}

    // Constructor with ID (for editing existing supplier)
    public Supplier(int supplierId, String name, String contact, String email, String address) {
        this.supplierId = supplierId;
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.address = address;
    }

    // Constructor without ID (for adding new supplier)
    public Supplier(String name, String contact, String email, String address) {
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.address = address;
    }

    // Getters and setters
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return name; // useful for ComboBoxes or debugging
    }
}
