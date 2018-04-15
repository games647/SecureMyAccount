package com.github.games647.securemyaccount.listener;

import com.github.games647.securemyaccount.SecureMyAccount;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class InventoryPinListener implements Listener {

    private static final int PIN_START = 29;
    private static final int PIN_END = 36 - 1;

    private final SecureMyAccount plugin;

    public InventoryPinListener(SecureMyAccount plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onItemClick(InventoryClickEvent inventoryClickEvent) {
        Player player = (Player) inventoryClickEvent.getWhoClicked();

        InventoryView inventory = player.getOpenInventory();
        if (plugin.getConfig().getBoolean("inventoryPin") && inventory != null
                && "Pin code".equals(inventory.getTitle())) {
            if (inventoryClickEvent.getRawSlot() < 36) {
                Inventory clickedInventory = inventoryClickEvent.getClickedInventory();
                ItemStack currentItem = inventoryClickEvent.getCurrentItem();
                if (currentItem != null) {
                    switch (currentItem.getType()) {
                        case STONE:
                            int nextPos = nextPinPos(clickedInventory);
                            if (nextPos != -1) {
                                inventory.setItem(nextPos, currentItem);
                            }
                            break;
                        case BARRIER:
                            player.closeInventory();
                            break;
                        case REDSTONE_BLOCK:
                            deleteLast(inventory);
                            break;
                        case SLIME_BALL:
                            submitPin((Player) inventoryClickEvent.getWhoClicked(), clickedInventory);
                            break;
                        default:
                            break;
                    }
                }
            }

            inventoryClickEvent.setCancelled(true);
        }
    }

    private int nextPinPos(Inventory inventory) {
        for (int i = PIN_START; i < PIN_END; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null) {
                return i;
            }
        }

        return -1;
    }

    private void deleteLast(InventoryView inventory) {
        for (int i = PIN_END - 1; i > PIN_START - 1; i--) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                inventory.setItem(i, null);
                break;
            }
        }
    }

    private void submitPin(Player player, Inventory inventory) {
        StringBuilder builder = new StringBuilder(6);
        for (int i = PIN_START; i < PIN_END; i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                builder.append(item.getAmount());
            }
        }

        if (builder.length() == 6) {
            builder.insert(0, "login ");
            player.performCommand(builder.toString());
        }
    }
}
