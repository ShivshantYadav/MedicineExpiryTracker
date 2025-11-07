package com.example.services;

import com.example.dao.AlertDAO;
import com.example.dao.MedicineDAO;
import com.example.dao.DAOException;
import com.example.models.Medicine;

import java.util.List;

public class StockManager {
    public void checkLowStock() {
        try {
            List<Medicine> low = MedicineDAO.getLowStock();
            for (Medicine m : low) {
                String msg = String.format(
                    "Medicine '%s' (Batch %s) low stock: %d <= min %d",
                    m.getName(), m.getBatchNo(), m.getQuantity(), m.getMinQuantity()
                );
                AlertDAO.addAlert(msg, "stock");
                // Optionally trigger reorder flow
            }
        } catch (DAOException e) {
            System.err.println("Error while checking low stock: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
