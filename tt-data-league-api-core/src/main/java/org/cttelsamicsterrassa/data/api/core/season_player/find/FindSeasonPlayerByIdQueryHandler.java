package org.cttelsamicsterrassa.data.api.core.season_player.find;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.repository.SeasonPlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindSeasonPlayerByIdQueryHandler extends DomainQueryHandler<FindSeasonPlayerByIdQuery> {

    private final SeasonPlayerRepository seasonPlayerRepository;

    @Autowired
    public FindSeasonPlayerByIdQueryHandler(SeasonPlayerRepository seasonPlayerRepository) {
        this.seasonPlayerRepository = seasonPlayerRepository;
    }

    @Override
    public DomainQueryResponse handle(FindSeasonPlayerByIdQuery findSeasonPlayerByIdQuery) {
        return seasonPlayerRepository.findById(findSeasonPlayerByIdQuery.getSeasonPlayerId())
                .map(DomainQueryResponse::sucessResponse)
                .orElse(DomainQueryResponse.failResponse("SeasonPlayer not found"));
    }
}
