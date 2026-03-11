package org.cttelsamicsterrassa.data.api.graphql.club;

import java.util.List;
import java.util.UUID;

public record ClubGraphQLDto(
        UUID id,
        String name,
        List<String> yearRanges
) {
    public static ClubGraphQLDto fromDomain(org.cttelsamicsterrassa.data.core.domain.model.Club club) {
        return new ClubGraphQLDto(
                club.getId(),
                club.getName(),
                club.getYearRanges()
        );
    }
}

