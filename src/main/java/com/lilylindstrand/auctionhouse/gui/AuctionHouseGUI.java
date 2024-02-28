package com.lilylindstrand.auctionhouse.gui;

import com.lilylindstrand.auctionhouse.AuctionHouse;
import com.lilylindstrand.auctionhouse.manager.DatabaseManager;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.item.impl.SuppliedItem;
import xyz.xenondevs.invui.window.Window;

import java.util.List;
import java.util.function.Supplier;

public class AuctionHouseGUI {

    Player player;
    DatabaseManager db;
    List<ItemStack> items;
    int index = 0;

    public AuctionHouseGUI(Player player, DatabaseManager db) {
        this.player = player;
        this.db = db;
    }

    public void createGui() {

        /* Get all items from database, feed them into a supplier that the GUI can use to display a new item each time.  */
        items = db.getAllItems();
        Supplier<? extends ItemProvider> supplier = new Supplier<ItemProvider>() {
            @Override
            public ItemProvider get() {
                if (index >= items.size()) {
                    return new SimpleItem(new ItemBuilder(Material.AIR)).getItemProvider();
                }
                ItemStack tempItem = items.get(index);
                index++;
                return new ItemWrapper(tempItem);
            }
        };

        Gui gui = Gui.normal()
                .setStructure(
                        "#########",
                        "#.......#",
                        "#.......#",
                        "#.......#",
                        "#.......#",
                        "#########")
                .addIngredient('#', new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)))
                .addIngredient('.', new SuppliedItem(supplier, null))
                .build();

        gui.addItems();

        Window window = Window.single()
                .setViewer(player)
                .setTitle("Auction House")
                .setGui(gui)
                .build();

        window.open();
    }

}
