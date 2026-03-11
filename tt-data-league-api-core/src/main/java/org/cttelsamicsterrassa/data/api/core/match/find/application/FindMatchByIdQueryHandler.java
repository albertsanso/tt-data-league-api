package org.cttelsamicsterrassa.data.api.core.match.find.application;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.PlayersSingleMatch;
import org.cttelsamicsterrassa.data.core.domain.repository.PlayersSingleMatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Query handler for retrieving a match by ID.
 * Executes the FindMatchByIdQuery and returns the specific match from the repository.
 */
@Component
public class FindMatchByIdQueryHandler extends DomainQueryHandler<FindMatchByIdQuery, PlayersSingleMatch> {

    private final PlayersSingleMatchRepository playersSingleMatchRepository;

    @Autowired
    public FindMatchByIdQueryHandler(PlayersSingleMatchRepository playersSingleMatchRepository) {
        this.playersSingleMatchRepository = playersSingleMatchRepository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DomainQueryResponse<PlayersSingleMatch> handle(FindMatchByIdQuery findMatchByIdQuery) {
        return playersSingleMatchRepository.findById(findMatchByIdQuery.getMatchId())
                .map(DomainQueryResponse::sucessResponse)
                .orElseGet(() -> (DomainQueryResponse<PlayersSingleMatch>) (DomainQueryResponse<?>)
                    DomainQueryResponse.failResponse("Match with that ID doesn't exist"));
    }
}

