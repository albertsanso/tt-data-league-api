package org.cttelsamicsterrassa.data.api.core.club_member.find;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.ClubMember;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FindClubMembersByClubIdQueryHandler extends DomainQueryHandler<FindClubMembersByClubIdQuery, List<ClubMember>> {

    private final ClubMemberRepository clubMemberRepository;

    @Autowired
    public FindClubMembersByClubIdQueryHandler(ClubMemberRepository clubMemberRepository) {
        this.clubMemberRepository = clubMemberRepository;
    }

    @Override
    public DomainQueryResponse<List<ClubMember>> handle(FindClubMembersByClubIdQuery findClubMembersByClubIdQuery) {
        List<ClubMember> clubMembersList = clubMemberRepository.findByClubId(
                findClubMembersByClubIdQuery.getClubId());

        return DomainQueryResponse.sucessResponse(clubMembersList);
    }
}
