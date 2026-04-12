package org.cttelsamicsterrassa.data.api.core.season_player_result.delete;

import org.albertsanso.commons.command.DomainCommandHandler;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.cttelsamicsterrassa.data.core.domain.repository.SeasonPlayerResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteSeasonPlayerResultCommandHandler extends DomainCommandHandler<DeleteSeasonPlayerResultCommand> {

    private final SeasonPlayerResultRepository seasonPlayerResultRepository;

    @Autowired
    public DeleteSeasonPlayerResultCommandHandler(SeasonPlayerResultRepository seasonPlayerResultRepository) {
        this.seasonPlayerResultRepository = seasonPlayerResultRepository;
    }

    @Override
    public DomainCommandResponse handle(DeleteSeasonPlayerResultCommand command) {
        return seasonPlayerResultRepository.findById(command.getSeasonPlayerResultId())
                .map(result -> {
                    seasonPlayerResultRepository.deteleById(result.getId());
                    return DomainCommandResponse.successResponse(result);
                })
                .orElseGet(() -> DomainCommandResponse.failResponse("SeasonPlayerResult not found"));
    }
}


