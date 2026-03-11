package org.cttelsamicsterrassa.data.api.core.season_player_result.find;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayerResult;
import org.cttelsamicsterrassa.data.core.domain.repository.SeasonPlayerResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Query handler for retrieving season player results by details.
 * Executes the FindSeasonPlayerResultByDetailsQuery and returns matching season player results.
 */
@Component
public class FindSeasonPlayerResultByDetailsQueryHandler extends DomainQueryHandler<FindSeasonPlayerResultByDetailsQuery, List<SeasonPlayerResult>> {

    private final SeasonPlayerResultRepository seasonPlayerResultRepository;

    @Autowired
    public FindSeasonPlayerResultByDetailsQueryHandler(SeasonPlayerResultRepository seasonPlayerResultRepository) {
        this.seasonPlayerResultRepository = seasonPlayerResultRepository;
    }

    @Override
    public DomainQueryResponse<List<SeasonPlayerResult>> handle(FindSeasonPlayerResultByDetailsQuery findSeasonPlayerResultByDetailsQuery) {
        // TODO: Implement once repository methods for finding by details are available
        // For now return empty list as a placeholder
        return DomainQueryResponse.sucessResponse(new ArrayList<>());
    }
}

