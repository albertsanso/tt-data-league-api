package org.cttelsamicsterrassa.data.api.rest.config.security;

import org.cttelsamicsterrassa.data.core.domain.model.auth.Permission;
import org.cttelsamicsterrassa.data.core.domain.model.auth.Role;
import org.cttelsamicsterrassa.data.core.domain.repository.auth.PermissionRepository;
import org.cttelsamicsterrassa.data.core.domain.repository.auth.RoleRepository;
import org.cttelsamicsterrassa.data.core.domain.service.auth.RbacCatalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

/**
 * Seeds the predefined RBAC roles and permissions from {@link RbacCatalog} into the
 * database on startup. The operation is idempotent: roles and permissions that already
 * exist are skipped.
 */
@Component
public class RbacInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RbacInitializer.class);

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RbacInitializer(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("RBAC initializer starting — seeding predefined roles and permissions");

        for (Role role : RbacCatalog.predefinedRoles()) {
            if (roleRepository.existsByName(role.getName())) {
                log.debug("Role '{}' already exists — skipping", role.getName());
                continue;
            }

            // Persist each permission first (idempotent)
            Set<Permission> permissions = role.getPermissions();
            for (Permission permission : permissions) {
                Optional<Permission> existing = permissionRepository
                        .findByResourceAndAction(permission.getResource(), permission.getAction());
                if (existing.isEmpty()) {
                    permissionRepository.save(permission);
                    log.debug("Seeded permission [{} {}]", permission.getResource(), permission.getAction());
                }
            }

            roleRepository.save(role);
            log.info("Seeded role '{}' with {} permissions", role.getName(), permissions.size());
        }

        log.info("RBAC initializer done");
    }
}

