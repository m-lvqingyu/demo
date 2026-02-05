package com.lv.demo.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lv
 */
public class JwtUtil {

    public static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    private static final long REFRESH_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;

    private static final String SECRET_KEY = "zXcVbNmAsDfGhJkLpOiUyTrEwQazSxDcFvGbHnJmKlP1o9I8U7Y6T5R";

    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * 生成Access Token
     */
    public static String generateAccessToken(Long userId, Long version) {
        return generateToken(userId, version, "access", EXPIRATION_TIME);
    }

    /**
     * 生成Refresh Token
     */
    public static String generateRefreshToken(Long userId, Long version) {
        return generateToken(userId, version, "refresh", REFRESH_EXPIRATION_TIME);
    }

    private static String generateToken(Long userId, Long version, String type, Long expiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("version", version);
        claims.put("type", type);
        return Jwts.builder().setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 验证Token
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 从Token中获取用户ID
     */
    public static String getUserIdFromToken(Claims claims) {
        return claims.get("userId", String.class);
    }

    public static Long getVersionFromToken(Claims claims) {
        return claims.get("version", Long.class);
    }

    /**
     * 从Token中获取Token类型
     */
    public static String getTokenType(Claims claims) {
        return claims.get("type", String.class);
    }

    /**
     * 获取Token过期时间
     */
    public static Date getExpirationDateFromToken(Claims claims) {
        return claims.getExpiration();
    }

    /**
     * 检查Token是否即将过期（在指定时间内）
     */
    public static boolean isTokenExpiringSoon(Claims claims, Long minutes) {
        Date expiration = getExpirationDateFromToken(claims);
        return expiration.before(new Date(System.currentTimeMillis() + minutes * 60 * 1000));
    }

    private static Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
