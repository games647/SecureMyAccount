package com.github.games647.securemyaccount.commands;

import com.github.games647.securemyaccount.Account;
import com.github.games647.securemyaccount.SecureMyAccount;
import com.github.games647.securemyaccount.TOTP;

import java.util.logging.Level;

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
        Account account = plugin.getOrLoadAccount(player);
        if (!account.isRegistered()) {
            player.sendMessage(ChatColor.DARK_RED + "You don't have key generated yet");
            return;
        }

        try {
            if (TOTP.checkPassword(account.getSecretCode(), code)) {
                player.sendMessage(ChatColor.DARK_GREEN + "Accepted. You can continue");

                account.setIp(player.getAddress().getHostString());
                plugin.saveAccount(player);
                plugin.startSession(player);
            } else {
                player.sendMessage(ChatColor.DARK_RED + "Incorrect password");
            }
        } catch (Exception ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
        }
    }
}
