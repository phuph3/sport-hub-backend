package com.badminton.platform.config;

import com.badminton.platform.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtFilter implements Filter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        String authHeader = req.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.replace("Bearer ", "");

            try {
                Claims claims = jwtService.validateToken(token);

                //  gắn user vào request
                req.setAttribute("userId", Long.parseLong(claims.getSubject()));
                req.setAttribute("email", claims.get("email"));

            } catch (Exception e) {
                System.out.println("❌ JWT INVALID");
            }
        }

        chain.doFilter(request, response);
    }
}