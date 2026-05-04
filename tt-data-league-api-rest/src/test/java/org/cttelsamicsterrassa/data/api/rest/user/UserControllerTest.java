package org.cttelsamicsterrassa.data.api.rest.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cttelsamicsterrassa.data.core.domain.model.auth.User;
import org.cttelsamicsterrassa.data.core.domain.repository.auth.UserRepository;
import org.cttelsamicsterrassa.data.core.domain.service.auth.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        UserController controller = new UserController(authenticationService, userRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void listUsersReturnsOkWithMappedDtos() throws Exception {
        UUID id = UUID.randomUUID();
        User user = User.createExisting(id, "alice", "alice@example.com", "hash", null, true, Set.of());
        when(userRepository.findAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("alice"))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void assignRoleReturnsOkOnSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(authenticationService).assignRole(eq(id), anyString());

        AssignRoleRequest request = new AssignRoleRequest("ADMIN");
        mockMvc.perform(post("/api/v1/users/" + id + "/assign-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(authenticationService).assignRole(id, "ADMIN");
    }

    @Test
    void assignRoleReturnsNotFoundWhenUserMissing() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new IllegalArgumentException("User not found"))
                .when(authenticationService).assignRole(any(), anyString());

        AssignRoleRequest request = new AssignRoleRequest("ADMIN");
        mockMvc.perform(post("/api/v1/users/" + id + "/assign-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void disableUserReturnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(authenticationService).disableUser(id);

        mockMvc.perform(put("/api/v1/users/" + id + "/disable"))
                .andExpect(status().isOk());

        verify(authenticationService).disableUser(id);
    }

    @Test
    void enableUserReturnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(authenticationService).enableUser(id);

        mockMvc.perform(put("/api/v1/users/" + id + "/enable"))
                .andExpect(status().isOk());

        verify(authenticationService).enableUser(id);
    }
}

