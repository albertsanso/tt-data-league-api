package org.cttelsamicsterrassa.data.api.core.club_member.find;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.ClubMember;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubMemberRepository;
import org.cttelsamicsterrassa.data.core.domain.repository.ClubRepository;
import org.cttelsamicsterrassa.data.core.domain.repository.PracticionerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FindEnrichedClubMembersByClubIdAndYearQueryHandler extends DomainQueryHandler<FindEnrichedClubMembersByClubIdAndYearQuery> {

    private final ClubMemberRepository clubMemberRepository;

    private final ClubRepository clubRepository;

    private final PracticionerRepository practicionerRepository;

    @Autowired
    public FindEnrichedClubMembersByClubIdAndYearQueryHandler(ClubMemberRepository clubMemberRepository, ClubRepository clubRepository, PracticionerRepository practicionerRepository) {
        this.clubMemberRepository = clubMemberRepository;
        this.clubRepository = clubRepository;
        this.practicionerRepository = practicionerRepository;
    }

    @Override
    public DomainQueryResponse handle(FindEnrichedClubMembersByClubIdAndYearQuery query) {
        /*
        List<ClubMember> clubMembers = clubMemberRepository.findByClubId(query.getClubId());
        List<ClubMember> enrichedClubMembers = clubMembers.stream().map(clubMember -> {

            clubMember.setClub(clubRepository.findById(clubMember.getClubId()).orElse(null));
            clubMember.setPracticioner(practicionerRepository.findById(clubMember.getPracticionerId()).orElse(null));

            ClubMember enrichedClubMember = new ClubMember(clubMember.getId(), clubMember.getClubId(), clubMember.getPracticionerId(), clubMember.getYearRanges());
            return clubMember;
        }).toList();
        return DomainQueryResponse.sucessResponse(clubMembers);
        */
        return null;
    }
}
