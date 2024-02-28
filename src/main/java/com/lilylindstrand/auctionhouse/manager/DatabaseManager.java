package com.lilylindstrand.auctionhouse.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private final String HOST = ConfigManager.getDbHost();
    private final int PORT = ConfigManager.getDbPort();
    private final String DATABASE = ConfigManager.getDbDatabase();
    private final String USERNAME = ConfigManager.getDbUsername();
    private final String PASSWORD = ConfigManager.getDbPassword();

    private Connection connection;

    public void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?useSSL=false", USERNAME, PASSWORD);
    }

    public void disconnect() {
        if (isConnected()) {
            try { connection.close(); }
            catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public boolean isConnected() {
        return connection != null;
    }

}
