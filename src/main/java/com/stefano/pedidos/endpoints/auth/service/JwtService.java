package com.stefano.pedidos.endpoints.auth.service;

import com.stefano.pedidos.config.model.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.stefano.pedidos.util.PedidoContantes.PERSON_ID_TOKEN;
import static com.stefano.pedidos.util.PedidoContantes.ROLES_TOKEN;

@Service
public class JwtService {

    @Value("${seguraca.jwt.secret}")
    private String jwtSecret;

    @Value("${seguraca.jwt.expiration}")
    private Long jwtExpiration;

    @Value("${seguraca.jwt.refresh-expiration}")
    private Long refreshExpiration;

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String gerarAccessToken(UserPrincipal user) {

        Map<String, Object> claims = montarClaims(user);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignKey())
                .compact();
    }

    public Map<String, Object> montarClaims(UserPrincipal user) {
        Map<String, Object> claims = new HashMap<>();

        claims.put(PERSON_ID_TOKEN, user.getUsuarioId());

        List<String> roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        if (!roles.isEmpty()) {
            claims.put(ROLES_TOKEN, roles);
        }

        return claims;
    }

    public String gerarRefreshToken(UserDetails user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSignKey())
                .compact();
    }

    public String extrairUsuario(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {

        final String username = extrairUsuario(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // MÉTODO GENÉRICO PARA EXTRAIR CLAIM
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // EXTRAI TODAS AS CLAIMS DO TOKEN
    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
