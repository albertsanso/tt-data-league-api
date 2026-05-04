package org.cttelsamicsterrassa.data.api.rest.auth;

import org.cttelsamicsterrassa.data.api.rest.config.security.JwtService;
import org.cttelsamicsterrassa.data.api.rest.config.security.LoginResponse;
import org.cttelsamicsterrassa.data.api.rest.config.security.TokenBlacklistService;
import org.cttelsamicsterrassa.data.core.domain.model.auth.User;
import org.cttelsamicsterrassa.data.core.domain.service.auth.AuthenticationService;
import org.cttelsamicsterrassa.data.core.domain.service.auth.InvalidCredentialsException;
import org.cttelsamicsterrassa.data.core.domain.service.auth.UserAlreadyExistsException;
import org.cttelsamicsterrassa.data.core.domain.service.auth.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.cttelsamicsterrassa.data.api.rest.ControllerConfig.API_BASE_PATH_V1;

@RestController
@RequestMapping(API_BASE_PATH_V1 + "/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User registered = authenticationService.registerUser(
                    request.getUsername(), request.getEmail(), request.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new RegisteredUserResponse(registered.getUsername(), registered.getEmail()));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorMessage(e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Optional<User> authenticated = authenticationService.authenticateUser(
                    request.getUsername(), request.getPassword());
            if (authenticated.isPresent()) {
                User authedUser = authenticated.get();
                Set<String> roleNames = authedUser.getRoles() == null ? Set.of()
                        : authedUser.getRoles().stream()
                                .map(role -> role.getName())
                                .collect(Collectors.toSet());
                String token = jwtService.generateToken(authedUser.getUsername(), roleNames);
                return ResponseEntity.ok(new LoginResponse(token, authedUser.getUsername()));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorMessage("Invalid username or password"));
            }
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorMessage("Invalid username or password"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorMessage("Authentication failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(name = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                tokenBlacklistService.blacklistToken(token, jwtService.extractExpiration(token));
            } catch (Exception e) {
                // invalid token — nothing to blacklist
            }
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    static class ErrorMessage {
        public String message;

        public ErrorMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    static class RegisteredUserResponse {
        public String username;
        public String email;

        public RegisteredUserResponse(String username, String email) {
            this.username = username;
            this.email = email;
        }
    }
}
