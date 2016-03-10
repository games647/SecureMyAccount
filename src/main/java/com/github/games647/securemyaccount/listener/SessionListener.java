package com.github.games647.securemyaccount.listener;

import com.github.games647.securemyaccount.Account;
import com.github.games647.securemyaccount.SecureMyAccount;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
                String newIp = player.getAddress().getHostString();
                if (newIp.equals(account.getIp())) {
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

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent quitEvent) {
        plugin.endSession(quitEvent.getPlayer());
    }
}
