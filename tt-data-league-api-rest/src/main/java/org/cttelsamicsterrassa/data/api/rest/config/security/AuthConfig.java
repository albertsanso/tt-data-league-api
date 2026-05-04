package org.cttelsamicsterrassa.data.api.rest.config.security;

import org.cttelsamicsterrassa.data.core.domain.repository.auth.PermissionRepository;
import org.cttelsamicsterrassa.data.core.domain.repository.auth.RoleRepository;
import org.cttelsamicsterrassa.data.core.domain.repository.auth.UserRepository;
import org.cttelsamicsterrassa.data.core.domain.service.auth.AuthenticationService;
import org.cttelsamicsterrassa.data.core.domain.service.auth.BcryptPasswordHasher;
import org.cttelsamicsterrassa.data.core.domain.service.auth.PasswordHasher;
import org.cttelsamicsterrassa.data.core.domain.service.auth.UserValidator;
import org.cttelsamicsterrassa.data.core.repository.jpa.auth.impl.PermissionRepositoryHelper;
import org.cttelsamicsterrassa.data.core.repository.jpa.auth.impl.PermissionRepositoryImpl;
import org.cttelsamicsterrassa.data.core.repository.jpa.auth.impl.RoleRepositoryHelper;
import org.cttelsamicsterrassa.data.core.repository.jpa.auth.impl.RoleRepositoryImpl;
import org.cttelsamicsterrassa.data.core.repository.jpa.auth.impl.UserRepositoryHelper;
import org.cttelsamicsterrassa.data.core.repository.jpa.auth.impl.UserRepositoryImpl;
import org.cttelsamicsterrassa.data.core.repository.jpa.auth.mapper.PermissionJPAToPermissionMapper;
import org.cttelsamicsterrassa.data.core.repository.jpa.auth.mapper.PermissionToPermissionJPAMapper;
import org.cttelsamicsterrassa.data.core.repository.jpa.auth.mapper.RoleJPAToRoleMapper;
import org.cttelsamicsterrassa.data.core.repository.jpa.auth.mapper.RoleToRoleJPAMapper;
import org.cttelsamicsterrassa.data.core.repository.jpa.auth.mapper.UserJPAToUserMapper;
import org.cttelsamicsterrassa.data.core.repository.jpa.auth.mapper.UserToUserJPAMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {

    // ── Permission mappers (no-arg) ───────────────────────────────────────

    @Bean
    public PermissionToPermissionJPAMapper permissionToPermissionJPAMapper() {
        return new PermissionToPermissionJPAMapper();
    }

    @Bean
    public PermissionJPAToPermissionMapper permissionJPAToPermissionMapper() {
        return new PermissionJPAToPermissionMapper();
    }

    // ── Role mappers (depend on permission mappers) ───────────────────────

    @Bean
    public RoleToRoleJPAMapper roleToRoleJPAMapper(PermissionToPermissionJPAMapper permissionToJpa) {
        return new RoleToRoleJPAMapper(permissionToJpa);
    }

    @Bean
    public RoleJPAToRoleMapper roleJPAToRoleMapper(PermissionJPAToPermissionMapper jpaToPermission) {
        return new RoleJPAToRoleMapper(jpaToPermission);
    }

    // ── User mappers (depend on role mappers) ─────────────────────────────

    @Bean
    public UserToUserJPAMapper userToUserJPAMapper(RoleToRoleJPAMapper roleToJpa) {
        return new UserToUserJPAMapper(roleToJpa);
    }

    @Bean
    public UserJPAToUserMapper userJPAToUserMapper(RoleJPAToRoleMapper jpaToRole) {
        return new UserJPAToUserMapper(jpaToRole);
    }

    // ── Repositories ──────────────────────────────────────────────────────

    @Bean
    public PermissionRepository permissionRepository(PermissionRepositoryHelper helper,
                                                     PermissionJPAToPermissionMapper jpaToPermission,
                                                     PermissionToPermissionJPAMapper permissionToJpa) {
        return new PermissionRepositoryImpl(helper, jpaToPermission, permissionToJpa);
    }

    @Bean
    public RoleRepository roleRepository(RoleRepositoryHelper helper,
                                         RoleJPAToRoleMapper jpaToRole,
                                         RoleToRoleJPAMapper roleToJpa) {
        return new RoleRepositoryImpl(helper, jpaToRole, roleToJpa);
    }

    @Bean
    public UserRepository userRepository(UserRepositoryHelper helper,
                                         UserJPAToUserMapper jpaToUser,
                                         UserToUserJPAMapper userToJpa) {
        return new UserRepositoryImpl(helper, jpaToUser, userToJpa);
    }

    // ── Domain services ───────────────────────────────────────────────────

    @Bean
    public UserValidator userValidator() {
        return new UserValidator();
    }

    @Bean
    public PasswordHasher passwordHasher() {
        return new BcryptPasswordHasher();
    }

    @Bean
    public AuthenticationService authenticationService(UserRepository userRepository,
                                                       RoleRepository roleRepository,
                                                       PasswordHasher passwordHasher,
                                                       UserValidator userValidator) {
        return new AuthenticationService(userRepository, roleRepository, passwordHasher, userValidator);
    }
}
