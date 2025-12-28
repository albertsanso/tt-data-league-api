package org.cttelsamicsterrassa.data.api.core.club.application.find;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindClubByIdQueryHandler extends DomainQueryHandler<FindClubByIdQuery> {

    private final ClubRepository clubRepository;

    @Autowired
    public FindClubByIdQueryHandler(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Override
    public DomainQueryResponse handle(FindClubByIdQuery findClubByIdQuery) {
        return clubRepository.findById(findClubByIdQuery.getClubId())
                .map(DomainQueryResponse::sucessResponse)
                .orElseGet(() -> DomainQueryResponse.failResponse("Club with that ID doesn't exist"));
    }
}
