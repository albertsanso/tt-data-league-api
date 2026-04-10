package org.cttelsamicsterrassa.data.api.rest.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cttelsamicsterrassa.data.api.rest.config.security.JwtService;
import org.cttelsamicsterrassa.data.api.rest.config.security.LoginResponse;
import org.cttelsamicsterrassa.data.api.rest.config.security.TokenBlacklistService;
import org.cttelsamicsterrassa.data.core.domain.model.auth.User;
import org.cttelsamicsterrassa.data.core.domain.service.auth.AuthenticationService;
import org.cttelsamicsterrassa.data.core.domain.service.auth.InvalidCredentialsException;
import org.cttelsamicsterrassa.data.core.domain.service.auth.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private JwtService jwtService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        AuthController controller = new AuthController();
        ReflectionTestUtils.setField(controller, "authenticationService", authenticationService);
        ReflectionTestUtils.setField(controller, "tokenBlacklistService", tokenBlacklistService);
        ReflectionTestUtils.setField(controller, "jwtService", jwtService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void registerReturnsCreated() throws Exception {
        User user = User.createNew("demo", "demo@example.com", "Password1!");
        when(authenticationService.registerUser("demo", "demo@example.com", "Password1!")).thenReturn(user);

        RegisterRequest req = new RegisterRequest("demo", "demo@example.com", "Password1!");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("demo"));
    }

    @Test
    void registerReturnsConflictWhenUserExists() throws Exception {
        when(authenticationService.registerUser(anyString(), anyString(), anyString()))
                .thenThrow(new UserAlreadyExistsException("already exists"));

        RegisterRequest req = new RegisterRequest("demo", "demo@example.com", "Password1!");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("already exists"));
    }

    @Test
    void loginReturnsTokenOnSuccess() throws Exception {
        User user = User.createNew("demo", "demo@example.com", "Password1!");
        when(authenticationService.authenticateUser("demo", "pwd")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("demo")).thenReturn("token-abc");

        LoginRequest req = new LoginRequest("demo", "pwd");
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-abc"))
                .andExpect(jsonPath("$.username").value("demo"));
    }

    @Test
    void loginReturnsUnauthorizedWhenCredentialsInvalid() throws Exception {
        when(authenticationService.authenticateUser(anyString(), anyString()))
                .thenThrow(new InvalidCredentialsException());

        LoginRequest req = new LoginRequest("demo", "wrong");
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void loginReturnsUnauthorizedWhenEmptyOptional() throws Exception {
        when(authenticationService.authenticateUser(anyString(), anyString()))
                .thenReturn(Optional.empty());

        LoginRequest req = new LoginRequest("demo", "wrong");
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void logoutReturnsBadRequestWhenHeaderMissing() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void logoutReturnsOkAndBlacklistsToken() throws Exception {
        Date expiry = new Date(System.currentTimeMillis() + 60_000);
        when(jwtService.extractExpiration("token-1")).thenReturn(expiry);
        doNothing().when(tokenBlacklistService).blacklistToken("token-1", expiry);

        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer token-1"))
                .andExpect(status().isOk());

        verify(tokenBlacklistService).blacklistToken("token-1", expiry);
    }
}
