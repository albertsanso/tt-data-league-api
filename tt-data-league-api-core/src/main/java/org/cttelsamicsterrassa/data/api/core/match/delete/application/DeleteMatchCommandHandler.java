package org.cttelsamicsterrassa.data.api.core.match.delete.application;

import org.albertsanso.commons.command.DomainCommandHandler;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.cttelsamicsterrassa.data.core.domain.repository.PlayersSingleMatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteMatchCommandHandler extends DomainCommandHandler<DeleteMatchCommand> {

    private final PlayersSingleMatchRepository playersSingleMatchRepository;

    @Autowired
    public DeleteMatchCommandHandler(PlayersSingleMatchRepository playersSingleMatchRepository) {
        this.playersSingleMatchRepository = playersSingleMatchRepository;
    }

    @Override
    public DomainCommandResponse handle(DeleteMatchCommand command) {
        return playersSingleMatchRepository.findById(command.getMatchId())
                .map(match -> {
                    playersSingleMatchRepository.deteleById(match.getId());
                    return DomainCommandResponse.successResponse(match);
                })
                .orElseGet(() -> DomainCommandResponse.failResponse("Match not found"));
    }
}


