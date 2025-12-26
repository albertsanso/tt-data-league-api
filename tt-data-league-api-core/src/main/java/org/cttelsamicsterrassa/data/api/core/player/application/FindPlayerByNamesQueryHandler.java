package org.cttelsamicsterrassa.data.api.core.player.application;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.repository.SeasonPlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindPlayerByNamesQueryHandler extends DomainQueryHandler<FindPlayerByNamesQuery> {

    private final SeasonPlayerRepository seasonPlayerRepository;

    @Autowired
    public FindPlayerByNamesQueryHandler(SeasonPlayerRepository seasonPlayerRepository) {
        this.seasonPlayerRepository = seasonPlayerRepository;
    }

    @Override
    public DomainQueryResponse handle(FindPlayerByNamesQuery findPlayerByNameQuery) {
        return DomainQueryResponse.sucessResponse(seasonPlayerRepository.findBySimilarNames(findPlayerByNameQuery.getStringToSearch()));
    }
}