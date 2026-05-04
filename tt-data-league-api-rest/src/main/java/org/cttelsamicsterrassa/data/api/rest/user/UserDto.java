package org.cttelsamicsterrassa.data.api.rest.user;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.cttelsamicsterrassa.data.core.domain.model.auth.User;

public record UserDto(
        UUID id,
        String username,
        String email,
        boolean active,
        Set<String> roles,
        LocalDateTime createdAt
) {
    public static UserDto fromDomain(User user) {
        Set<String> roleNames = user.getRoles() == null ? Set.of()
                : user.getRoles().stream()
                        .map(r -> r.getName())
                        .collect(Collectors.toSet());
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isActive(),
                roleNames,
                user.getCreatedAt()
        );
    }
}

