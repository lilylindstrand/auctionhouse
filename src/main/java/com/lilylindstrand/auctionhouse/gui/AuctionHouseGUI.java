package com.lilylindstrand.auctionhouse.gui;

import com.lilylindstrand.auctionhouse.AuctionHouse;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

public class AuctionHouseGUI {

    Player player;
    public AuctionHouseGUI(Player player) {
        this.player = player;
    }

    public void createGui() {
        Gui gui = Gui.normal()
                .setStructure(
                        "#########",
                        "#.......#",
                        "#.......#",
                        "#.......#",
                        "#.......#",
                        "#########")
                .addIngredient('#', new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS)))
                .build();

        Window window = Window.single()
                .setViewer(player)
                .setTitle("Auction House")
                .setGui(gui)
                .build();

        window.open();
    }

}
