package spl.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    // 1) 서명 키 (적당한 길이의 랜덤 시크릿)
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // 2) 토큰 유효 기간 (예: 1시간)
    private final long validityInMs = 3600_000;

    // 토큰 생성
    public String generateToken(String userId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validityInMs))
                .signWith(key)
                .compact();
    }

    // 토큰에서 userId 가져오기
    public String getUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
