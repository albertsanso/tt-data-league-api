package org.cttelsamicsterrassa.data.api.core.season_player.modify;

import org.albertsanso.commons.command.DomainCommandResponse;
import org.cttelsamicsterrassa.data.core.domain.model.ClubMember;
import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayer;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubMemberRepository;
import org.cttelsamicsterrassa.data.core.domain.repository.SeasonPlayerRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ModifySeasonPlayerCommandHandlerTest {

    private final SeasonPlayerRepository seasonPlayerRepository = mock(SeasonPlayerRepository.class);
    private final ClubMemberRepository clubMemberRepository = mock(ClubMemberRepository.class);
    private final ModifySeasonPlayerCommandHandler handler = new ModifySeasonPlayerCommandHandler(seasonPlayerRepository, clubMemberRepository);

    @Test
    void handleFailsWhenSeasonPlayerDoesNotExist() {
        UUID id = UUID.randomUUID();
        ModifySeasonPlayerCommand command = new ModifySeasonPlayerCommand(id, UUID.randomUUID(), "L-1", "TAG", "2025-2026");
        when(seasonPlayerRepository.findById(id)).thenReturn(Optional.empty());

        DomainCommandResponse response = handler.handle(command);

        assertThat(response.isSuccess()).isFalse();
    }

    @Test
    void handleFailsWhenClubMemberDoesNotExist() {
        UUID id = UUID.randomUUID();
        SeasonPlayer seasonPlayer = mock(SeasonPlayer.class);
        when(seasonPlayerRepository.findById(id)).thenReturn(Optional.of(seasonPlayer));
        when(clubMemberRepository.findById(any())).thenReturn(Optional.empty());

        ModifySeasonPlayerCommand command = new ModifySeasonPlayerCommand(id, UUID.randomUUID(), "L-1", "TAG", "2025-2026");
        DomainCommandResponse response = handler.handle(command);

        assertThat(response.isSuccess()).isFalse();
    }

    @Test
    void handleSavesUpdatedSeasonPlayerWhenDataExists() {
        UUID id = UUID.randomUUID();
        UUID clubMemberId = UUID.randomUUID();
        SeasonPlayer seasonPlayer = mock(SeasonPlayer.class);
        ClubMember clubMember = mock(ClubMember.class);

        when(seasonPlayer.getId()).thenReturn(id);
        when(seasonPlayerRepository.findById(id)).thenReturn(Optional.of(seasonPlayer));
        when(clubMemberRepository.findById(clubMemberId)).thenReturn(Optional.of(clubMember));

        ModifySeasonPlayerCommand command = new ModifySeasonPlayerCommand(id, clubMemberId, "L-1", "TAG", "2025-2026");
        DomainCommandResponse response = handler.handle(command);

        assertThat(response.isSuccess()).isTrue();
        verify(seasonPlayerRepository).save(any(SeasonPlayer.class));
    }
}

