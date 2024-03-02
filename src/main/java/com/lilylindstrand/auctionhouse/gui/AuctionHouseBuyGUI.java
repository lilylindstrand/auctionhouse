package com.lilylindstrand.auctionhouse.gui;

import com.lilylindstrand.auctionhouse.AuctionHouse;
import com.lilylindstrand.auctionhouse.ItemSerializer;
import com.lilylindstrand.auctionhouse.item.CancelItem;
import com.lilylindstrand.auctionhouse.item.ConfirmItem;
import com.lilylindstrand.auctionhouse.manager.DatabaseManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

public class AuctionHouseBuyGUI extends GUI {

    ItemStack displayItem;
    DatabaseManager db;
    Window window;
    Player player;
    AuctionHouse plugin;

    ItemStack originalItem;
    int itemIndex;

    public AuctionHouseBuyGUI(DatabaseManager db, ItemStack displayItem, Player player, AuctionHouse plugin) {
        this.displayItem = displayItem;
        this.db = db;
        this.player = player;
        this.plugin = plugin;

        // Get the actual item the player is buying
        PersistentDataContainer persistentDataContainer = displayItem.getItemMeta().getPersistentDataContainer();
        NamespacedKey key = plugin.getKey();
        itemIndex = persistentDataContainer.get(key, PersistentDataType.INTEGER);
        originalItem = db.getAllItems().get(itemIndex - 1);
    }

    public void createGui() {

        Gui gui = Gui.normal()
                .setStructure(
                        "#########",
                        "#...x...#",
                        "###1#2###")
                .addIngredient('#', new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)))
                .addIngredient('x', new ItemWrapper(displayItem))
                .addIngredient('1', new ConfirmItem(this))
                .addIngredient('2', new CancelItem(this))
                .build();

        window = Window.single()
                .setViewer(player)
                .setTitle("Buy Item")
                .setGui(gui)
                .build();

        window.open();

    }

    @Override
    public void closeGUI() {
        window.close();
    }

    @Override
    public void onConfirm() {
        Economy economy = AuctionHouse.getEconomy();
        int price = db.getItemPrice(itemIndex);

        if (economy.getBalance(player) >= price) {
            player.sendMessage(ChatColor.GREEN + "You bought the item!");
            player.getInventory().addItem(originalItem);
            economy.withdrawPlayer(player, price);
            db.sellItem(itemIndex, ItemSerializer.encode(originalItem), player.getUniqueId(), price);
        } else {
            player.sendMessage(ChatColor.RED + "You cannot afford this item!");
        }

        window.close();
    }
}
