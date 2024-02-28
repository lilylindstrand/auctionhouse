package com.lilylindstrand.auctionhouse.command;

import com.lilylindstrand.auctionhouse.abstracted.Command;
import com.lilylindstrand.auctionhouse.gui.AuctionHouseGUI;
import com.lilylindstrand.auctionhouse.gui.AuctionHouseSellGUI;
import org.bukkit.ChatColor;
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

        if (args.length == 0) {
            AuctionHouseGUI auctionHouseGUI = new AuctionHouseGUI(player);
            auctionHouseGUI.createGui();
        } else if (args.length == 1 && args[0].equals("sell")) {
            AuctionHouseSellGUI auctionHouseSellGUI = new AuctionHouseSellGUI(player);
            auctionHouseSellGUI.createGui();
        } else {
            player.sendMessage(ChatColor.RED + "Invalid arguments!");
        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
