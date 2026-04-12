package org.cttelsamicsterrassa.data.api.core.club_member.modify.application;

import org.albertsanso.commons.command.DomainCommandHandler;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.cttelsamicsterrassa.data.core.domain.model.Club;
import org.cttelsamicsterrassa.data.core.domain.model.ClubMember;
import org.cttelsamicsterrassa.data.core.domain.model.Practicioner;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubMemberRepository;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubRepository;
import org.cttelsamicsterrassa.data.core.domain.repository.PracticionerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ModifyClubMemberCommandHandler extends DomainCommandHandler<ModifyClubMemberCommand> {

    private final ClubMemberRepository clubMemberRepository;
    private final ClubRepository clubRepository;
    private final PracticionerRepository practicionerRepository;

    @Autowired
    public ModifyClubMemberCommandHandler(ClubMemberRepository clubMemberRepository,
                                          ClubRepository clubRepository,
                                          PracticionerRepository practicionerRepository) {
        this.clubMemberRepository = clubMemberRepository;
        this.clubRepository = clubRepository;
        this.practicionerRepository = practicionerRepository;
    }

    @Override
    public DomainCommandResponse handle(ModifyClubMemberCommand command) {
        Optional<Club> club = clubRepository.findById(command.getClubId());
        Optional<Practicioner> practicioner = practicionerRepository.findById(command.getPracticionerId());
        if (club.isEmpty()) {
            return DomainCommandResponse.failResponse("Club not found");
        }
        if (practicioner.isEmpty()) {
            return DomainCommandResponse.failResponse("Practicioner not found");
        }

        return clubMemberRepository.findById(command.getId())
                .map(clubMember -> {
                    ClubMember updated = ClubMember.createExisting(clubMember.getId(), club.get(), practicioner.get());
                    updated.updateAllRanges(command.getYearRanges());
                    clubMemberRepository.save(updated);
                    return DomainCommandResponse.successResponse(updated);
                })
                .orElseGet(() -> DomainCommandResponse.failResponse("Club member not found"));
    }
}


