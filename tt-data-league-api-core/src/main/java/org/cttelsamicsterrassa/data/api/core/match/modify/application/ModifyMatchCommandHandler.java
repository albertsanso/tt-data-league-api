package org.cttelsamicsterrassa.data.api.core.match.modify.application;

import org.albertsanso.commons.command.DomainCommandHandler;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.cttelsamicsterrassa.data.core.domain.repository.PlayersSingleMatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModifyMatchCommandHandler extends DomainCommandHandler<ModifyMatchCommand> {

    private final PlayersSingleMatchRepository playersSingleMatchRepository;

    @Autowired
    public ModifyMatchCommandHandler(PlayersSingleMatchRepository playersSingleMatchRepository) {
        this.playersSingleMatchRepository = playersSingleMatchRepository;
    }

    @Override
    public DomainCommandResponse handle(ModifyMatchCommand command) {
        return playersSingleMatchRepository.findById(command.getId())
                .map(match -> {
                    var updated = org.cttelsamicsterrassa.data.core.domain.model.PlayersSingleMatch.createExisting(
                            match.getId(),
                            match.getSeasonPlayerResultLocal(),
                            match.getSeasonPlayerResultVisitor(),
                            command.getSeason(),
                            command.getCompetitionInfo(),
                            command.getMatchDayNumber(),
                            command.getUniqueRowMatchId(),
                            match.getMatchDateTime()
                    );
                    playersSingleMatchRepository.save(updated);
                    return DomainCommandResponse.successResponse(updated);
                })
                .orElseGet(() -> DomainCommandResponse.failResponse("Match not found"));
    }
}


