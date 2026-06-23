package com.badminton.platform.service;

import com.badminton.platform.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.badminton.platform.entity.User;
import com.badminton.platform.dto.RegisterRequest;
import com.badminton.platform.dto.AuthResponse;
import java.time.LocalDateTime;
import com.badminton.platform.dto.LoginRequest;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public AuthResponse login(LoginRequest req) {

        // 1. tìm user theo email
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
                

        // 2. check account type (tránh Google login bằng mật khẩu)
        if (!"LOCAL".equals(user.getProvider())) {
            throw new RuntimeException("USE_GOOGLE_LOGIN");
        }

        // 3. check password (bcrypt)
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("INVALID_PASSWORD");
        }

        // 4. tạo token
        String token = jwtService.generateToken(user.getId());

        // 5. trả về token + user
        return new AuthResponse(token, user);
    }

    public AuthResponse register(RegisterRequest req) {

        // 1. check email tồn tại
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // 2. tạo user
        User user = new User();
        user.setEmail(req.getEmail());
        user.setNickname(req.getNickname());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setProvider("LOCAL");
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        // 3. tạo token
        String token = jwtService.generateToken(user.getId());

        // 4. trả về user + token
        return new AuthResponse(token, user);

    }

}