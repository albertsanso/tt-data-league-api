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
 * Query handler for retrieving all season player results.
 * Executes the FindAllSeasonPlayerResultsQuery and returns all season player results from the repository.
 */
@Component
public class FindAllSeasonPlayerResultsQueryHandler extends DomainQueryHandler<FindAllSeasonPlayerResultsQuery, List<SeasonPlayerResult>> {

    private final SeasonPlayerResultRepository seasonPlayerResultRepository;

    @Autowired
    public FindAllSeasonPlayerResultsQueryHandler(SeasonPlayerResultRepository seasonPlayerResultRepository) {
        this.seasonPlayerResultRepository = seasonPlayerResultRepository;
    }

    @Override
    public DomainQueryResponse<List<SeasonPlayerResult>> handle(FindAllSeasonPlayerResultsQuery findAllSeasonPlayerResultsQuery) {
        // TODO: Implement once findAll() is available in SeasonPlayerResultRepository
        // For now return empty list as a placeholder
        return DomainQueryResponse.sucessResponse(new ArrayList<>());
    }
}

