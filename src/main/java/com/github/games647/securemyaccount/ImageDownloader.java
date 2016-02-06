package com.github.games647.securemyaccount;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ImageDownloader implements Runnable {

    private final SecureMyAccount plugin;
    private final Player player;
    private final URL targetImage;

    public ImageDownloader(SecureMyAccount plugin, Player player, URL targetImage) {
        this.plugin = plugin;
        this.player = player;
        this.targetImage = targetImage;
    }

    @Override
    public void run() {
        try {
            //download image
            BufferedImage resultImage = ImageIO.read(targetImage);
            //reschedule to the main thread to run non thread-safe methods
            Bukkit.getScheduler().runTask(plugin, new MapGiver(plugin, player, resultImage));
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Tried downloading image", ex);
        }
    }
}
