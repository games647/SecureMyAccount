package com.github.games647.securemyaccount;

import java.util.UUID;

public class Account {

    private final UUID uuid;
    private String secretCode;
    private String ip;

    public Account(UUID uuid) {
        this.uuid = uuid;
    }

    public Account(UUID uuid, String secretCode, String ip) {
        this.uuid = uuid;
        this.secretCode = secretCode;
        this.ip = ip;
    }

    public UUID getUuid() {
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
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
