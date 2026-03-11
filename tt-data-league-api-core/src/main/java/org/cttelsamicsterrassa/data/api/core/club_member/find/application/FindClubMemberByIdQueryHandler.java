package org.cttelsamicsterrassa.data.api.core.club_member.find.application;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.ClubMember;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Query handler for retrieving a club member by ID.
 * Executes the FindClubMemberByIdQuery and returns the specific club member from the repository.
 */
@Component
public class FindClubMemberByIdQueryHandler extends DomainQueryHandler<FindClubMemberByIdQuery, ClubMember> {

    private final ClubMemberRepository clubMemberRepository;

    @Autowired
    public FindClubMemberByIdQueryHandler(ClubMemberRepository clubMemberRepository) {
        this.clubMemberRepository = clubMemberRepository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DomainQueryResponse<ClubMember> handle(FindClubMemberByIdQuery findClubMemberByIdQuery) {
        return clubMemberRepository.findById(findClubMemberByIdQuery.getClubMemberId())
                .map(DomainQueryResponse::sucessResponse)
                .orElseGet(() -> (DomainQueryResponse<ClubMember>) (DomainQueryResponse<?>) DomainQueryResponse.failResponse("Club member with that ID doesn't exist"));
    }
}

