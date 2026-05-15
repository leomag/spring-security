package ru.itmo.springsecurity.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokensUtils {

    @Value("${jwt.secret}")
    public String secret;

    @Value("${jwt.lifetime}")
    public Duration jwtLifetime;

    public String generateToken(UserDetails userDetails) {
        HashMap<String, Object> claims = new HashMap<>();
        var rolesList = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", rolesList);

        var issuedDate = Instant.now();
        var expiredDate = issuedDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + jwtLifetime.toMillis();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(issuedDate.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(Instant.ofEpochSecond(expiredDate)))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String getUsername(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public List<String> getRoles(String token) {
        List<?> rawList = getAllClaimsFromToken(token).get("roles", List.class);
        if (rawList == null) {
            return Collections.emptyList();
        }
        List<String> roles = new ArrayList<>();
        for (Object obj : rawList) {
            if (obj instanceof String) {
                roles.add((String) obj);
            } else {
            // handle unexpected type – log or throw
            throw new IllegalStateException("Invalid role type found");
            }
        }
        return Collections.unmodifiableList(roles);
    }
    


    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}

