package org.cttelsamicsterrassa.data.api.rest.season_player;

import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayer;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record SeasonPlayerDto(
        UUID id,
        UUID clubMemberId,
        String licenseId,
        String licenseTag,
        String yearRange) {
    public static SeasonPlayerDto fromObject(SeasonPlayer seasonPlayer) {
        return new SeasonPlayerDto(
                seasonPlayer.getId(),
                seasonPlayer.getClubMember().getId(),
                seasonPlayer.getLicense().id(),
                seasonPlayer.getLicense().tag(),
                seasonPlayer.getYearRange()
        );
    }

    public static List<SeasonPlayerDto> fromObjectList(Object payload) {
        if (payload == null) return Collections.emptyList();
        if (payload instanceof List) {
            List<?> raw = (List<?>) payload;
            return raw.stream().map(item -> fromObject((SeasonPlayer) item)).collect(Collectors.toList());
        }
        // If single object, return single-element list
        return List.of(fromObject((SeasonPlayer) payload));
    }
}
