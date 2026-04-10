package org.cttelsamicsterrassa.data.api.rest.config.security;

import org.cttelsamicsterrassa.data.core.domain.repository.auth.UserRepository;
import org.cttelsamicsterrassa.data.core.domain.service.auth.AuthenticationService;
import org.cttelsamicsterrassa.data.core.domain.service.auth.BcryptPasswordHasher;
import org.cttelsamicsterrassa.data.core.domain.service.auth.PasswordHasher;
import org.cttelsamicsterrassa.data.core.domain.service.auth.UserValidator;
import org.cttelsamicsterrassa.data.core.repository.jpa.auth.impl.UserRepositoryHelper;
import org.cttelsamicsterrassa.data.core.repository.jpa.auth.impl.UserRepositoryImpl;
import org.cttelsamicsterrassa.data.core.repository.jpa.auth.mapper.UserJPAToUserMapper;
import org.cttelsamicsterrassa.data.core.repository.jpa.auth.mapper.UserToUserJPAMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {

    @Bean
    public UserValidator userValidator() {
        return new UserValidator();
    }

    @Bean
    public PasswordHasher passwordHasher() {
        return new BcryptPasswordHasher();
    }

    @Bean
    public UserToUserJPAMapper userToUserJPAMapper() {
        return new UserToUserJPAMapper();
    }

    @Bean
    public UserJPAToUserMapper userJPAToUserMapper() {
        return new UserJPAToUserMapper();
    }

    @Bean
    public UserRepository userRepository(UserRepositoryHelper helper,
                                         UserJPAToUserMapper jpaToUser,
                                         UserToUserJPAMapper userToJpa) {
        return new UserRepositoryImpl(helper, jpaToUser, userToJpa);
    }

    @Bean
    public AuthenticationService authenticationService(UserRepository userRepository,
                                                       PasswordHasher passwordHasher,
                                                       UserValidator userValidator) {
        return new AuthenticationService(userRepository, passwordHasher, userValidator);
    }
}

