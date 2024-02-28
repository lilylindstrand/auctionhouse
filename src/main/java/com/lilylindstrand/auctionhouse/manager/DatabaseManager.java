package com.lilylindstrand.auctionhouse.manager;

import com.lilylindstrand.auctionhouse.ItemSerializer;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

    private final String HOST = ConfigManager.getDbHost();
    private final int PORT = ConfigManager.getDbPort();
    private final String DATABASE = ConfigManager.getDbDatabase();
    private final String USERNAME = ConfigManager.getDbUsername();
    private final String PASSWORD = ConfigManager.getDbPassword();

    private Connection connection;

    /* Methods */
    public void connect() {
        try { connection = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE, USERNAME, PASSWORD); }
        catch (SQLException e) { e.printStackTrace(); }
        setupDatabase();
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

    public void setupDatabase() {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS auctionhouse (" +
                            "item VARCHAR(750) PRIMARY KEY," +
                            "selleruuid CHAR(36)," +
                            "price INT," +
                            "upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ")"
            );
            statement.executeUpdate();
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    /* Getters & Setters */
    public void listItem(String base64Item, UUID sellerUUID, int price) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO auctionhouse (item, selleruuid, price) VALUES (?, ?, ?)"
            );
            statement.setString(1, base64Item);
            statement.setString(2, sellerUUID.toString());
            statement.setInt(3, price);

            statement.executeUpdate();
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<ItemStack> getAllItems() {
        List<ItemStack> itemStacks = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT item FROM auctionhouse"
            );
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                itemStacks.add(ItemSerializer.decode(resultSet.getString("item")));
            }
            return itemStacks;
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public int getItemPrice(String base64Item) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT price FROM auctionhouse WHERE item = ?"
            );
            statement.setString(1, base64Item);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("price");
            }
        }
        catch (SQLException e) { throw new RuntimeException(e); }
        return 0;
    }

    public UUID getItemSeller(String base64Item) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT selleruuid FROM auctionhouse WHERE item = ?"
            );
            statement.setString(1, base64Item);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return UUID.fromString(resultSet.getString("selleruuid"));
            }
        }
        catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    public Date getItemDate(String base64Item) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT upload_date FROM auctionhouse WHERE item = ?"
            );
            statement.setString(1, base64Item);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDate("upload_date");
            }
        }
        catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

}
