package org.cttelsamicsterrassa.data.api.core.club.find.application;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.Club;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindClubByIdQueryHandler extends DomainQueryHandler<FindClubByIdQuery, Club> {

    private final ClubRepository clubRepository;

    @Autowired
    public FindClubByIdQueryHandler(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DomainQueryResponse<Club> handle(FindClubByIdQuery findClubByIdQuery) {
        return clubRepository.findById(findClubByIdQuery.getClubId())
                .map(DomainQueryResponse::sucessResponse)
                .orElseGet(() -> (DomainQueryResponse<Club>) (DomainQueryResponse<?>) DomainQueryResponse.failResponse("Club with that ID doesn't exist"));
    }
}
