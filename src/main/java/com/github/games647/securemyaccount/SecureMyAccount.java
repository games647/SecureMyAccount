package com.github.games647.securemyaccount;

import com.github.games647.securemyaccount.commands.CreateKeyCommand;
import com.github.games647.securemyaccount.commands.LoginCommand;
import com.github.games647.securemyaccount.listener.PreventListener;
import com.github.games647.securemyaccount.listener.SessionListener;
import com.google.common.collect.Sets;

import java.awt.image.BufferedImage;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

public class SecureMyAccount extends JavaPlugin {

    private final Set<UUID> sessions = Sets.newHashSet();

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            //create the basic folder
            getDataFolder().mkdir();
        }

        //register commands
        getCommand(getName().toLowerCase()).setExecutor(new CreateKeyCommand(this));
        getCommand("startsession").setExecutor(new LoginCommand(this));

        //register listeners
        getServer().getPluginManager().registerEvents(new PreventListener(this), this);
        getServer().getPluginManager().registerEvents(new SessionListener(this), this);
    }

    public MapView installRenderer(Player player, BufferedImage image) {
        MapView mapView = Bukkit.createMap(player.getWorld());
        for (MapRenderer mapRenderer : mapView.getRenderers()) {
            mapView.removeRenderer(mapRenderer);
        }

        mapView.addRenderer(new ImageRenderer(player, image));
        return mapView;
    }

    public void startSession(Player player) {
        sessions.add(player.getUniqueId());
    }

    public boolean isInSession(Player player) {
        return sessions.contains(player.getUniqueId());
    }

    public void endSession(Player player) {
        sessions.remove(player.getUniqueId());
    }
}
