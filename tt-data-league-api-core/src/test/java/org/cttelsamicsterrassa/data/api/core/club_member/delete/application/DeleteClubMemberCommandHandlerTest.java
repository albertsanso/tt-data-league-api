package org.cttelsamicsterrassa.data.api.core.club_member.delete.application;

import org.albertsanso.commons.command.DomainCommandResponse;
import org.cttelsamicsterrassa.data.core.domain.model.ClubMember;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubMemberRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteClubMemberCommandHandlerTest {

    private final ClubMemberRepository clubMemberRepository = mock(ClubMemberRepository.class);
    private final DeleteClubMemberCommandHandler handler = new DeleteClubMemberCommandHandler(clubMemberRepository);

    @Test
    void handleDeletesClubMemberWhenExists() {
        UUID id = UUID.randomUUID();
        ClubMember clubMember = mock(ClubMember.class);
        when(clubMember.getId()).thenReturn(id);
        when(clubMemberRepository.findById(id)).thenReturn(Optional.of(clubMember));

        DomainCommandResponse response = handler.handle(new DeleteClubMemberCommand(id));

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getResponse()).isEqualTo(clubMember);
        verify(clubMemberRepository).deteleById(id);
    }

    @Test
    void handleFailsWhenClubMemberDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(clubMemberRepository.findById(id)).thenReturn(Optional.empty());

        DomainCommandResponse response = handler.handle(new DeleteClubMemberCommand(id));

        assertThat(response.isSuccess()).isFalse();
    }
}

