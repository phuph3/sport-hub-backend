package com.badminton.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //  QUAN TRỌNG NHẤT
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) //  disable CSRF cho test API
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() //  cho phép auth APIs
                .anyRequest().permitAll() //  tạm thời cho hết
            );

        return http.build();
    }
}