package org.cttelsamicsterrassa.data.api.core.club.application.create;

import org.albertsanso.commons.command.DomainCommandHandler;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.cttelsamicsterrassa.data.core.domain.model.Club;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateClubCommandHandler extends DomainCommandHandler<CreateClubCommand> {

    private final ClubRepository clubRepository;

    @Autowired
    public CreateClubCommandHandler(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Override
    public DomainCommandResponse handle(CreateClubCommand createClubCommand) {
        return clubRepository.findByName(createClubCommand.getName())
                .map(existingClub -> DomainCommandResponse.failResponse("Club with the same name already exists"))
                .orElseGet(() -> {
                    Club newClub = Club.createNew(createClubCommand.getName());
                    newClub.setYearRanges(createClubCommand.getYearRanges());
                    clubRepository.save(newClub);
                    return DomainCommandResponse.successResponse(newClub);
                });
    }
}
