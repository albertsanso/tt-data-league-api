package org.cttelsamicsterrassa.data.api.core.season_player.find;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayer;
import org.cttelsamicsterrassa.data.core.domain.repository.SeasonPlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FindSeasonPlayerByPracticionerIdQueryHandler extends DomainQueryHandler<FindSeasonPlayerByPracticionerIdQuery, List<SeasonPlayer>> {

    private final SeasonPlayerRepository seasonPlayerRepository;

    @Autowired
    public FindSeasonPlayerByPracticionerIdQueryHandler(SeasonPlayerRepository seasonPlayerRepository) {
        this.seasonPlayerRepository = seasonPlayerRepository;
    }

    @Override
    public DomainQueryResponse<List<SeasonPlayer>> handle(FindSeasonPlayerByPracticionerIdQuery findSeasonPlayerByPracticionerIdQuery) {
        return DomainQueryResponse.sucessResponse(
                seasonPlayerRepository.findByPracticionerId(
                        findSeasonPlayerByPracticionerIdQuery.getPracticionerId()));
    }
}
