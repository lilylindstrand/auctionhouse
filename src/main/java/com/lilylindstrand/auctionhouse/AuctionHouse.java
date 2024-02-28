package com.lilylindstrand.auctionhouse;

import com.lilylindstrand.auctionhouse.command.Ah;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuctionHouse extends JavaPlugin {

    private static Economy economy = null;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("Missing dependency: Vault. Disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        registerCommands();
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
}
