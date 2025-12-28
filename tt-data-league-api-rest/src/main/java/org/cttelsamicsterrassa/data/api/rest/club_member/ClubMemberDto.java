package org.cttelsamicsterrassa.data.api.rest.club_member;

import org.cttelsamicsterrassa.data.core.domain.model.ClubMember;

import java.util.List;
import java.util.UUID;

public record ClubMemberDto(
        UUID id,
        UUID clubId,
        UUID practicionerId,
        List<String> yearRanges) {
    public static ClubMemberDto fromObject(ClubMember clubMember) {
        return new ClubMemberDto(
                clubMember.getId(),
                clubMember.getClub().getId(),
                clubMember.getPracticioner().getId(),
                clubMember.getYearRanges()
        );
    }
}
