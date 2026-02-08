package com.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectionFactory {
    // Initialize the Logger for this class
    private static final Logger logger = LogManager.getLogger(ConnectionFactory.class);

    private static final String URL = "jdbc:mysql://localhost:3306/revconnect_db";
    private static final String USER = "root";
    private static final String PASS = "0000"; // Replace with your actual password

    public static Connection getConnection() {
        Connection conn = null;
        try {
            logger.info("Attempting to connect to the database...");
            conn = DriverManager.getConnection(URL, USER, PASS);
            logger.info("Database connection established successfully.");
        } catch (SQLException e) {
            logger.error("Database connection failed! Error: {}", e.getMessage());
        }
        return conn;
    }
}