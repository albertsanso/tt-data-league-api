package org.cttelsamicsterrassa.data.api.core.match.delete.application;

import org.albertsanso.commons.command.DomainCommandResponse;
import org.cttelsamicsterrassa.data.core.domain.model.PlayersSingleMatch;
import org.cttelsamicsterrassa.data.core.domain.repository.PlayersSingleMatchRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteMatchCommandHandlerTest {

    private final PlayersSingleMatchRepository playersSingleMatchRepository = mock(PlayersSingleMatchRepository.class);
    private final DeleteMatchCommandHandler handler = new DeleteMatchCommandHandler(playersSingleMatchRepository);

    @Test
    void handleDeletesMatchWhenExists() {
        UUID id = UUID.randomUUID();
        PlayersSingleMatch match = mock(PlayersSingleMatch.class);
        when(match.getId()).thenReturn(id);
        when(playersSingleMatchRepository.findById(id)).thenReturn(Optional.of(match));

        DomainCommandResponse response = handler.handle(new DeleteMatchCommand(id));

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getResponse()).isEqualTo(match);
        verify(playersSingleMatchRepository).deteleById(id);
    }

    @Test
    void handleFailsWhenMatchDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(playersSingleMatchRepository.findById(id)).thenReturn(Optional.empty());

        DomainCommandResponse response = handler.handle(new DeleteMatchCommand(id));

        assertThat(response.isSuccess()).isFalse();
    }
}

