package org.cttelsamicsterrassa.data.api.core.club.application.delete;

import org.albertsanso.commons.command.DomainCommandHandler;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteClubCommandHandler extends DomainCommandHandler<DeleteClubCommand> {

    private final ClubRepository clubRepository;

    @Autowired
    public DeleteClubCommandHandler(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Override
    public DomainCommandResponse handle(DeleteClubCommand deleteClubCommand) {
        return clubRepository.findById(deleteClubCommand.getClubId())
            .map(club -> {
                clubRepository.deteleById(deleteClubCommand.getClubId());
                return DomainCommandResponse.successResponse(null);
            })
            .orElseGet(() -> DomainCommandResponse.failResponse("Club with that ID doesn't exist"));
    }
}
