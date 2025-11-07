package com.example.dao;

/**
 * Custom exception class for DAO (Data Access Object) operations.
 * Helps separate database-level errors from business logic.
 */
public class DAOException extends Exception {

    /**
     * Constructor with message only.
     * 
     * @param message The exception message.
     */
    public DAOException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause (e.g., SQLException).
     * 
     * @param message The exception message.
     * @param cause The root cause of the exception.
     */
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
