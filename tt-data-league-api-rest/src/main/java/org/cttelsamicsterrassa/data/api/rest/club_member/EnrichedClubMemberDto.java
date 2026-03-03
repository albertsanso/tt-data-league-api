package org.cttelsamicsterrassa.data.api.rest.club_member;

import org.cttelsamicsterrassa.data.api.rest.club.ClubDto;
import org.cttelsamicsterrassa.data.api.rest.practicioner.PracticionerDto;

import java.util.List;
import java.util.UUID;

public record EnrichedClubMemberDto(
        UUID id,
        ClubDto club,
        PracticionerDto practicioner,
        List<String> yearRanges) {
    public static EnrichedClubMemberDto fromDomain(org.cttelsamicsterrassa.data.core.domain.model.ClubMember clubMember) {
        return new EnrichedClubMemberDto(
                clubMember.getId(),
                ClubDto.fromObject(clubMember.getClub()),
                PracticionerDto.fromDomain(clubMember.getPracticioner()),
                clubMember.getYearRanges()
        );
    }
}
