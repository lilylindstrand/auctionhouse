package com.lilylindstrand.auctionhouse.gui;

import com.lilylindstrand.auctionhouse.AuctionHouse;
import com.lilylindstrand.auctionhouse.ItemSerializer;
import com.lilylindstrand.auctionhouse.item.CancelItem;
import com.lilylindstrand.auctionhouse.manager.DatabaseManager;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Click;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.item.impl.SuppliedItem;
import xyz.xenondevs.invui.window.Window;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class AuctionHouseGUI extends GUI{

    Player player;
    DatabaseManager db;
    List<ItemStack> items;
    List<ItemStack> removedItems;
    Window window;
    AuctionHouse plugin;
    int index;
    int removedIndex;

    public AuctionHouseGUI(Player player, DatabaseManager db, AuctionHouse plugin) {
        this.player = player;
        this.db = db;
        this.plugin = plugin;

        index = db.getFirstId() - 1;
        removedIndex = db.getFirstRemovedId() - 1;
    }

    public void createGui() {

        /* Get all items from database, feed them into a supplier that the GUI can use to display a new item each time.  */
        items = db.getAllItems();
        removedItems = db.getAllRemovedItems();
        System.out.println(items);
        if (removedItems != null) { System.out.println(removedItems); }

        Supplier<? extends ItemProvider> supplier = new Supplier<ItemProvider>() {
            @Override
            public ItemProvider get() {
                while (removedIndex != removedItems.size()) {
                    if (db.getRemovedItemSeller(removedIndex).equals(player.getUniqueId())) {
                        return createSoldItem();
                    }
                    removedIndex++;
                }

                return createItem();
            }
        };

        /* Function for buying items */
        Function<Click, Boolean> function = new Function<Click, Boolean>() {
            @Override
            public Boolean apply(Click click) {
                AuctionHouseBuyGUI buyGUI = new AuctionHouseBuyGUI(db, click.getEvent().getCurrentItem(), player, plugin);
                buyGUI.createGui();
                return true;
            }
        };

        Gui gui = Gui.normal()
                .setStructure(
                        "#########",
                        "#.......#",
                        "#.......#",
                        "#.......#",
                        "#.......#",
                        "####X####")
                .addIngredient('#', new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("")))
                .addIngredient('.', new SuppliedItem(supplier, function))
                .addIngredient('X', new CancelItem(this))
                .build();

        gui.addItems();

        window = Window.single()
                .setViewer(player)
                .setTitle("Auction House")
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

    }

    public ItemProvider createSoldItem() {
        // Get Item Data
        int price = db.getSoldItemPrice(removedIndex);
        UUID sellerUUID = db.getRemovedItemSeller(removedIndex);

        // Modify item for GUI
        ItemStack tempItem = removedItems.get(removedIndex);
        ItemMeta tempItemItemMeta = tempItem.getItemMeta();
        List<String> lore;
        if (tempItemItemMeta.hasLore()) { lore = tempItemItemMeta.getLore(); }
        else { lore = new ArrayList<>(); }
        lore.add(lore.size(), ChatColor.translateAlternateColorCodes('&', "&7Price: &6" + price));
        lore.add(lore.size(), ChatColor.RED + "SOLD");
        lore.add(lore.size(), ChatColor.YELLOW + "Click to collect $" + price);
        tempItemItemMeta.setLore(lore);

        // Set items PDC, so that the Buy GUI can obtain the original item later (Value = current index)
        PersistentDataContainer persistentDataContainer = tempItemItemMeta.getPersistentDataContainer();
        NamespacedKey soldItemKey = plugin.getSoldItemKey();
        persistentDataContainer.set(soldItemKey, PersistentDataType.BOOLEAN, true);

        tempItem.setItemMeta(tempItemItemMeta);
        removedIndex++;
        return new ItemWrapper(tempItem);
    }

    public void createExpiredItem() {

    }

    public ItemProvider createItem() {
        // GUI needs to process 28 items per page. If there are less than 28 items, return air for the rest.
        if (index >= (items.size())) {
            return new SimpleItem(new ItemBuilder(Material.AIR)).getItemProvider();
        }

        // Get Item Data
        int price = db.getItemPrice(index);
        UUID sellerUUID = db.getItemSeller(index);
        System.out.println(sellerUUID);
        OfflinePlayer seller = Bukkit.getOfflinePlayer(sellerUUID);

        // Get time until expiry
        Date uploadDate = db.getItemDate(index);
        Date newDate = new Date(uploadDate.getTime());
        LocalDateTime uploadDateTime = newDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime currentDateTime = LocalDateTime.now();
        Duration duration = Duration.between(currentDateTime, uploadDateTime.plusHours(24));
        long minutes = duration.toMinutes();
        long hours = minutes / 60;
        minutes -= (hours * 60);
        String time;
        if (hours >= 1) { time = hours + " hours, " + minutes + " minutes"; }
        else { time = minutes + " minutes"; }

        // Modify item for GUI
        ItemStack tempItem = items.get(index);
        ItemMeta tempItemItemMeta = tempItem.getItemMeta();
        List<String> lore;
        if (tempItemItemMeta.hasLore()) { lore = tempItemItemMeta.getLore(); }
        else { lore = new ArrayList<>(); }
        lore.add(lore.size(), ChatColor.translateAlternateColorCodes('&', "&7Price: &6" + price));
        lore.add(lore.size(), ChatColor.translateAlternateColorCodes('&', "&7Sold by: &a" + seller.getName())); //todo: fix, offlineplayer
        lore.add(lore.size(), ChatColor.translateAlternateColorCodes('&', "&7Expires in: &a" + time));
        tempItemItemMeta.setLore(lore);

        // Set items PDC, so that the Buy GUI can obtain the original item later (Value = current index)
        PersistentDataContainer persistentDataContainer = tempItemItemMeta.getPersistentDataContainer();
        NamespacedKey itemId = plugin.getKey();
        persistentDataContainer.set(itemId, PersistentDataType.INTEGER, index);

        tempItem.setItemMeta(tempItemItemMeta);
        index++;
        return new ItemWrapper(tempItem);
    }

}
