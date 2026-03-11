package org.cttelsamicsterrassa.data.api.core.club_member.find.application;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.ClubMember;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * Query handler for retrieving a club member by practitioner and club IDs.
 * Executes the FindClubMemberByPracticionerIdAndClubIdQuery and returns
 * the specific club member that matches both criteria.
 */
@Component
public class FindClubMemberByPracticionerIdAndClubIdQueryHandler extends DomainQueryHandler<FindClubMemberByPracticionerIdAndClubIdQuery, ClubMember> {

    private final ClubMemberRepository clubMemberRepository;

    @Autowired
    public FindClubMemberByPracticionerIdAndClubIdQueryHandler(ClubMemberRepository clubMemberRepository) {
        this.clubMemberRepository = clubMemberRepository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DomainQueryResponse<ClubMember> handle(FindClubMemberByPracticionerIdAndClubIdQuery query) {
        Optional<ClubMember> result = clubMemberRepository.findByPracticionerIdAndClubId(
                query.getPractitionerId(),
                query.getClubId()
        );

        return result.map(DomainQueryResponse::sucessResponse)
                .orElseGet(() -> (DomainQueryResponse<ClubMember>) (DomainQueryResponse<?>)
                    DomainQueryResponse.failResponse("Club member with the given practitioner and club doesn't exist"));
    }
}

