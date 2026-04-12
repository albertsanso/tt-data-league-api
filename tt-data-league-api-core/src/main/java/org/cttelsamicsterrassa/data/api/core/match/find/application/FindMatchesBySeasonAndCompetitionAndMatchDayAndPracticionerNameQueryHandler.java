package org.cttelsamicsterrassa.data.api.core.match.find.application;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.PlayersSingleMatch;
import org.cttelsamicsterrassa.data.core.domain.service.MatchQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Query handler for retrieving matches by season, competition, match day and practitioner name.
 * Executes the FindMatchesBySeasonAndCompetitionAndMatchDayAndPracticionerNameQuery
 * and returns matches matching all criteria.
 */
@Component
public class FindMatchesBySeasonAndCompetitionAndMatchDayAndPracticionerNameQueryHandler
        extends DomainQueryHandler<FindMatchesBySeasonAndCompetitionAndMatchDayAndPracticionerNameQuery, List<PlayersSingleMatch>> {

    private final MatchQueryService matchQueryService;

    @Autowired
    public FindMatchesBySeasonAndCompetitionAndMatchDayAndPracticionerNameQueryHandler(
            MatchQueryService matchQueryService) {
        this.matchQueryService = matchQueryService;
    }

    @Override
    public DomainQueryResponse<List<PlayersSingleMatch>> handle(
            FindMatchesBySeasonAndCompetitionAndMatchDayAndPracticionerNameQuery query) {

        List<PlayersSingleMatch> matches = matchQueryService.findMatchesByPracticionerName(
                query.getPractitionerName(),
                query.getSeason(),
                query.getCompetitionInfo(),
                query.getMatchDayNumber()
        );
        return DomainQueryResponse.sucessResponse(matches);
    }
}

