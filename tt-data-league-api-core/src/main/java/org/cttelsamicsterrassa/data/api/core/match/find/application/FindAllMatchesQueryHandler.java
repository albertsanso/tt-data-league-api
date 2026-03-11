package org.cttelsamicsterrassa.data.api.core.match.find.application;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.PlayersSingleMatch;
import org.cttelsamicsterrassa.data.core.domain.repository.PlayersSingleMatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Query handler for retrieving all matches.
 * Executes the FindAllMatchesQuery and returns all matches from the repository.
 */
@Component
public class FindAllMatchesQueryHandler extends DomainQueryHandler<FindAllMatchesQuery, List<PlayersSingleMatch>> {

    private final PlayersSingleMatchRepository playersSingleMatchRepository;

    @Autowired
    public FindAllMatchesQueryHandler(PlayersSingleMatchRepository playersSingleMatchRepository) {
        this.playersSingleMatchRepository = playersSingleMatchRepository;
    }

    @Override
    public DomainQueryResponse<List<PlayersSingleMatch>> handle(FindAllMatchesQuery findAllMatchesQuery) {
        // TODO: Implement once findAll() is available in PlayersSingleMatchRepository
        // For now return empty list as a placeholder
        return DomainQueryResponse.sucessResponse(new ArrayList<>());
    }
}


