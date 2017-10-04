package com.github.games647.securemyaccount;

import com.google.common.primitives.Ints;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder;
import com.warrenstrange.googleauth.HmacHashFunction;
import com.warrenstrange.googleauth.IGoogleAuthenticator;
import com.warrenstrange.googleauth.KeyRepresentation;

public class TOTP {

    private final IGoogleAuthenticator gAuth = new GoogleAuthenticator(new GoogleAuthenticatorConfigBuilder()
            .setHmacHashFunction(HmacHashFunction.HmacSHA512)
            .setKeyRepresentation(KeyRepresentation.BASE64)
            .build());

    private static final String URL_FORMAT = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl="
            + "otpauth://totp/"
            + "%s@%s%%3Fsecret%%3D%s";

    public String generateSecretKey() {
        return gAuth.createCredentials().getKey();
    }

    public String getQRBarcodeURL(String user, String host, String secret) {
        return String.format(URL_FORMAT, user, host, secret);
    }

    public boolean checkPassword(String secretKey, String userInput) {
        Integer code = Ints.tryParse(userInput);
        return code != null && gAuth.authorize(secretKey, code);
    }
}
