package com.example.zero2dev.filter;

import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.models.User;
import com.example.zero2dev.services.BlacklistedTokenService;
import com.example.zero2dev.services.IPSecurityService;
import com.example.zero2dev.services.TokenService;
import com.example.zero2dev.storage.MESSAGE;
import com.example.zero2dev.storage.TokenType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;
    private final IPSecurityService ipSecurityService;
    private final BlacklistedTokenService blacklistedTokenService;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider,
                          UserDetailsService userDetailsService,
                          TokenService tokenService,
                          IPSecurityService ipSecurityService,
                          BlacklistedTokenService blacklistedTokenService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
        this.ipSecurityService = ipSecurityService;
        this.blacklistedTokenService = blacklistedTokenService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (this.isByPassToken(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(JwtTokenFilter.createJsonResponse(401, MESSAGE.UNAUTHORIZED_ACCESS));
                return;
            }

            if (ipSecurityService.isIPBlacklisted(getClientIP(request))) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(JwtTokenFilter.createJsonResponse(403, MESSAGE.IP_BLACKLISTED));
                return;
            }

            final String token = authHeader.substring(7);
            if (this.blacklistedTokenService.isTokenBlacklisted(token)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(JwtTokenFilter.createJsonResponse(403, MESSAGE.TOKEN_BLACKLISTED));
                return;
            }

            if (!this.tokenService.validateToken(token, TokenType.ACCESS)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(JwtTokenFilter.createJsonResponse(401, MESSAGE.TOKEN_INVALID));
                return;
            }

            final String username = jwtTokenProvider.extractUserName(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User userDetails = (User) userDetailsService.loadUserByUsername(username);
                if (jwtTokenProvider.validateToken(token)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JwtTokenFilter.createJsonResponse(401, MESSAGE.GENERAL_ERROR));
        }
    }
    private boolean isByPassToken(@NonNull HttpServletRequest request) {
        String apiPrefix = "/api/v1";
        final List<Pair<String,String>> byPassTokens = List.of(
                Pair.of(String.format("%s/user/register", apiPrefix), "POST"),
                Pair.of(String.format("%s/user/login", apiPrefix), "POST"),
                Pair.of(String.format("%s/auth/verify", apiPrefix), "GET"),
                Pair.of(String.format("%s/user/verify", apiPrefix), "GET"),
                Pair.of(String.format("%s/contest/filter/all", apiPrefix), "GET"),
                Pair.of(String.format("%s/user/forgot-password", apiPrefix), "POST"),
                Pair.of(String.format("%s/user/reset-password", apiPrefix), "POST")
                );
        String requestPath = request.getServletPath();
        String requestMethod = request.getMethod();
        for (Pair<String, String> bypassToken : byPassTokens) {
            if (requestPath.contains(bypassToken.getFirst())
                    && requestMethod.equals(bypassToken.getSecond())) {
                return true;
            }
        }
        return false;
    }
    private String getClientIP(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
    public static String createJsonResponse(int status, String message) {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", status);
        responseMap.put("message", message);

        try {
            return objectMapper.writeValueAsString(responseMap);
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"status\": 500, \"message\": \"Error creating JSON response\"}";
        }
    }
}
