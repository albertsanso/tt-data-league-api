package org.cttelsamicsterrassa.data.api.rest.role;

import org.cttelsamicsterrassa.data.core.domain.model.auth.Role;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record RoleDto(UUID id, String name, List<PermissionDto> permissions) {

    public static RoleDto fromDomain(Role role) {
        List<PermissionDto> permDtos = role.getPermissions() == null ? List.of()
                : role.getPermissions().stream()
                        .map(p -> new PermissionDto(
                                p.getId(),
                                p.getResource().name(),
                                p.getAction().name()))
                        .collect(Collectors.toList());
        return new RoleDto(role.getId(), role.getName(), permDtos);
    }
}

