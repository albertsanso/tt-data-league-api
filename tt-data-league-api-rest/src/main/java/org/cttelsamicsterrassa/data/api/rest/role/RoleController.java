package org.cttelsamicsterrassa.data.api.rest.role;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.cttelsamicsterrassa.data.core.domain.repository.auth.RoleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.cttelsamicsterrassa.data.api.rest.ControllerConfig.API_BASE_PATH_V1;

@RestController
@RequestMapping(API_BASE_PATH_V1 + "/roles")
@Tag(name = "Roles", description = "Available roles catalogue")
public class RoleController {

    private final RoleRepository roleRepository;

    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping
    @Operation(summary = "List all roles", description = "Returns the full catalogue of available roles with their permissions.")
    @ApiResponse(responseCode = "200", description = "Roles listed successfully")
    public ResponseEntity<List<RoleDto>> listRoles() {
        List<RoleDto> roles = roleRepository.findAll().stream()
                .map(RoleDto::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }
}

