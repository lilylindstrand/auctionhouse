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

import java.util.ArrayList;
import java.util.List;

public class AuctionHouseSellGUI {

    Player player;
    int price;

    public AuctionHouseSellGUI(Player player, int price) {
        this.player = player;
        this.price = price;
    }

    public void createGui() {

        /* SELL ITEM */
        ItemStack sellItem = new ItemStack(player.getInventory().getItemInMainHand());
        ItemMeta sellItemMeta = sellItem.getItemMeta();

        // Create item lore if it doesn't have any, modify existing lore if it has lore
        List<String> sellItemLore;
        if (sellItemMeta.hasLore()) {
            sellItemLore = sellItemMeta.getLore();
        } else {
            sellItemLore = new ArrayList<>();
        }
        sellItemLore.add(0, ChatColor.translateAlternateColorCodes('&', "&7Sell Price: &6") + price);
        sellItemMeta.setLore(sellItemLore);
        sellItem.setItemMeta(sellItemMeta);

        /* GUI */
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