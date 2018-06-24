package com.github.games647.securemyaccount;

import java.net.InetAddress;
import java.util.UUID;

public class Account {

    private final UUID uuid;
    private String secretCode;
    private InetAddress ip;

    public Account(UUID uuid) {
        this.uuid = uuid;
    }

    public Account(UUID uuid, String secretCode, InetAddress ip) {
        this.uuid = uuid;
        this.secretCode = secretCode;
        this.ip = ip;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public boolean isRegistered() {
        return secretCode != null;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }

    public InetAddress getIP() {
        return ip;
    }

    public void setIP(InetAddress ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '{' +
                "uuid=" + uuid +
                ", ip='" + ip + '\'' +
                '}';
    }
}
