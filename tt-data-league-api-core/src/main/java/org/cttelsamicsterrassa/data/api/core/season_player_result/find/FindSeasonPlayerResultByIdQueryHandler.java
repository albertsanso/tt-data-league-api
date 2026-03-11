package org.cttelsamicsterrassa.data.api.core.season_player_result.find;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayerResult;
import org.cttelsamicsterrassa.data.core.domain.repository.SeasonPlayerResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Query handler for retrieving a season player result by ID.
 * Executes the FindSeasonPlayerResultByIdQuery and returns the specific season player result from the repository.
 */
@Component
public class FindSeasonPlayerResultByIdQueryHandler extends DomainQueryHandler<FindSeasonPlayerResultByIdQuery, SeasonPlayerResult> {

    private final SeasonPlayerResultRepository seasonPlayerResultRepository;

    @Autowired
    public FindSeasonPlayerResultByIdQueryHandler(SeasonPlayerResultRepository seasonPlayerResultRepository) {
        this.seasonPlayerResultRepository = seasonPlayerResultRepository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DomainQueryResponse<SeasonPlayerResult> handle(FindSeasonPlayerResultByIdQuery findSeasonPlayerResultByIdQuery) {
        return seasonPlayerResultRepository.findById(findSeasonPlayerResultByIdQuery.getSeasonPlayerResultId())
                .map(DomainQueryResponse::sucessResponse)
                .orElseGet(() -> (DomainQueryResponse<SeasonPlayerResult>) (DomainQueryResponse<?>)
                    DomainQueryResponse.failResponse("Season player result with that ID doesn't exist"));
    }
}

