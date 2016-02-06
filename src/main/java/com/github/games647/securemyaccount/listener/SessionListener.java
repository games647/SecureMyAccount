package com.github.games647.securemyaccount.listener;

import com.github.games647.securemyaccount.SecureMyAccount;

import java.util.logging.Level;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapView;

public class SessionListener implements Listener {

    private final SecureMyAccount plugin;

    public SessionListener(SecureMyAccount plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onMap(MapInitializeEvent mapInitializeEvent) {
        MapView map = mapInitializeEvent.getMap();
        plugin.getLogger().log(Level.FINE, "Created a map with id={0}", map.getId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent quitEvent) {
        plugin.endSession(quitEvent.getPlayer());
    }
}
