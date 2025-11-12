package com.iot.common.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author IoT Platform
 */
public class JwtUtils {

    /**
     * 密钥（实际项目中应该从配置文件读取）
     */
    private static final String SECRET = "iot-platform-secret-key-for-jwt-token-generation-2024";

    /**
     * 过期时间（7天，单位：秒）
     */
    private static final long EXPIRATION = 7 * 24 * 3600;

    /**
     * 生成密钥
     */
    private static SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成Token
     *
     * @param claims 自定义声明
     * @return Token字符串
     */
    public static String createToken(Map<String, Object> claims) {
        return createToken(claims, EXPIRATION);
    }

    /**
     * 生成Token（自定义过期时间）
     *
     * @param claims     自定义声明
     * @param expiration 过期时间（秒）
     * @return Token字符串
     */
    public static String createToken(Map<String, Object> claims, long expiration) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expirationDate = new Date(nowMillis + expiration * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成Token（指定subject）
     *
     * @param subject 主题（通常是用户名或用户ID）
     * @param claims  自定义声明
     * @return Token字符串
     */
    public static String createToken(String subject, Map<String, Object> claims) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expirationDate = new Date(nowMillis + EXPIRATION * 1000);

        return Jwts.builder()
                .setSubject(subject)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析Token
     *
     * @param token Token字符串
     * @return Claims
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token Token字符串
     * @return 用户ID
     */
    public static String getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", String.class);
    }

    /**
     * 从Token中获取用户名
     *
     * @param token Token字符串
     * @return 用户名
     */
    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 验证Token是否过期
     *
     * @param token Token字符串
     * @return true已过期，false未过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 验证Token是否有效
     *
     * @param token Token字符串
     * @return true有效，false无效
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 刷新Token
     *
     * @param token 旧Token
     * @return 新Token
     */
    public static String refreshToken(String token) {
        Claims claims = parseToken(token);
        claims.remove(Claims.ISSUED_AT);
        claims.remove(Claims.EXPIRATION);
        return createToken(claims);
    }
}