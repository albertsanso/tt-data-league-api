package org.cttelsamicsterrassa.data.api.rest.auth;

import org.cttelsamicsterrassa.data.api.runtime.config.security.LoginResponse;
import org.cttelsamicsterrassa.data.api.runtime.config.security.UserService;
import org.cttelsamicsterrassa.data.api.runtime.config.security.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.cttelsamicsterrassa.data.api.rest.ControllerConfig.API_BASE_PATH_V1;

@RestController
@RequestMapping(API_BASE_PATH_V1 + "/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Users> register(@RequestBody Users user) {
        try {
            Users registeredUser = userService.register(user);
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users user) {
        try {
            LoginResponse response = userService.verify(user);
            if (response != null) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorMessage("Invalid username or password"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorMessage("Authentication failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            userService.logout(token);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    // Simple error message DTO
    static class ErrorMessage {
        public String message;

        public ErrorMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}



