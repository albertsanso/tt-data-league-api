package org.cttelsamicsterrassa.data.api.core.season_player.create;

import org.albertsanso.commons.command.DomainCommandHandler;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.cttelsamicsterrassa.data.core.domain.model.ClubMember;
import org.cttelsamicsterrassa.data.core.domain.model.License;
import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayer;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubMemberRepository;
import org.cttelsamicsterrassa.data.core.domain.repository.SeasonPlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CreateSeasonPlayerCommandHandler extends DomainCommandHandler<CreateSeasonPlayerCommand> {

    private final SeasonPlayerRepository seasonPlayerRepository;

    private final ClubMemberRepository clubMemberRepository;

    @Autowired
    public CreateSeasonPlayerCommandHandler(SeasonPlayerRepository seasonPlayerRepository, ClubMemberRepository clubMemberRepository) {
        this.seasonPlayerRepository = seasonPlayerRepository;
        this.clubMemberRepository = clubMemberRepository;
    }

    @Override
    public DomainCommandResponse handle(CreateSeasonPlayerCommand createSeasonPlayerCommand) {
        Optional<ClubMember> optionalClubMember = clubMemberRepository.findById(createSeasonPlayerCommand.getClubMemberId());
        if (optionalClubMember.isEmpty()) {
            return DomainCommandResponse.failResponse("Club member not found");
        }

        SeasonPlayer seasonPlayer = SeasonPlayer.createNew(
                optionalClubMember.get(),
                new License(createSeasonPlayerCommand.getLicenseTag(), createSeasonPlayerCommand.getLicenseId()),
                createSeasonPlayerCommand.getYearRange()
        );
        seasonPlayerRepository.save(seasonPlayer);
        return DomainCommandResponse.successResponse(seasonPlayer);
    }
}
