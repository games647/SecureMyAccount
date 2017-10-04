package com.github.games647.securemyaccount.commands;

import com.github.games647.securemyaccount.Account;
import com.github.games647.securemyaccount.ImageDownloader;
import com.github.games647.securemyaccount.SecureMyAccount;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

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
        account.setIp(player.getAddress().getHostString());
        if (!plugin.saveAccount(player)) {
            player.sendMessage(ChatColor.DARK_RED + "Error while saving your secret key");
            return;
        }

        try {
            String serverIp = plugin.getConfig().getString("serverIp");
            if (serverIp.isEmpty()) {
                serverIp = Bukkit.getIp();
            }

            URL barcodeUrl = new URL(plugin.getTotp().getQRBarcodeURL(player.getName(), serverIp, secretKey));

            Runnable imageDownloader = new ImageDownloader(plugin, player, barcodeUrl);
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, imageDownloader);
            player.sendMessage(ChatColor.DARK_GREEN + "Queued generation of your secret key");
        } catch (MalformedURLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Tried creating barcode url", ex);
            player.sendMessage(ChatColor.DARK_RED + "Error occurred while creating the image url");
        }
    }
}
