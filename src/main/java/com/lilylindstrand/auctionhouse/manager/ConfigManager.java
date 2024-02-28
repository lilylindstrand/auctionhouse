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

    public ConfigManager(AuctionHouse plugin) {
        this.plugin = plugin;
    }


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
    }

}
