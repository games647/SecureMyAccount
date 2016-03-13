package com.github.games647.securemyaccount.commands;

import com.github.games647.securemyaccount.Account;
import com.github.games647.securemyaccount.SecureMyAccount;
import com.github.games647.securemyaccount.TOTP;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LoginCommand implements CommandExecutor {

    private final SecureMyAccount plugin;

    public LoginCommand(SecureMyAccount plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (plugin.isInSession((Player) sender)) {
                sender.sendMessage(ChatColor.DARK_RED + "You're aready logged in");
            } else if (args.length > 0) {
                checkCode((Player) sender, args[0]);
            } else {
                if (plugin.getConfig().getBoolean("inventoryPin")) {
                    openCodeInventory((Player) sender);
                } else {
                    sender.sendMessage(ChatColor.DARK_RED + "Your time based password code is missing");
                }
            }
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have to start a session, you're not a player");
        }

        return true;
    }

    private void checkCode(Player player, String code) {
        if (plugin.isInSession(player)) {
            player.sendMessage(ChatColor.DARK_GREEN + "You're already loggedin");
            return;
        }

        Account account = plugin.getOrLoadAccount(player);
        if (!account.isRegistered()) {
            player.sendMessage(ChatColor.DARK_RED + "You don't have key generated yet");
            return;
        }

        String newIp = player.getAddress().getHostString();
        if (plugin.getConfig().getBoolean("forceSampleIp") && !account.getIp().equals(newIp)) {
            player.sendMessage(ChatColor.DARK_RED + "You don't have the same IP as last time");
            return;
        }

        try {
            if (TOTP.checkPassword(account.getSecretCode(), code)) {
                InventoryView openInventory = player.getOpenInventory();
                if (openInventory.getType() == InventoryType.PLAYER) {
                    openInventory.close();
                }

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

    private void openCodeInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(player, InventoryType.PLAYER, "Pin code");

        inventory.setItem(2, new ItemStack(Material.STONE, 0));

        inventory.setItem(3, new ItemStack(Material.STONE, 1));
        inventory.setItem(4, new ItemStack(Material.STONE, 2));
        inventory.setItem(5, new ItemStack(Material.STONE, 3));

        inventory.setItem(12, new ItemStack(Material.STONE, 4));
        inventory.setItem(13, new ItemStack(Material.STONE, 5));
        inventory.setItem(14, new ItemStack(Material.STONE, 6));

        inventory.setItem(21, new ItemStack(Material.STONE, 7));
        inventory.setItem(22, new ItemStack(Material.STONE, 8));
        inventory.setItem(23, new ItemStack(Material.STONE, 9));

        ItemStack cancelBlock = new ItemStack(Material.BARRIER, 1);
        setDisplayName(cancelBlock, ChatColor.DARK_RED + "Cancel");

        ItemStack removeBlock = new ItemStack(Material.REDSTONE_BLOCK, 1);
        setDisplayName(removeBlock, ChatColor.DARK_RED + "Remove");

        ItemStack enterBlock = new ItemStack(Material.SLIME_BALL, 1);
        setDisplayName(enterBlock, ChatColor.DARK_GREEN + "Enter");

        inventory.setItem(27, cancelBlock);
        inventory.setItem(28, removeBlock);
        inventory.setItem(35, enterBlock);

        player.openInventory(inventory);
    }

    private void setDisplayName(ItemStack item, String display) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(display);
        item.setItemMeta(itemMeta);
    }
}
