package com.example.playpal.auth.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.StringReader;
import java.security.PrivateKey;
import java.security.PublicKey;

@Configuration
public class TokenConfiguration {
    @Value("${jwt.PRIVATE_KEY}")
    private String privateKeyStr;


    @Value("${jwt.ISSUER}")
    @Getter
    private String issuer;
    @Getter
    private final long accessTokenValiditySeconds = 60 * 60; // 1 hour
    @Getter
    private final long refreshTokenValiditySeconds = 60 * 60 * 24 * 3; // 3 days
    @Getter
    private final String tokenHeaderPrefix = "Bearer ";
    @Getter
    private PrivateKey privateKey;
    @Getter
    private PublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {
        var pemKeyPair = (PEMKeyPair) new PEMParser(new StringReader(privateKeyStr)).readObject();
        var keyPair = new JcaPEMKeyConverter().getKeyPair(pemKeyPair);
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }
}
