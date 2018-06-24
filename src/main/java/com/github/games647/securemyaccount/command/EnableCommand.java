package com.github.games647.securemyaccount.command;

import com.github.games647.securemyaccount.Account;
import com.github.games647.securemyaccount.ImageGenerator;
import com.github.games647.securemyaccount.SecureMyAccount;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnableCommand implements CommandExecutor {

    private final SecureMyAccount plugin;

    public EnableCommand(SecureMyAccount plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            generateKey(player);
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "You have to be a player in order to receive a map item");
        }

        return true;
    }

    private void generateKey(Player player) {
        Account account = plugin.getOrLoadAccount(player);
        if (account.isRegistered()) {
            player.sendMessage(ChatColor.DARK_RED + "You have already a secret key");
            return;
        }

        String secretKey = plugin.getTotp().generateSecretKey();
        account.setSecretCode(secretKey);
        account.setIP(player.getAddress().getAddress());
        if (!plugin.saveAccount(player)) {
            player.sendMessage(ChatColor.DARK_RED + "Error while saving your secret key");
            return;
        }

        String serverIp = plugin.getConfig().getString("serverIp");
        if (serverIp.isEmpty()) {
            serverIp = Bukkit.getIp();
        }

        Runnable imageDownloader = new ImageGenerator(plugin, player, serverIp, secretKey);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, imageDownloader);
        player.sendMessage(ChatColor.DARK_GREEN + "Queued generation of your secret key");
    }
}
