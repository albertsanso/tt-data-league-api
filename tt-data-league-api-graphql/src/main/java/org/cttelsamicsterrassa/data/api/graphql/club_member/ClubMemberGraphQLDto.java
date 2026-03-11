package org.cttelsamicsterrassa.data.api.graphql.club_member;

import java.util.List;
import java.util.UUID;

public record ClubMemberGraphQLDto(
        UUID id,
        UUID clubId,
        UUID practicionerId,
        List<String> yearRanges
) {
    public static ClubMemberGraphQLDto fromDomain(org.cttelsamicsterrassa.data.core.domain.model.ClubMember clubMember) {
        return new ClubMemberGraphQLDto(
                clubMember.getId(),
                clubMember.getClub().getId(),
                clubMember.getPracticioner().getId(),
                clubMember.getYearRanges()
        );
    }
}

