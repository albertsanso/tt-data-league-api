package org.cttelsamicsterrassa.data.api.rest.season_player_result;

import java.util.UUID;

public record SeasonPlayerResultDto(
        CompetitionInfoDto competitionInfo,
        UUID seasonPlayerId,
        String matchDay,
        int matchDayNumber,
        String matchGamePoints,
        int matchGamesWon,
        String matchLinkageId,
        String matchPlayerLetter
) {
}
