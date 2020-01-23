package com.itis.javalab.services.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.itis.javalab.models.User;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenCreator {
    private static final String SECRET = "qpalmz"; //key
    static Algorithm algorithm = Algorithm.HMAC256(SECRET);

    public static String createToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("algorithm", "HS256");
        Date now = new Date(System.currentTimeMillis());
        String token = JWT.create()
                .withExpiresAt(new Date(now.getTime() + 15 * 60000))
                .withHeader(claims)
                .withClaim("id", user.getId())
                .withClaim("nickName", user.getUserName())
                .withClaim("role", user.getRole())
                .sign(algorithm);
        return token;
    }
}
