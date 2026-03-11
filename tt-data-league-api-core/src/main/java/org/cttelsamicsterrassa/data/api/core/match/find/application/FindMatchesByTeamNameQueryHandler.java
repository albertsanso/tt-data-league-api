package org.cttelsamicsterrassa.data.api.core.match.find.application;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.PlayersSingleMatch;
import org.cttelsamicsterrassa.data.core.domain.repository.PlayersSingleMatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Query handler for retrieving matches by team name.
 * Executes the FindMatchesByTeamNameQuery and returns matches where team name matches.
 */
@Component
public class FindMatchesByTeamNameQueryHandler extends DomainQueryHandler<FindMatchesByTeamNameQuery, List<PlayersSingleMatch>> {

    private final PlayersSingleMatchRepository playersSingleMatchRepository;

    @Autowired
    public FindMatchesByTeamNameQueryHandler(PlayersSingleMatchRepository playersSingleMatchRepository) {
        this.playersSingleMatchRepository = playersSingleMatchRepository;
    }

    @Override
    public DomainQueryResponse<List<PlayersSingleMatch>> handle(FindMatchesByTeamNameQuery findMatchesByTeamNameQuery) {
        List<PlayersSingleMatch> matches = playersSingleMatchRepository.findByTeamName(findMatchesByTeamNameQuery.getTeamName());
        return DomainQueryResponse.sucessResponse(matches);
    }
}

