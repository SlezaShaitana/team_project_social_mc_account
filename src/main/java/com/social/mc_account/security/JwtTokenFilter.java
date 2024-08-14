package com.social.mc_account.security;

import com.social.mc_account.feign.JwtValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final JwtValidation jwtValidation;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getToken(request);
            log.info("token: {}", token);
            if (token != null && jwtValidation.validateToken(token)) {
                String email = jwtUtils.getEmail(token);
                List<String> roles = jwtUtils.getRoles(token);

                log.info("email: {}", email);
                log.info("roles: {}", roles);

                Collection<? extends GrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        email, null, authorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("JWT token validation failed");
        }
        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String headerAuth = request.getHeader("Set-Cookie");


        log.info("headerAuth: {}", headerAuth);
        // Разделяем строку по разделителю ";"
        String[] parts = headerAuth.split(";");

        // Находим часть, содержащую Refresh_token
        for (String part : parts) {
            if (part.trim().startsWith("Refresh_token=")) {
                // Извлекаем значение токена
                log.info("Refresh token: {}", part);
                String token = part.split("=")[1].trim();
                System.out.println("Refresh_token: " + token);
                return token;
            }
        }
        return null;
    }
}