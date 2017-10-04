package com.github.games647.securemyaccount;

import java.awt.image.BufferedImage;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

class ImageRenderer extends MapRenderer {

    private final UUID forPlayer;
    private BufferedImage image;

    public ImageRenderer(Player player, BufferedImage image) {
        super(true);

        //just save the uuid in order to prevent memory leaks by keeping the player reference
        this.forPlayer = player.getUniqueId();
        this.image = image;
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        //the image is just for the player who requested a new key
        if (image != null && player.getUniqueId().equals(forPlayer)) {
            canvas.drawImage(0, 0, image);
            //release ressources in order to prevent memory leaks
            image = null;
        }
    }
}
