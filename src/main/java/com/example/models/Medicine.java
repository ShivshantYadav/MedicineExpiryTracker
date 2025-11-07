package com.example.models;

import java.time.LocalDate;

public class Medicine {
    private int id;
    private String name;
    private String batchNo;
    private LocalDate expiryDate;
    private int quantity;
    private int minQuantity;
    private Integer supplierId; // can be null
    private String barcode;

    public Medicine() {}

    public Medicine(int id, String name, String batchNo, LocalDate expiryDate, int quantity, int minQuantity, Integer supplierId, String barcode) {
        this.id = id;
        this.name = name;
        this.batchNo = batchNo;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
        this.minQuantity = minQuantity;
        this.supplierId = supplierId;
        this.barcode = barcode;
    }

    // getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getMinQuantity() { return minQuantity; }
    public void setMinQuantity(int minQuantity) { this.minQuantity = minQuantity; }
    public Integer getSupplierId() { return supplierId; }
    public void setSupplierId(Integer supplierId) { this.supplierId = supplierId; }
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
}
