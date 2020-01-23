package com.itis.javalab.services.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import static com.itis.javalab.services.token.TokenCreator.algorithm;

public class TokenVerifyHelper {
    public static DecodedJWT verify(String token) {
        DecodedJWT jwt;
        try {
            JWTVerifier jwtVerifier = JWT.require(algorithm)
                    .build();
            jwt = jwtVerifier.verify(token);

        } catch (JWTVerificationException e) {
            throw new IllegalStateException(e);
        }
        return jwt;
    }
}
