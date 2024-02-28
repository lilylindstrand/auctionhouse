package com.lilylindstrand.auctionhouse.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

public class AuctionHouseSellGUI {

    Player player;
    int price;

    public AuctionHouseSellGUI(Player player, int price) {
        this.player = player;
        this.price = price;
    }

    public void createGui() {

        ItemStack sellItem = new ItemStack(player.getInventory().getItemInMainHand());
        ItemMeta sellItemMeta = sellItem.getItemMeta();
        // Yes, this is Null-Safe despite the IDE's complaints. It is only null if the Material is Air.
        // AuctionHouse.java confirms it is not Air before running this method.
        sellItemMeta.getLore().add(0, ChatColor.translateAlternateColorCodes('&', "&7Sell Price: &6") + price);
        sellItem.setItemMeta(sellItemMeta);

        Gui gui = Gui.normal()
                .setStructure(
                        "#########",
                        "#...x...#",
                        "#########")
                .addIngredient('#', new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)))
                .addIngredient('x', new SimpleItem(sellItem))
                .build();

        Window window = Window.single()
                .setViewer(player)
                .setTitle("Sell on Auction House")
                .setGui(gui)
                .build();

        window.open();

    }

}