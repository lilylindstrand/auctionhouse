package com.lilylindstrand.auctionhouse.command;

import com.lilylindstrand.auctionhouse.abstracted.Command;
import com.lilylindstrand.auctionhouse.gui.AuctionHouseGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Ah extends Command {

    public Ah(String command, String permission, String[] aliases, String description) {
        super(command, permission, aliases, description);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) { return; }
        AuctionHouseGUI auctionHouseGUI = new AuctionHouseGUI(player);
        auctionHouseGUI.createGui();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
