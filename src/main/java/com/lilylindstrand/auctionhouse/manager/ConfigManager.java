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
    String dbHost;
    int dbPort;
    String dbDatabase;
    String dbUsername;
    String dbPassword;
    public void saveConfigData() {
        dbHost = dbConfiguration.getString("host");
        dbPort = dbConfiguration.getInt("port");
        dbDatabase = dbConfiguration.getString("database");
        dbUsername = dbConfiguration.getString("database");
        dbPassword = dbConfiguration.getString("database");
    }

    /* Getters */
    public String getDbHost() { return dbHost; }
    public int getDbPort() { return dbPort; }
    public String getDbDatabase() { return dbDatabase; }
    public String getDbUsername() { return dbUsername; }
    public String getDbPassword() { return dbPassword; }



}
