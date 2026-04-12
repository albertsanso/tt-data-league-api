package org.cttelsamicsterrassa.data.api.core.season_player_result.delete;

import org.albertsanso.commons.command.DomainCommandResponse;
import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayerResult;
import org.cttelsamicsterrassa.data.core.domain.repository.SeasonPlayerResultRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteSeasonPlayerResultCommandHandlerTest {

    private final SeasonPlayerResultRepository seasonPlayerResultRepository = mock(SeasonPlayerResultRepository.class);
    private final DeleteSeasonPlayerResultCommandHandler handler = new DeleteSeasonPlayerResultCommandHandler(seasonPlayerResultRepository);

    @Test
    void handleDeletesSeasonPlayerResultWhenExists() {
        UUID id = UUID.randomUUID();
        SeasonPlayerResult result = mock(SeasonPlayerResult.class);
        when(result.getId()).thenReturn(id);
        when(seasonPlayerResultRepository.findById(id)).thenReturn(Optional.of(result));

        DomainCommandResponse response = handler.handle(new DeleteSeasonPlayerResultCommand(id));

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getResponse()).isEqualTo(result);
        verify(seasonPlayerResultRepository).deteleById(id);
    }

    @Test
    void handleFailsWhenSeasonPlayerResultDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(seasonPlayerResultRepository.findById(id)).thenReturn(Optional.empty());

        DomainCommandResponse response = handler.handle(new DeleteSeasonPlayerResultCommand(id));

        assertThat(response.isSuccess()).isFalse();
    }
}

