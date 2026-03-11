package org.cttelsamicsterrassa.data.api.core.club_member.find.application;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.ClubMember;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Query handler for retrieving all club members.
 * Executes the FindAllClubMembersQuery and returns all club members from the repository.
 */
@Component
public class FindAllClubMembersQueryHandler extends DomainQueryHandler<FindAllClubMembersQuery, List<ClubMember>> {

    private final ClubMemberRepository clubMemberRepository;

    @Autowired
    public FindAllClubMembersQueryHandler(ClubMemberRepository clubMemberRepository) {
        this.clubMemberRepository = clubMemberRepository;
    }

    @Override
    public DomainQueryResponse<List<ClubMember>> handle(FindAllClubMembersQuery findAllClubMembersQuery) {
        // TODO: Implement once ClubMemberRepository.findAll() is available
        // For now return empty list as a placeholder
        return DomainQueryResponse.sucessResponse(new ArrayList<>());
    }
}



