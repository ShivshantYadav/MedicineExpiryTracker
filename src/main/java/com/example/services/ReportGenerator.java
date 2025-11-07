package com.example.services;

import com.example.dao.MedicineDAO;
import com.example.models.Medicine;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReportGenerator {
    public boolean generateExpiringCSV(String path, int days) {
        List<Medicine> list = MedicineDAO.getExpiringWithinDays(days);
        try (FileWriter fw = new FileWriter(path)) {
            fw.append("Medicine, Batch, ExpiryDate, Quantity, SupplierId\n");
            for (Medicine m : list) {
                fw.append(String.format(
                    "%s,%s,%s,%d,%s\n",
                    m.getName(),
                    m.getBatchNo(),
                    m.getExpiryDate().toString(),
                    m.getQuantity(),
                    m.getSupplierId() == null ? "" : m.getSupplierId()
                ));
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
