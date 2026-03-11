package org.cttelsamicsterrassa.data.api.core.club.find.application;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.Club;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Query handler for retrieving all clubs.
 * Executes the FindAllClubsQuery and returns all clubs from the repository.
 */
@Component
public class FindAllClubsQueryHandler extends DomainQueryHandler<FindAllClubsQuery, List<Club>> {

    private final ClubRepository clubRepository;

    @Autowired
    public FindAllClubsQueryHandler(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Override
    public DomainQueryResponse<List<Club>> handle(FindAllClubsQuery findAllClubsQuery) {
        Collection<Club> results = clubRepository.findAll();
        if (results == null) {
            results = List.of();
        }
        // Return a mutable copy if needed by later consumers
        return DomainQueryResponse.sucessResponse(new ArrayList<>(results));
    }
}

