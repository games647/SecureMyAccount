package com.github.games647.securemyaccount.commands;

import com.github.games647.securemyaccount.ImageDownloader;
import com.github.games647.securemyaccount.SecureMyAccount;
import com.github.games647.securemyaccount.TOTP;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateKeyCommand implements CommandExecutor {

    private final SecureMyAccount plugin;

    public CreateKeyCommand(SecureMyAccount plugin) {
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
        File keyStorage = new File(plugin.getDataFolder(), player.getUniqueId().toString());
        if (keyStorage.exists()) {
            player.sendMessage(ChatColor.DARK_RED + "You have already a secret key");
            return;
        }

        String secretKey = TOTP.generateSecretKey();
        if (!saveKey(keyStorage, secretKey)) {
            player.sendMessage(ChatColor.DARK_RED + "Error while saving your secret key");
            return;
        }

        try {
            String serverIp = plugin.getConfig().getString("serverIp");
            if (serverIp.isEmpty()) {
                serverIp = Bukkit.getIp();
            }

            URL barcodeUrl = new URL(TOTP.getQRBarcodeURL(player.getName(), serverIp, secretKey));

            ImageDownloader imageDownloader = new ImageDownloader(plugin, player, barcodeUrl);
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, imageDownloader);
            player.sendMessage(ChatColor.DARK_GREEN + "Queued generation of your secret key");
        } catch (MalformedURLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Tried creating barcode url", ex);
            player.sendMessage(ChatColor.DARK_RED + "Error occurred while creating the image url");
        }
    }

    private boolean saveKey(File file, String secretKey) {
        BufferedWriter fileWriter = null;
        try {
            file.createNewFile();

            fileWriter = Files.newWriter(file, Charsets.UTF_8);
            fileWriter.write(secretKey);
            fileWriter.flush();
            return true;
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Tried saving your secret key", ex);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException ex) {
                    plugin.getLogger().log(Level.SEVERE, "Tried closing file", ex);
                }
            }
        }

        return false;
    }
}
