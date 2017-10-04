package com.github.games647.securemyaccount.listener;

import com.github.games647.securemyaccount.SecureMyAccount;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PreventListener implements Listener {

    private final Pattern slashRemover = Pattern.compile("/");

    private final SecureMyAccount plugin;

    public PreventListener(SecureMyAccount plugin) {
        this.plugin = plugin;
    }

    //prevent events before other plugins will notice them (call order is from the lowest to the highest)
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onCommand(PlayerCommandPreprocessEvent commandEvent) {
        Player invoker = commandEvent.getPlayer();

        //remove the command identifier and further command arguments
        String command = slashRemover.matcher(commandEvent.getMessage()).replaceFirst("").split(" ")[0];
        if ("login".equalsIgnoreCase(command) || "register".equalsIgnoreCase(command)) {
            //ignore our own commands
            return;
        }

        if (plugin.getConfig().getBoolean("commandOnlyProtection")) {
            List<String> protectedCommands = plugin.getConfig().getStringList("protectedCommands");
            if (protectedCommands.isEmpty() || protectedCommands.contains(command)) {
                if (!plugin.isInSession(invoker)) {
                    invoker.sendMessage(ChatColor.DARK_RED + "This action is protected for extra security");
                    invoker.sendMessage(ChatColor.DARK_RED + "Please type /session <code>");
                    commandEvent.setCancelled(true);
                }
            }
        } else {
            checkLoginStatus(invoker, commandEvent);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent asyncPlayerChatEvent) {
        //keep mind that this have to be thread-safe
        checkLoginStatus(asyncPlayerChatEvent.getPlayer(), asyncPlayerChatEvent);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        Location from = playerMoveEvent.getFrom();
        Location to = playerMoveEvent.getTo();

        if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
            checkLoginStatus(playerMoveEvent.getPlayer(), playerMoveEvent);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent blockPlaceEvent) {
        checkLoginStatus(blockPlaceEvent.getPlayer(), blockPlaceEvent);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        checkLoginStatus(blockBreakEvent.getPlayer(), blockBreakEvent);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
        checkLoginStatus(playerInteractEvent.getPlayer(), playerInteractEvent);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onItemPickup(EntityPickupItemEvent pickupItemEvent) {
        LivingEntity entity = pickupItemEvent.getEntity();
        if (entity instanceof Player) {
            checkLoginStatus((Player) entity, pickupItemEvent);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onItemDrop(PlayerDropItemEvent dropItemEvent) {
        checkLoginStatus(dropItemEvent.getPlayer(), dropItemEvent);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onInventoryClick(InventoryClickEvent clickEvent) {
        checkLoginStatus((Player) clickEvent.getWhoClicked(), clickEvent);
    }

    //this lookup have to be highly optimized, because events like the move event will call this very often
    private void checkLoginStatus(Player player, Cancellable cancelEvent) {
        //thread-safe
        if (plugin.isInSession(player) || plugin.getConfig().getBoolean("commandOnlyProtection")) {
            return;
        }

        if (!plugin.getConfig().getBoolean("protectAll")
                && !player.hasPermission(plugin.getName().toLowerCase() + ".protect") ) {
            //we don't need to protect this player
            return;
        }

        cancelEvent.setCancelled(true);
    }
}
