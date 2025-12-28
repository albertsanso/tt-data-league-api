package org.cttelsamicsterrassa.data.api.core.season_player.delete;

import org.albertsanso.commons.command.DomainCommandHandler;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.cttelsamicsterrassa.data.core.domain.repository.SeasonPlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteSeasonPlayerCommandHandler extends DomainCommandHandler<DeleteSeasonPlayerCommand> {

    private final SeasonPlayerRepository seasonPlayerRepository;

    @Autowired
    public DeleteSeasonPlayerCommandHandler(SeasonPlayerRepository seasonPlayerRepository) {
        this.seasonPlayerRepository = seasonPlayerRepository;
    }

    @Override
    public DomainCommandResponse handle(DeleteSeasonPlayerCommand deleteSeasonPlayerCommand) {
        return seasonPlayerRepository.findById(deleteSeasonPlayerCommand.getSeasonPlayerId())
                .map(seasonPlayer -> {
                    seasonPlayerRepository.delete(seasonPlayer);
                    return DomainCommandResponse.successResponse(seasonPlayer);
                })
                .orElse(DomainCommandResponse.failResponse("SeasonPlayer not found"));
    }
}
