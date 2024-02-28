package com.lilylindstrand.auctionhouse;

import com.lilylindstrand.auctionhouse.command.Ah;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuctionHouse extends JavaPlugin {

    @Override
    public void onEnable() {
        registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void registerCommands() {
        new Ah("ah", "auctionhouse.use", new String[]{"auctionhouse", "auction"}, "");
    }
}
