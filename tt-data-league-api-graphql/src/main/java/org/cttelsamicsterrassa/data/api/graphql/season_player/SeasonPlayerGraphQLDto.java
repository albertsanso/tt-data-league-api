package org.cttelsamicsterrassa.data.api.graphql.season_player;

import java.util.UUID;

public record SeasonPlayerGraphQLDto(
        UUID id,
        UUID clubMemberId,
        String licenseId,
        String licenseTag,
        String yearRange
) {
    public static SeasonPlayerGraphQLDto fromDomain(org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayer seasonPlayer) {
        return new SeasonPlayerGraphQLDto(
                seasonPlayer.getId(),
                seasonPlayer.getClubMember().getId(),
                seasonPlayer.getLicense().id(),
                seasonPlayer.getLicense().tag(),
                seasonPlayer.getYearRange()
        );
    }
}

