package org.cttelsamicsterrassa.data.api.core.club_member.find.application;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.ClubMember;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FindEnrichedClubMembersByClubIdAndYearQueryHandler extends DomainQueryHandler<FindEnrichedClubMembersByClubIdAndYearQuery, List<ClubMember>> {

    private final ClubMemberRepository clubMemberRepository;

    @Autowired
    public FindEnrichedClubMembersByClubIdAndYearQueryHandler(ClubMemberRepository clubMemberRepository) {
        this.clubMemberRepository = clubMemberRepository;
    }

    @Override
    public DomainQueryResponse<List<ClubMember>> handle(FindEnrichedClubMembersByClubIdAndYearQuery query) {
        List<ClubMember> clubMembers = clubMemberRepository.findByClubId(query.getClubId());
        return DomainQueryResponse.sucessResponse(clubMembers);
    }
}
