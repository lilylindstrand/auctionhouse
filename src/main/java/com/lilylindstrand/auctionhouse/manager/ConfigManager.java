package com.lilylindstrand.auctionhouse.manager;

import com.lilylindstrand.auctionhouse.AuctionHouse;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    AuctionHouse plugin;
    FileConfiguration fileConfiguration;
    File dbConfigFile;
    FileConfiguration dbConfiguration;

    /* Constructor */
    public ConfigManager(AuctionHouse plugin) {
        this.plugin = plugin;
    }

    /* Methods */
    public void setupConfig() throws IOException, InvalidConfigurationException {
        plugin.saveDefaultConfig();
        fileConfiguration = plugin.getConfig();

        dbConfigFile = new File(plugin.getDataFolder(), "database.yml");
        if (!(dbConfigFile.exists())) {
            dbConfigFile.getParentFile().mkdirs();
            plugin.saveResource("database.yml", false);
        }

        dbConfiguration = new YamlConfiguration();
        dbConfiguration.load(dbConfigFile);
        saveConfigData();
    }

    // Saves config data after loading the file, so that files only have to be accessed once, not every time the data is used.
    // TODO: /reload command
    static String dbHost;
    static int dbPort;
    static String dbDatabase;
    static String dbUsername;
    static String dbPassword;

    public void saveConfigData() {
        dbHost = dbConfiguration.getString("host");
        dbPort = dbConfiguration.getInt("port");
        dbDatabase = dbConfiguration.getString("database");
        dbUsername = dbConfiguration.getString("database");
        dbPassword = dbConfiguration.getString("database");
    }

    /* Getters */
    public static String getDbHost() { return dbHost; }
    public static int getDbPort() { return dbPort; }
    public static String getDbDatabase() { return dbDatabase; }
    public static String getDbUsername() { return dbUsername; }
    public static String getDbPassword() { return dbPassword; }



}
