package com.github.games647.securemyaccount.listener;

import com.github.games647.securemyaccount.Account;
import com.github.games647.securemyaccount.ImageRenderer;
import com.github.games647.securemyaccount.SecureMyAccount;

import java.net.InetAddress;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

public class SessionListener implements Listener {

    private final SecureMyAccount plugin;

    public SessionListener(SecureMyAccount plugin) {
        this.plugin = plugin;
    }

    //listen to high priority in order to ignore for example player kicks
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent joinEvent) {
        Player player = joinEvent.getPlayer();
        if (plugin.getConfig().getBoolean("protectAll")
                || player.hasPermission(plugin.getName().toLowerCase() + ".protect")) {
            Account account = plugin.getOrLoadAccount(player);
            if (account.isRegistered()) {
                InetAddress newIp = player.getAddress().getAddress();
                if (newIp.equals(account.getIP())) {
                    player.sendMessage(ChatColor.DARK_GREEN + "IP auto login");
                    plugin.startSession(player);
                } else if (!plugin.getConfig().getBoolean("commandOnlyProtection")) {
                    player.sendMessage(ChatColor.DARK_GREEN + "Your account is protected. Please login /login <code>");
                }
            } else {
                player.sendMessage(ChatColor.DARK_GREEN + "This account should be protected. Generating key...");
                player.performCommand("register");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDrop(PlayerDropItemEvent dropItemEvent) {
        Item itemDrop = dropItemEvent.getItemDrop();
        ItemStack mapItem = itemDrop.getItemStack();
        if (isOurGraph(mapItem)) {
            itemDrop.setItemStack(new ItemStack(Material.AIR));
        }
    }

    private boolean isOurGraph(ItemStack mapItem) {
        if (mapItem == null || mapItem.getType() != Material.MAP) {
            return false;
        }

        short mapId = mapItem.getDurability();
        MapView map = Bukkit.getMap(mapId);
        return map != null && map.getRenderers().stream()
                .anyMatch(ImageRenderer.class::isInstance);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent quitEvent) {
        plugin.endSession(quitEvent.getPlayer());
    }
}
