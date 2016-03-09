package com.github.games647.securemyaccount.listener;

import com.github.games647.securemyaccount.SecureMyAccount;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PreventListener implements Listener {

    private final SecureMyAccount plugin;

    public PreventListener(SecureMyAccount plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onCommand(PlayerCommandPreprocessEvent commandEvent) {
        Player invoker = commandEvent.getPlayer();
        String command = commandEvent.getMessage().replaceFirst("/", "");
        if (command.startsWith("op") && !plugin.isInSession(invoker)) {
            invoker.sendMessage(ChatColor.DARK_RED + "This action is protected by a password from a second device");
            invoker.sendMessage(ChatColor.DARK_RED + "Please type /session <code>");
        }
    }
}
