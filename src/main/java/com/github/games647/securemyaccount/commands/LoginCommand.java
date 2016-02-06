package com.github.games647.securemyaccount.commands;

import com.github.games647.securemyaccount.SecureMyAccount;
import com.github.games647.securemyaccount.TOTP;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoginCommand implements CommandExecutor {

    private final SecureMyAccount plugin;

    public LoginCommand(SecureMyAccount plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length > 0) {
                checkCode((Player) sender, args[0]);
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "Your time based password code is missing");
            }
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have to start a session, you're not a player");
        }

        return true;
    }

    private void checkCode(Player player, String code) {
        File keyStorage = new File(plugin.getDataFolder(), player.getUniqueId().toString());
        if (!keyStorage.exists()) {
            player.sendMessage(ChatColor.DARK_RED + "You don't have key generated yet");
            return;
        }

        try {
            String firstLine = Files.readFirstLine(keyStorage, Charsets.UTF_8);
            if (TOTP.checkPassword(firstLine, code)) {
                player.sendMessage(ChatColor.DARK_GREEN + "Accepted. You can continue");
                plugin.startSession(player);
            } else {
                player.sendMessage(ChatColor.DARK_RED + "Incorrect password");
            }
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Tried reading secret key", ex);
        } catch (Exception ex) {
            Logger.getLogger(LoginCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
