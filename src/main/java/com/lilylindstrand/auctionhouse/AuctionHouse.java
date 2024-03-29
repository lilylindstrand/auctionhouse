package com.lilylindstrand.auctionhouse;

import com.lilylindstrand.auctionhouse.command.Ah;
import com.lilylindstrand.auctionhouse.manager.ConfigManager;
import com.lilylindstrand.auctionhouse.manager.DatabaseManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class AuctionHouse extends JavaPlugin {

    private static Economy economy = null;
    private ConfigManager configManager;
    DatabaseManager databaseManager;
    NamespacedKey itemId = new NamespacedKey(this, "itemId");
    NamespacedKey soldItemKey = new NamespacedKey(this, "soldItem");
    NamespacedKey expiredItemKey = new NamespacedKey(this, "expiredItem");

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("Missing dependency: Vault. Disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        configManager = new ConfigManager(this);
        try { configManager.setupConfig(); }
        catch (IOException | InvalidConfigurationException e) { throw new RuntimeException(e); }

        databaseManager = new DatabaseManager();
        databaseManager.connect();

        registerCommands();
    }

    @Override
    public void onDisable() {
        databaseManager.disconnect();
    }

    public void registerCommands() {
        new Ah("ah", "auctionhouse.use", new String[]{"auctionhouse", "auction"}, "", databaseManager, this);
    }

    public boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> registeredServiceProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (registeredServiceProvider == null) { return false; }
        economy = registeredServiceProvider.getProvider();
        return economy != null;
    }

    public static Economy getEconomy() {
        return economy;
    }

    public NamespacedKey getKey() {
        return itemId;
    }
    public NamespacedKey getSoldItemKey() { return soldItemKey; }
    public NamespacedKey getExpiredItemKey() { return expiredItemKey; }
}
