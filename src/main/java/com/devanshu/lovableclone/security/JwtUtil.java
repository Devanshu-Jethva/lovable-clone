package com.devanshu.lovableclone.security;

import com.devanshu.lovableclone.config.properties.JwtPropertiesConfig;
import com.devanshu.lovableclone.entity.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JwtUtil {

    JwtPropertiesConfig jwtPropertiesConfig;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtPropertiesConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Users users) {
        return Jwts.builder()
                   .subject(users.getUsername())
                   .claim("userId", users.getId())
//                   .claim("roles", users.getAuthorities()
//                                        .stream()
//                                        .map(GrantedAuthority::getAuthority)
//                                        .toList())
                   .issuedAt(new Date())
                   .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10 * 60)) // Token valid for 1 hour
                   .signWith(getSecretKey())
                   .compact();
    }

    public JwtUserPrincipal verifyToken(String token) {
        Claims claims = Jwts.parser()
                            .verifyWith(getSecretKey())
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();

        Long userId = claims.get("userId", Long.class);
        String username = claims.getSubject();

//        @SuppressWarnings("unchecked")
//        List<String> roles = claims.get("roles", List.class);
//        List<? extends GrantedAuthority> authorities = roles.stream()
//                                                            .map(SimpleGrantedAuthority::new)
//                                                            .toList();

        return new JwtUserPrincipal(userId, username, List.of());
    }

}
