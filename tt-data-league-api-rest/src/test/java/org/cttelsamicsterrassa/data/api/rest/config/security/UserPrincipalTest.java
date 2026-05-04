package org.cttelsamicsterrassa.data.api.rest.config.security;

import org.cttelsamicsterrassa.data.core.domain.model.auth.Role;
import org.cttelsamicsterrassa.data.core.domain.model.auth.User;
import org.cttelsamicsterrassa.data.core.domain.service.auth.RbacCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class UserPrincipalTest {

    @Test
    void getAuthoritiesReturnsMappedRoles() {
        Role adminRole = Role.createExisting(UUID.randomUUID(), RbacCatalog.ADMIN, Set.of());
        User user = User.createExisting(UUID.randomUUID(), "admin", "admin@example.com",
                "hash", null, true, Set.of(adminRole));

        UserPrincipal principal = new UserPrincipal(user);
        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

        Set<String> authorityNames = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertThat(authorityNames).containsExactly("ROLE_" + RbacCatalog.ADMIN);
    }

    @Test
    void getAuthoritiesReturnsMultipleRoles() {
        Role adminRole = Role.createExisting(UUID.randomUUID(), RbacCatalog.ADMIN, Set.of());
        Role analystRole = Role.createExisting(UUID.randomUUID(), RbacCatalog.ANALYST, Set.of());
        User user = User.createExisting(UUID.randomUUID(), "multi", "multi@example.com",
                "hash", null, true, Set.of(adminRole, analystRole));

        UserPrincipal principal = new UserPrincipal(user);
        Set<String> authorityNames = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertThat(authorityNames).containsExactlyInAnyOrder(
                "ROLE_" + RbacCatalog.ADMIN,
                "ROLE_" + RbacCatalog.ANALYST);
    }

    @Test
    void getAuthoritiesFallsBackToGuestWhenNoRoles() {
        User user = User.createExisting(UUID.randomUUID(), "guest", "guest@example.com",
                "hash", null, true, Set.of());

        UserPrincipal principal = new UserPrincipal(user);
        Set<String> authorityNames = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertThat(authorityNames).containsExactly("ROLE_" + RbacCatalog.defaultRoleName());
    }

    @Test
    void isEnabledReflectsUserActiveFlag() {
        User active = User.createExisting(UUID.randomUUID(), "u", "u@e.com", "h", null, true, Set.of());
        User inactive = User.createExisting(UUID.randomUUID(), "u2", "u2@e.com", "h", null, false, Set.of());

        assertThat(new UserPrincipal(active).isEnabled()).isTrue();
        assertThat(new UserPrincipal(inactive).isEnabled()).isFalse();
    }
}

