package org.cttelsamicsterrassa.data.api.core.club.application.find;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindClubBySimilarNameQueryHandler extends DomainQueryHandler<FindClubBySimilarNameQuery> {

    private final ClubRepository clubRepository;

    @Autowired
    public FindClubBySimilarNameQueryHandler(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Override
    public DomainQueryResponse handle(FindClubBySimilarNameQuery findClubBySimilarNameQuery) {
        return DomainQueryResponse.sucessResponse(clubRepository.searchBySimilarName(findClubBySimilarNameQuery.getNameToSearch()));
    }
}
