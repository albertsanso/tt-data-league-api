package org.cttelsamicsterrassa.data.api.core.match.find.application;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.PlayersSingleMatch;
import org.cttelsamicsterrassa.data.core.domain.repository.PlayersSingleMatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FindMatchesQueryHandler extends DomainQueryHandler<FindMatchesQuery, List<PlayersSingleMatch>> {

    private final PlayersSingleMatchRepository playersSingleMatchRepository;

    @Autowired
    public FindMatchesQueryHandler(PlayersSingleMatchRepository playersSingleMatchRepository) {
        this.playersSingleMatchRepository = playersSingleMatchRepository;
    }

    @Override
    public DomainQueryResponse<List<PlayersSingleMatch>> handle(FindMatchesQuery findMatchesQuery) {
        return DomainQueryResponse.sucessResponse(
                playersSingleMatchRepository.findBySeasonAndCompetitionAndMatchDayNumber(
                    findMatchesQuery.getSeason(),
                    findMatchesQuery.getCompetitionInfo(),
                    findMatchesQuery.getMatchDayNumber(),
                    findMatchesQuery.getPractitionerName()
        ));
    }
}
