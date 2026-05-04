package org.cttelsamicsterrassa.data.api.rest.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    void generateTokenWithRolesAndExtractUsername() {
        String token = jwtService.generateToken("alice", Set.of("ADMIN"));
        assertThat(jwtService.extractUsername(token)).isEqualTo("alice");
    }

    @Test
    void generateTokenWithRolesAndExtractRoles() {
        String token = jwtService.generateToken("alice", List.of("ADMIN", "ANALYST"));
        List<String> roles = jwtService.extractRoles(token);
        assertThat(roles).containsExactlyInAnyOrder("ADMIN", "ANALYST");
    }

    @Test
    void generateTokenNoRolesReturnsEmptyRolesList() {
        String token = jwtService.generateToken("bob");
        List<String> roles = jwtService.extractRoles(token);
        assertThat(roles).isEmpty();
    }

    @Test
    void validateTokenReturnsTrueForValidToken() {
        String token = jwtService.generateToken("alice", Set.of("ADMIN"));
        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(
                        "alice", "pwd", List.of());
        assertThat(jwtService.validateToken(token, userDetails)).isTrue();
    }
}

