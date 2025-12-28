package org.cttelsamicsterrassa.data.api.core.club_member.create;

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
public class CreateClubMemberCommandHandler extends DomainCommandHandler<CreateClubMemberCommand> {

    private final ClubRepository clubRepository;

    private final PracticionerRepository practicionerRepository;

    private final ClubMemberRepository clubMemberRepository;

    @Autowired
    public CreateClubMemberCommandHandler(ClubRepository clubRepository, PracticionerRepository practicionerRepository, ClubMemberRepository clubMemberRepository) {
        this.clubRepository = clubRepository;
        this.practicionerRepository = practicionerRepository;
        this.clubMemberRepository = clubMemberRepository;
    }

    @Override
    public DomainCommandResponse handle(CreateClubMemberCommand createClubMemberCommand) {
        if (clubMemberRepository.existsByPracticionerIdAndClubId(
                createClubMemberCommand.getPracticionerId(),
                createClubMemberCommand.getClubId())) {
            return DomainCommandResponse.failResponse("Club member already exists");
        }

        Optional<Practicioner> optPracticioner = practicionerRepository.findById(createClubMemberCommand.getPracticionerId());
        Optional<Club> optClub = clubRepository.findById(createClubMemberCommand.getClubId());

        if (optPracticioner.isPresent() && optClub.isPresent()) {
            ClubMember newClubMember = ClubMember.createNew(
                    optClub.get(),
                    optPracticioner.get()
            );
            newClubMember.updateAllRanges(createClubMemberCommand.getYearRanges());
            clubMemberRepository.save(newClubMember);
            return DomainCommandResponse.successResponse(newClubMember);
        } else {
            return DomainCommandResponse.failResponse("Practicioner or Club not found");
        }
    }
}
