package org.cttelsamicsterrassa.data.api.rest.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.cttelsamicsterrassa.data.core.domain.model.auth.User;
import org.cttelsamicsterrassa.data.core.domain.repository.auth.UserRepository;
import org.cttelsamicsterrassa.data.core.domain.service.auth.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.cttelsamicsterrassa.data.api.rest.ControllerConfig.API_BASE_PATH_V1;

@RestController
@RequestMapping(API_BASE_PATH_V1 + "/users")
@Tag(name = "User Management", description = "Admin-only user management operations")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    public UserController(AuthenticationService authenticationService,
                          UserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "List all users", description = "Returns all registered users with their roles. Requires ADMIN role.")
    @ApiResponse(responseCode = "200", description = "Users listed successfully")
    public ResponseEntity<List<UserDto>> listUsers() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(UserDto::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PostMapping("/{id}/assign-role")
    @Operation(summary = "Assign role to user", description = "Assigns a named role to the specified user. Requires ADMIN role.")
    @ApiResponse(responseCode = "200", description = "Role assigned successfully")
    @ApiResponse(responseCode = "404", description = "User or role not found")
    public ResponseEntity<?> assignRole(@PathVariable UUID id,
                                        @RequestBody AssignRoleRequest request) {
        try {
            authenticationService.assignRole(id, request.roleName());
            log.info("Assigned role '{}' to user {}", request.roleName(), id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/disable")
    @Operation(summary = "Disable user", description = "Deactivates a user account. Requires ADMIN role.")
    @ApiResponse(responseCode = "200", description = "User disabled successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<?> disableUser(@PathVariable UUID id) {
        try {
            authenticationService.disableUser(id);
            log.info("Disabled user {}", id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/enable")
    @Operation(summary = "Enable user", description = "Reactivates a user account. Requires ADMIN role.")
    @ApiResponse(responseCode = "200", description = "User enabled successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<?> enableUser(@PathVariable UUID id) {
        try {
            authenticationService.enableUser(id);
            log.info("Enabled user {}", id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

