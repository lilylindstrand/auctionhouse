package com.lilylindstrand.auctionhouse.item;

import com.lilylindstrand.auctionhouse.gui.AuctionHouseSellGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class ConfirmItem extends AbstractItem {

    AuctionHouseSellGUI gui;
    public ConfirmItem(AuctionHouseSellGUI gui) {
        this.gui = gui;
    }

    @Override
    public ItemProvider getItemProvider() {
        return new ItemBuilder(Material.GREEN_WOOL).setDisplayName(ChatColor.GREEN + "Confirm");
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
        gui.confirmSell();
    }
}
