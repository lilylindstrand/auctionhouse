package com.lilylindstrand.auctionhouse;

import com.lilylindstrand.auctionhouse.command.Ah;
import com.lilylindstrand.auctionhouse.manager.ConfigManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class AuctionHouse extends JavaPlugin {

    private static Economy economy = null;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("Missing dependency: Vault. Disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        registerCommands();
        configManager = new ConfigManager(this);
        try { configManager.setupConfig(); }
        catch (IOException | InvalidConfigurationException e) { throw new RuntimeException(e); }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void registerCommands() {
        new Ah("ah", "auctionhouse.use", new String[]{"auctionhouse", "auction"}, "");
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
}
