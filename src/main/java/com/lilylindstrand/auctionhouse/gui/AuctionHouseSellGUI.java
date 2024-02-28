package com.lilylindstrand.auctionhouse.gui;

import com.lilylindstrand.auctionhouse.ItemSerializer;
import com.lilylindstrand.auctionhouse.item.CancelItem;
import com.lilylindstrand.auctionhouse.item.ConfirmItem;
import com.lilylindstrand.auctionhouse.manager.DatabaseManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.item.impl.CommandItem;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.List;

public class AuctionHouseSellGUI {

    Player player;
    int price;
    Window window;
    DatabaseManager db;

    public AuctionHouseSellGUI(Player player, int price, DatabaseManager db) {
        this.player = player;
        this.price = price;
        this.db = db;
    }

    public void createGui() {

        /* SELL ITEM */
        ItemStack sellItem = new ItemStack(player.getInventory().getItemInMainHand());
        ItemMeta sellItemMeta = sellItem.getItemMeta();
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
                        "###1#2###")
                .addIngredient('#', new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)))
                .addIngredient('x', new SimpleItem(sellItem))
                .addIngredient('1', new ConfirmItem(this))
                .addIngredient('2', new CancelItem(this))
                .build();

        window = Window.single()
                .setViewer(player)
                .setTitle("Sell on Auction House")
                .setGui(gui)
                .build();

        window.open();
    }

    public void confirmSell() {
        window.close();
        db.listItem(ItemSerializer.encode(player.getInventory().getItemInMainHand()), player.getUniqueId(), price);
        player.getInventory().removeItem(player.getInventory().getItemInMainHand());
    }

    public void cancel() {
        window.close();
    }

}

