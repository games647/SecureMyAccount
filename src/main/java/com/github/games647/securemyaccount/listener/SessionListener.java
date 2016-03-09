package com.github.games647.securemyaccount.listener;

import com.github.games647.securemyaccount.SecureMyAccount;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SessionListener implements Listener {

    private final SecureMyAccount plugin;

    public SessionListener(SecureMyAccount plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent joinEvent) {

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent quitEvent) {
        plugin.endSession(quitEvent.getPlayer());
    }
}
