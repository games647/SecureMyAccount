package com.github.games647.securemyaccount;

import com.github.games647.securemyaccount.commands.EnableCommand;
import com.github.games647.securemyaccount.commands.LoginCommand;
import com.github.games647.securemyaccount.listener.InventoryPinListener;
import com.github.games647.securemyaccount.listener.PreventListener;
import com.github.games647.securemyaccount.listener.SessionListener;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SecureMyAccount extends JavaPlugin {

    private final Set<UUID> sessions = Sets.newConcurrentHashSet();
    private final Map<UUID, Account> cache = new ConcurrentHashMap<>();

    private final TOTP totp = new TOTP();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        //register commands
        getCommand(getName().toLowerCase()).setExecutor(new EnableCommand(this));
        getCommand("startsession").setExecutor(new LoginCommand(this));
        getCommand("unregister").setExecutor(new LoginCommand(this));

        //register listeners
        getServer().getPluginManager().registerEvents(new PreventListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryPinListener(this), this);
        getServer().getPluginManager().registerEvents(new SessionListener(this), this);
    }

    @Override
    public void onDisable() {
        sessions.clear();
        cache.clear();
    }

    public TOTP getTotp() {
        return totp;
    }

    //thread-safe
    public void startSession(Player player) {
        sessions.add(player.getUniqueId());
    }

    //thread-safe
    public boolean isInSession(Player player) {
        return sessions.contains(player.getUniqueId());
    }

    //thread-safe
    public void endSession(Player player) {
        sessions.remove(player.getUniqueId());
        cache.remove(player.getUniqueId());
    }

    //todo make async
    public Account getOrLoadAccount(Player player) {
        return cache.computeIfAbsent(player.getUniqueId(), uuid -> {
            Path file = getDataFolder().toPath().resolve(uuid.toString());
            if (Files.exists(file)) {
                try {
                    List<String> lines = Files.readAllLines(file);
                    String secretCode = lines.get(0);
                    String ip = lines.get(1);

                    return new Account(uuid, secretCode, ip);
                } catch (IOException ex) {
                    getLogger().log(Level.SEVERE, "Error loading account", ex);
                }
            } else {
                return new Account(uuid);
            }

            return null;
        });
    }

    //todo make async
    public boolean saveAccount(Player player) {
        UUID uniqueId = player.getUniqueId();
        Account account = cache.get(uniqueId);
        if (account != null) {
            Path file = getDataFolder().toPath().resolve(uniqueId.toString());
            try {
                Files.write(file, Lists.newArrayList(account.getSecretCode(), account.getIp()));
                return true;
            } catch (IOException ex) {
                getLogger().log(Level.SEVERE, "Error saving account", ex);
            }
        }

        return false;
    }
}
