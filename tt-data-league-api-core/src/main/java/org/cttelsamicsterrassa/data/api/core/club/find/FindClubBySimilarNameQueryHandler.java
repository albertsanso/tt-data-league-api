package org.cttelsamicsterrassa.data.api.core.club.find;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.Club;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

@Component
public class FindClubBySimilarNameQueryHandler extends DomainQueryHandler<FindClubBySimilarNameQuery, List<Club>> {

    private final ClubRepository clubRepository;

    @Autowired
    public FindClubBySimilarNameQueryHandler(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Override
    public DomainQueryResponse<List<Club>> handle(FindClubBySimilarNameQuery findClubBySimilarNameQuery) {
        Collection<Club> results = clubRepository.searchBySimilarName(findClubBySimilarNameQuery.getNameToSearch());
        if (results == null) {
            results = List.of();
        }
        // return a mutable copy if needed by later consumers
        return DomainQueryResponse.sucessResponse(new ArrayList<>(results));
    }
}
