package org.cttelsamicsterrassa.data.api.core.club_member.find;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindClubMembersByPracticionerIdQueryHandler extends DomainQueryHandler<FindClubMembersByPracticionerIdQuery> {

    private final ClubMemberRepository clubMemberRepository;

    @Autowired
    public FindClubMembersByPracticionerIdQueryHandler(ClubMemberRepository clubMemberRepository) {
        this.clubMemberRepository = clubMemberRepository;
    }

    @Override
    public DomainQueryResponse handle(FindClubMembersByPracticionerIdQuery findClubMembersByPracticionerIdQuery) {
        return DomainQueryResponse.sucessResponse(
                clubMemberRepository.findByPracticionerId(
                        findClubMembersByPracticionerIdQuery.getPractitionerId()));
    }
}
