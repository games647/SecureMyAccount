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

    public String generateSecretKey() {
        return gAuth.createCredentials().getKey();
    }

    public boolean checkPassword(String secretKey, String userInput) {
        Integer code = Ints.tryParse(userInput);
        return code != null && gAuth.authorize(secretKey, code);
    }
}
