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

    //  FINAL CONFIG (CLEAN)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ❌ disable csrf cho API
            .csrf(csrf -> csrf.disable())

            // ❌ KHÔNG cần cors() nữa (đã xử lý bằng GlobalCorsFilter)
            // .cors(cors -> {}) ❌ REMOVE nếu có

            .authorizeHttpRequests(auth -> auth
                //  cho phép login Google
                .requestMatchers("/api/auth/**").permitAll()

                //  cho phép OPTIONS (preflight) — cực kỳ quan trọng
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                //  tạm mở hết (sau này tighten lại)
                .anyRequest().permitAll()
            );

        return http.build();
    }
}