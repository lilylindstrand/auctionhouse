package com.lilylindstrand.auctionhouse.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

public class AuctionHouseSellGUI {

    Player player;
    public AuctionHouseSellGUI(Player player) {
        this.player = player;
    }

    public void createGui() {

        Gui gui = Gui.normal()
                .setStructure(
                        "#########",
                        "#...x...#",
                        "#########")
                .addIngredient('#', new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)))
                .addIngredient('x', new SimpleItem(player.getInventory().getItemInMainHand())) // TODO: Replace Placeholder
                .build();

        Window window = Window.single()
                .setViewer(player)
                .setTitle("Sell on Auction House")
                .setGui(gui)
                .build();

        window.open();

    }

}