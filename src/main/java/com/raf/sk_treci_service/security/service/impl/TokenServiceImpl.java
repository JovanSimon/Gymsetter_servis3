package com.raf.sk_treci_service.security.service.impl;

import com.raf.sk_treci_service.security.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

@Service
public class TokenServiceImpl implements TokenService {

    @Value("${oauth.jwt.secret}")
    private String base64Secret;



    @Override
    public String generate(Claims claims) {
        Key secretKey = new SecretKeySpec(base64Secret.getBytes(), SignatureAlgorithm.HS512.getJcaName());

        return Jwts.builder()  // Postavite podatke koji vam trebaju
                .setClaims(claims)// Postavite vaš objekat kao claim
                .signWith(secretKey)  // Postavite svoj tajni ključ za potpisivanje
                .compact();
    }

    @Override
    public Claims parseToken(String jwt) {
        Key secretKey = new SecretKeySpec(base64Secret.getBytes(), SignatureAlgorithm.HS512.getJcaName());
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
