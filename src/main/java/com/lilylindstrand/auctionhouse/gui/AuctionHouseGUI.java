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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class AuctionHouseGUI extends GUI{

    Player player;
    DatabaseManager db;
    List<ItemStack> items;
    List<ItemStack> removedItems;
    int removedIndex = 0;
    int index = 0;
    Window window;
    AuctionHouse plugin;

    public AuctionHouseGUI(Player player, DatabaseManager db, AuctionHouse plugin) {
        this.player = player;
        this.db = db;
        this.plugin = plugin;
    }

    public void createGui() {

        /* Get all items from database, feed them into a supplier that the GUI can use to display a new item each time.  */
        items = db.getAllItems();
        removedItems = db.getAllRemovedItems();

        Supplier<? extends ItemProvider> supplier = new Supplier<ItemProvider>() {
            @Override
            public ItemProvider get() {
                if (removedIndex != (removedItems.size())) {
                    ItemProvider isProvider = createSoldItem();
                    ItemStack is = isProvider.get();
                    ItemMeta isMeta = is.getItemMeta();
                    PersistentDataContainer persistentDataContainer = isMeta.getPersistentDataContainer();
                    NamespacedKey itemIdKey = plugin.getKey();
                    int id = persistentDataContainer.get(itemIdKey, PersistentDataType.INTEGER);

                    if (db.getRemovedItemSeller(ItemSerializer.encode(removedItems.get(id))) == player.getUniqueId()) {
                        return isProvider;
                    }
                }
                return createItem();
            }
        };

        /* Function for buying items */
        Function<Click, Boolean> function = new Function<Click, Boolean>() {
            @Override
            public Boolean apply(Click click) {
//            if (Boolean.TRUE.equals(click.getEvent().getCurrentItem().getItemMeta().getPersistentDataContainer()
//                    .get(plugin.getSoldItemKey(), PersistentDataType.BOOLEAN))) {
//                ItemStack is = (ItemStack) createSoldItem();
//                ItemMeta isMeta = is.getItemMeta();
//                PersistentDataContainer persistentDataContainer = isMeta.getPersistentDataContainer();
//                NamespacedKey itemIdKey = plugin.getKey();
//
//                int id = persistentDataContainer.get(itemIdKey, PersistentDataType.INTEGER);
//
//                AuctionHouse.getEconomy().depositPlayer(player, db.getSoldItemPrice(ItemSerializer.encode(removedItems.get(id))));
//                db.deleteRemovedItem(ItemSerializer.encode(removedItems.get(id)));
//                closeGUI();
//                return true;
//            }

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
        int price = db.getItemPrice(ItemSerializer.encode(removedItems.get(removedIndex)));
        UUID sellerUUID = db.getItemSeller(ItemSerializer.encode(removedItems.get(removedIndex)));

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
        NamespacedKey itemIdKey = plugin.getKey();
        persistentDataContainer.set(soldItemKey, PersistentDataType.BOOLEAN, true);
        persistentDataContainer.set(itemIdKey, PersistentDataType.INTEGER, removedIndex);


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
        int price = db.getItemPrice(ItemSerializer.encode(items.get(index)));
        UUID sellerUUID = db.getItemSeller(ItemSerializer.encode(items.get(index)));
        OfflinePlayer seller = Bukkit.getOfflinePlayer(sellerUUID);

        // Get time until expiry
        Date uploadDate = db.getItemDate(ItemSerializer.encode(items.get(index)));
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
