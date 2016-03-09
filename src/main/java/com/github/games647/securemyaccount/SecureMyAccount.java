package com.github.games647.securemyaccount;

import com.github.games647.securemyaccount.commands.CreateKeyCommand;
import com.github.games647.securemyaccount.commands.LoginCommand;
import com.github.games647.securemyaccount.listener.PreventListener;
import com.github.games647.securemyaccount.listener.SessionListener;
import com.google.common.collect.Sets;

import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SecureMyAccount extends JavaPlugin {

    private final Set<UUID> sessions = Sets.newHashSet();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        //register commands
        getCommand(getName().toLowerCase()).setExecutor(new CreateKeyCommand(this));
        getCommand("startsession").setExecutor(new LoginCommand(this));

        //register listeners
        getServer().getPluginManager().registerEvents(new PreventListener(this), this);
        getServer().getPluginManager().registerEvents(new SessionListener(this), this);
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
