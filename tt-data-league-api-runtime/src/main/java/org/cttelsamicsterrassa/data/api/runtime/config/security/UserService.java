package org.cttelsamicsterrassa.data.api.runtime.config.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private TokenBlacklistService blacklistService;

    @Autowired
    private UserRepo repo;


    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public Users register(Users user) {
        user.setPassword(encoder.encode(user.getPassword()));
        repo.save(user);
        return user;
    }

    public String verify(Users user) {
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(user.getUsername());
        } else {
            return "fail";
        }
    }

    public void logout(String rawToken) {
        if (rawToken == null) return;
        // rawToken should be the JWT string (without "Bearer ")
        Date expiry;
        try {
            expiry = jwtService.extractExpiration(rawToken);
        } catch (Exception e) {
            // invalid token -> nothing to blacklist
            return;
        }
        blacklistService.blacklistToken(rawToken, expiry);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistService.isBlacklisted(token);
    }
}
