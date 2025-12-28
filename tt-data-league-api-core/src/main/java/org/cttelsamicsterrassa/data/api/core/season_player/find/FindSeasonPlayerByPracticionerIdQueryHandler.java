package org.cttelsamicsterrassa.data.api.core.season_player.find;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.repository.SeasonPlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindSeasonPlayerByPracticionerIdQueryHandler extends DomainQueryHandler<FindSeasonPlayerByPracticionerIdQuery> {

    private final SeasonPlayerRepository seasonPlayerRepository;

    @Autowired
    public FindSeasonPlayerByPracticionerIdQueryHandler(SeasonPlayerRepository seasonPlayerRepository) {
        this.seasonPlayerRepository = seasonPlayerRepository;
    }

    @Override
    public DomainQueryResponse handle(FindSeasonPlayerByPracticionerIdQuery findSeasonPlayerByPracticionerIdQuery) {
        return DomainQueryResponse.sucessResponse(
                seasonPlayerRepository.findByPracticionerId(
                        findSeasonPlayerByPracticionerIdQuery.getPracticionerId()));
    }
}
