package com.example.services;

import com.example.dao.AlertDAO;
import com.example.dao.MedicineDAO;
import com.example.dao.DAOException;
import com.example.models.Medicine;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ExpiryChecker {
    private final int DAYS_THRESHOLD = 30; // notify if expiring within 30 days

    public void runCheck() {
        try {
            List<Medicine> list = MedicineDAO.getExpiringWithinDays(DAYS_THRESHOLD);
            LocalDate today = LocalDate.now();

            for (Medicine m : list) {
                long daysLeft = ChronoUnit.DAYS.between(today, m.getExpiryDate());
                String msg = String.format(
                    "Medicine '%s' (Batch %s) expires in %d day(s)",
                    m.getName(), m.getBatchNo(), daysLeft
                );
                AlertDAO.addAlert(msg, "expiry");
            }

        } catch (DAOException e) {
            System.err.println("Error while checking expiry dates: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
