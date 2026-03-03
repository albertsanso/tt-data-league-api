package org.cttelsamicsterrassa.data.api.core.match.find;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.repository.PlayersSingleMatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindMatchQueryHandler extends DomainQueryHandler<FindMatchesQuery> {

    private final PlayersSingleMatchRepository playersSingleMatchRepository;

    @Autowired
    public FindMatchQueryHandler(PlayersSingleMatchRepository playersSingleMatchRepository) {
        this.playersSingleMatchRepository = playersSingleMatchRepository;
    }

    @Override
    public DomainQueryResponse handle(FindMatchesQuery findMatchesQuery) {
        return DomainQueryResponse.sucessResponse(
                playersSingleMatchRepository.findBySeasonAndCompetitionAndMatchDayNumber(
                    findMatchesQuery.getSeason(),
                    findMatchesQuery.getCompetitionInfo(),
                    findMatchesQuery.getMatchDayNumber()
        ));
    }
}
