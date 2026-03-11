package org.cttelsamicsterrassa.data.api.core.club.find.application;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.Club;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindClubByNameQueryHandler extends DomainQueryHandler<FindClubByNameQuery, Club> {

    private final ClubRepository clubRepository;

    @Autowired
    public FindClubByNameQueryHandler(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DomainQueryResponse<Club> handle(FindClubByNameQuery findClubByNameQuery) {
        return clubRepository.findByName(findClubByNameQuery.getNameToSearch())
                .map(DomainQueryResponse::sucessResponse)
                .orElseGet(() -> (DomainQueryResponse<Club>) (DomainQueryResponse<?>) DomainQueryResponse.failResponse("Club with that Name doesn't exist"));
    }
}
