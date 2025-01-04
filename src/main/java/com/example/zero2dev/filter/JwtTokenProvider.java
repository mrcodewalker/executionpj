package com.example.zero2dev.filter;

import com.example.zero2dev.exceptions.ValueNotValidException;
import com.example.zero2dev.models.User;
import com.example.zero2dev.models.UserSession;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.InvalidParameterException;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final String jwtSecret = "Z54uiPhveohL/uORp8a8rHhu0qalR4Mj+aIOz5ZA5zY=";
    private final long jwtExpiration = 8640000L;
    private final PasswordEncoder passwordEncoder;

    public String generateToken(UserSession userSession, User user) throws InvalidParameterException {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sid", userSession.getSessionId());
        claims.put("role", user.getRole().getRoleName());
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        try{
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setSubject(user.getUsername())
                    .setExpiration(expiryDate)
                    .signWith(SignatureAlgorithm.HS256, getSigninKey())
                    .setIssuer("Zero2Dev x Mr.CodeWalker")
                    .setAudience("web-client")
                    .compact();
            return token;
        } catch (Exception e){
            throw new ValueNotValidException(e.getMessage());
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public final String getTokenFromRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String authorizationHeader = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                return authorizationHeader.substring(7);
            }
        }
        return null;
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
    public boolean checkExtractToken(String token, String username){
        if (this.getUsernameFromToken(token).equalsIgnoreCase(username)){
            return true;
        }
        return false;
    }
    public final Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(this.getSigninKey())  // Thay bằng key của bạn
                .parseClaimsJws(token)
                .getBody();
    }
    public final String getSessionIdFromToken() {
        String token = this.getTokenFromRequest();
        Claims claims = extractClaims(token);
        return (String) claims.get("sid");
    }
    public Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }
    public String extractUserName(String token){
        return this.extractClaim(token, Claims::getSubject);
    }

    private Key getSigninKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigninKey())
                .parseClaimsJws(token)
                .getBody();
    }
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    public boolean isTokenExpired(String token){
        Date expirationDate = this.extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());

    }
    public String getSessionIdFromToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims != null ? (String) claims.get("sid") : null;
        } catch (ExpiredJwtException e) {
            return e.getClaims().get("sid", String.class);
        } catch (Exception e) {
            return null;
        }
    }
}

