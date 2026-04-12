package org.cttelsamicsterrassa.data.api.core.season_player.modify;

import org.albertsanso.commons.command.DomainCommandHandler;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.cttelsamicsterrassa.data.core.domain.model.License;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubMemberRepository;
import org.cttelsamicsterrassa.data.core.domain.repository.SeasonPlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModifySeasonPlayerCommandHandler extends DomainCommandHandler<ModifySeasonPlayerCommand> {

    private final SeasonPlayerRepository seasonPlayerRepository;
    private final ClubMemberRepository clubMemberRepository;

    @Autowired
    public ModifySeasonPlayerCommandHandler(SeasonPlayerRepository seasonPlayerRepository,
                                            ClubMemberRepository clubMemberRepository) {
        this.seasonPlayerRepository = seasonPlayerRepository;
        this.clubMemberRepository = clubMemberRepository;
    }

    @Override
    public DomainCommandResponse handle(ModifySeasonPlayerCommand command) {
        return seasonPlayerRepository.findById(command.getId())
                .map(seasonPlayer -> {
                    return clubMemberRepository.findById(command.getClubMemberId())
                            .map(clubMember -> {
                                var updated = org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayer.createExisting(
                                        seasonPlayer.getId(),
                                        clubMember,
                                        new License(command.getLicenseTag(), command.getLicenseId()),
                                        command.getYearRange()
                                );
                                seasonPlayerRepository.save(updated);
                                return DomainCommandResponse.successResponse(updated);
                            })
                            .orElseGet(() -> DomainCommandResponse.failResponse("Club member not found"));
                })
                .orElseGet(() -> DomainCommandResponse.failResponse("SeasonPlayer not found"));
    }
}


