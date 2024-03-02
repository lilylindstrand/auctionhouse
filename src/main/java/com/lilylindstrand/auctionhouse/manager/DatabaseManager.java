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

    //TODO: Add a "isBought" boolean, maybe add an id int
    public void setupDatabase() {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS auctionhouse (" +
                            "id INT PRIMARY KEY AUTO_INCREMENT," +
                            "item VARCHAR(750), " +
                            "selleruuid CHAR(36)," +
                            "price INT," +
                            "upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ")"
            );
            statement.executeUpdate();

            // Second table, for items that should not be listed on the Auction House (Expired/Sold), but canot yet be deleted)
            PreparedStatement statement2 = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS removeditems (" +
                            "id INT PRIMARY KEY AUTO_INCREMENT," +
                            "item VARCHAR(750)," +
                            "selleruuid CHAR(36)," +
                            "price INT," +
                            "sold BOOLEAN," +
                            "expired BOOLEAN" +
                            ")"
            );
            statement2.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /* Setters */
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

    public void sellItem(int id, String base64Item, UUID sellerUUID, int price) {
        id++;
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO removeditems (item, selleruuid, price, sold, expired) VALUES (?, ?, ?, ?, ?)"
            );
            statement.setString(1, base64Item);
            statement.setString(2, sellerUUID.toString());
            statement.setInt(3, price);
            statement.setBoolean(4, true);
            statement.setBoolean(5, false);
            statement.executeUpdate();

            PreparedStatement statement2 = connection.prepareStatement(
                    "DELETE FROM auctionhouse WHERE id = ?"
            );
            statement2.setInt(1, id);
            statement2.executeUpdate();
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void expireItem(String base64Item, UUID sellerUUID) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO removeditems (item, selleruuid, price, sold, expired) VALUES (?, ?, ?, ?, ?)"
            );
            statement.setString(1, base64Item);
            statement.setString(2, sellerUUID.toString());
            statement.setInt(3, 0);
            statement.setBoolean(4, false);
            statement.setBoolean(5, true);
            statement.executeUpdate();
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void deleteRemovedItem(int id) {
        id++;
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM removeditems WHERE id = ?"
            );
            statement.setInt(1, id);
            statement.executeUpdate();
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    /* Getters */
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

    public int getItemPrice(int id) {
        id++;
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT price FROM auctionhouse WHERE id = ?"
            );
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("price");
            }
        }
        catch (SQLException e) { throw new RuntimeException(e); }
        return 0;
    }

    public UUID getItemSeller(int id) {
        id++;
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT selleruuid FROM auctionhouse WHERE id = ?"
            );
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return UUID.fromString(resultSet.getString("selleruuid"));
            }
        }
        catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    public Timestamp getItemDate(int id) {
        id++;
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT upload_date FROM auctionhouse WHERE id = ?"
            );
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getTimestamp("upload_date");
            }
        }
        catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    public List<ItemStack> getAllRemovedItems() {
        List<ItemStack> itemStacks = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT item FROM removeditems"
            );
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                itemStacks.add(ItemSerializer.decode(resultSet.getString("item")));
            }
            return itemStacks;
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public int getSoldItemPrice(int id) {
        id++;
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT price FROM removeditems WHERE id = ?"
            );
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("price");
            }
        }
        catch (SQLException e) { throw new RuntimeException(e); }
        return 0;
    }

    public boolean isSold(int id) { //todo: Use this instead of PDC now that I did the key migration
        id++;
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT sold FROM removeditems WHERE id = ?"
            );
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("sold");
            }
        }
        catch (SQLException e) { throw new RuntimeException(e); }
        return false;
    }

    public boolean isExpired(int id) {
        id++;
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT expired FROM removeditems WHERE id = ?"
            );
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("expired");
            }
        }
        catch (SQLException e) { throw new RuntimeException(e); }
        return false;
    }

    public UUID getRemovedItemSeller(int id) {
        id++;
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT selleruuid FROM removeditems WHERE id = ?"
            );
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return UUID.fromString(resultSet.getString("selleruuid"));
            }
        }
        catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

}
