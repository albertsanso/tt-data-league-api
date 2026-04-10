package org.cttelsamicsterrassa.data.api.rest.config.security;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class TokenBlacklistServiceTest {

    private final TokenBlacklistService service = new TokenBlacklistService();

    @Test
    void blacklistedTokenIsDetected() {
        String token = "token-1";
        service.blacklistToken(token, new Date(System.currentTimeMillis() + 60_000));

        assertThat(service.isBlacklisted(token)).isTrue();
    }

    @Test
    void expiredTokenIsCleanedWhenChecked() {
        String token = "token-expired";
        service.blacklistToken(token, new Date(System.currentTimeMillis() - 1_000));

        assertThat(service.isBlacklisted(token)).isFalse();
    }
}

