package org.cttelsamicsterrassa.data.api.core.club.application.modify;

import org.albertsanso.commons.command.DomainCommandHandler;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModifyClubCommandHandler extends DomainCommandHandler<ModifyClubCommand> {

    private final ClubRepository clubRepository;

    @Autowired
    public ModifyClubCommandHandler(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Override
    public DomainCommandResponse handle(ModifyClubCommand modifyClubCommand) {
        return clubRepository.findById(modifyClubCommand.getId())
                .map(club -> {
                    club.modifyName(modifyClubCommand.getName());
                    club.setYearRanges(modifyClubCommand.getYearRanges());
                    clubRepository.save(club);
                    return DomainCommandResponse.successResponse(club);
                })
                .orElseGet(() -> DomainCommandResponse.failResponse("Club with that name doesn't exist"));
    }
}
