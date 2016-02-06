package com.github.games647.securemyaccount;

import java.awt.image.BufferedImage;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

public class MapGiver implements Runnable {

    private final SecureMyAccount plugin;
    private final Player player;
    private final BufferedImage resultImage;

    public MapGiver(SecureMyAccount plugin, Player player, BufferedImage resultImage) {
        this.plugin = plugin;
        this.player = player;
        this.resultImage = resultImage;
    }

    @Override
    public void run() {
        MapView createdView = plugin.installRenderer(player, resultImage);
        //stack count 0 prevents the item from being dropped
        ItemStack mapItem = new ItemStack(Material.MAP, 0, createdView.getId());
        player.getInventory().addItem(mapItem);
        player.sendMessage(ChatColor.DARK_GREEN + "Here is your secret code. Just scan it with your phone");
        player.sendMessage(ChatColor.DARK_GREEN + "Then drop it in order to remove it");
    }
}
