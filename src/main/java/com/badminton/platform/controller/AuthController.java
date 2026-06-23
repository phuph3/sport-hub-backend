package com.badminton.platform.controller;

import com.badminton.platform.dto.AuthResponse;
import com.badminton.platform.dto.GoogleLoginRequest;
import com.badminton.platform.dto.LoginRequest;
import com.badminton.platform.dto.RegisterRequest;
import com.badminton.platform.service.AuthService;
import com.badminton.platform.service.GoogleService;
import com.badminton.platform.service.JwtService;
import com.badminton.platform.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.gson.GsonFactory;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.badminton.platform.entity.User;
import com.badminton.platform.repository.UserFollowRepository;
import com.badminton.platform.repository.UserRepository;

import java.util.Map;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private GoogleService googleService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthService authService;

    @Autowired
    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {

        try {
            String token = request.getToken();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new com.google.api.client.http.javanet.NetHttpTransport(),
                    GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList("248980513097-e1btvc6qb7dj7e9j0go4bn8eilr2rp5v.apps.googleusercontent.com"))
                    .build();

            GoogleIdToken idToken = verifier.verify(token);

            if (idToken == null) {
                return ResponseEntity.badRequest().body("Invalid Google token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String name = (String) payload.get("name");

            //  tìm user theo email
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setEmail(email);
                        newUser.setNickname(name);
                        return userRepository.save(newUser);
                    });

            //  tạo JWT
            String jwt = jwtService.generateToken(user.getId());

            return ResponseEntity.ok(new AuthResponse(jwt, user));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Google login failed");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            @RequestHeader("Authorization") String authHeader) {

        try {
            // 1. lấy token (bỏ "Bearer ")
            String token = authHeader.replace("Bearer ", "");

            // 2. decode userId
            Long userId = jwtService.extractUserId(token);

            // 3. query DB
            User user = userService.getById(userId);

            // 4. trả về
            return ResponseEntity.ok(user);

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            return ResponseEntity.ok(authService.login(req));
        } catch (RuntimeException e) {

            String msg = e.getMessage();

            if ("USER_NOT_FOUND".equals(msg) || "INVALID_PASSWORD".equals(msg)) {
                return ResponseEntity.status(401).body(msg);
            }

            if ("USE_GOOGLE_LOGIN".equals(msg)) {
                return ResponseEntity.status(400).body(msg);
            }

            return ResponseEntity.status(500).body("Internal error");
        }
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest req) {
        return authService.register(req);
    }
}