package org.cttelsamicsterrassa.data.api.core.season_player.find;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayer;
import org.cttelsamicsterrassa.data.core.domain.repository.SeasonPlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class FindPlayerByNamesQueryHandler extends DomainQueryHandler<FindPlayerByNamesQuery, List<SeasonPlayer>> {

    private final SeasonPlayerRepository seasonPlayerRepository;

    @Autowired
    public FindPlayerByNamesQueryHandler(SeasonPlayerRepository seasonPlayerRepository) {
        this.seasonPlayerRepository = seasonPlayerRepository;
    }

    @Override
    public DomainQueryResponse<List<SeasonPlayer>> handle(FindPlayerByNamesQuery findPlayerByNameQuery) {
        Collection<SeasonPlayer> results = seasonPlayerRepository.findBySimilarNames(findPlayerByNameQuery.getStringToSearch());
        List<SeasonPlayer> resultList = results == null ? List.of() : new java.util.ArrayList<>(results);
        return DomainQueryResponse.sucessResponse(resultList);
    }
}