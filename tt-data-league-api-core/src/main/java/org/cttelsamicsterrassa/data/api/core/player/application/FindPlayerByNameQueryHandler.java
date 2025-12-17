package org.cttelsamicsterrassa.data.api.core.player.application;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayer;
import org.cttelsamicsterrassa.data.core.domain.repository.SeasonPlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class FindPlayerByNameQueryHandler extends DomainQueryHandler<FindPlayerByNameQuery> {

    private final SeasonPlayerRepository seasonPlayerRepository;

    @Autowired
    public FindPlayerByNameQueryHandler(SeasonPlayerRepository seasonPlayerRepository) {
        this.seasonPlayerRepository = seasonPlayerRepository;
    }

    @Override
    public DomainQueryResponse handle(FindPlayerByNameQuery findPlayerByNameQuery) {
        return DomainQueryResponse.sucessResponse(seasonPlayerRepository.findBySimilarName(findPlayerByNameQuery.getName()));
    }
}
