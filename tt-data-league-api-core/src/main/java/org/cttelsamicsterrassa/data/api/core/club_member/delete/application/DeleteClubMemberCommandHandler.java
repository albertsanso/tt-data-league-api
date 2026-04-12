package org.cttelsamicsterrassa.data.api.core.club_member.delete.application;

import org.albertsanso.commons.command.DomainCommandHandler;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteClubMemberCommandHandler extends DomainCommandHandler<DeleteClubMemberCommand> {

    private final ClubMemberRepository clubMemberRepository;

    @Autowired
    public DeleteClubMemberCommandHandler(ClubMemberRepository clubMemberRepository) {
        this.clubMemberRepository = clubMemberRepository;
    }

    @Override
    public DomainCommandResponse handle(DeleteClubMemberCommand command) {
        return clubMemberRepository.findById(command.getClubMemberId())
                .map(clubMember -> {
                    clubMemberRepository.deteleById(clubMember.getId());
                    return DomainCommandResponse.successResponse(clubMember);
                })
                .orElseGet(() -> DomainCommandResponse.failResponse("Club member not found"));
    }
}


