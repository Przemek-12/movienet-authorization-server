package com.auth.infrastructure.security;

import java.io.IOException;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JWTFilter extends BasicAuthenticationFilter {

    private final KeyPair keyPair;

    public JWTFilter(AuthenticationManager authenticationManager, KeyPair keyPair) {
        super(authenticationManager);
        this.keyPair = keyPair;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        try {
            UsernamePasswordAuthenticationToken authResult = getAuthenticationByToken(token);
            SecurityContextHolder.getContext().setAuthentication(authResult);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized. Invalid token.");
            return;
        }
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthenticationByToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        RSAPublicKey publicKey = (RSAPublicKey) this.keyPair.getPublic();
        Claims claims = Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(token)
                .getBody();
        return new UsernamePasswordAuthenticationToken(
                null, new DefaultOAuth2AccessToken(token), null);
    }
}
