package com.github.games647.securemyaccount;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ImageGenerator implements Runnable {

    private static final String PREFIX = "otpauth://totp/";

    private final SecureMyAccount plugin;
    private final Player player;

    private final String secret;
    private final String serverHost;

    public ImageGenerator(SecureMyAccount plugin, Player player, String serverHost, String secret) {
        this.plugin = plugin;
        this.player = player;
        this.secret = secret;
        this.serverHost = serverHost;
    }

    @Override
    public void run() {
        Writer qrWriter = new QRCodeWriter();
        try {
            //generate
            String contents = PREFIX + player.getName() + '@' + serverHost + "?secret=" + secret;
            BitMatrix encode = qrWriter.encode(contents, BarcodeFormat.QR_CODE, 200, 200);
            BufferedImage resultImage = MatrixToImageWriter.toBufferedImage(encode);

            //reschedule to the main thread to run non thread-safe methods
            Bukkit.getScheduler().runTask(plugin, new MapGiver(player, resultImage));
        } catch (WriterException writeEx) {
            plugin.getLogger().log(Level.SEVERE, "Tried downloading image", writeEx);
        }
    }
}
