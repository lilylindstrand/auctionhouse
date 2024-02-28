package com.lilylindstrand.auctionhouse.command;

import com.lilylindstrand.auctionhouse.AuctionHouse;
import com.lilylindstrand.auctionhouse.abstracted.Command;
import com.lilylindstrand.auctionhouse.gui.AuctionHouseGUI;
import com.lilylindstrand.auctionhouse.gui.AuctionHouseSellGUI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
        }

        // Ensure command is structured as "/ah sell <price>"
        else if (args.length == 2 && args[0].equals("sell") && (!args[1].isBlank())) {
            // Ensure player is holding an item before attempting to sell it
            if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                player.sendMessage(ChatColor.RED + "You are not holding an item!");
                return;
            }

            try {
                int price = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid price!");
                return;
            }

            AuctionHouseSellGUI auctionHouseSellGUI = new AuctionHouseSellGUI(player, price);
            auctionHouseSellGUI.createGui();
        }

        else {
            player.sendMessage(ChatColor.RED + "Invalid arguments!");
        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
