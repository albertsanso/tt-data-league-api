package org.cttelsamicsterrassa.data.api.rest.season_player_result;

import org.cttelsamicsterrassa.data.core.domain.model.CompetitionInfo;
import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayer;

public record SeasonPlayerResultDto(
        CompetitionInfo competitionInfo,
        SeasonPlayer seasonPlayer,
        String matchDay,
        int matchDayNumber,
        String matchGamePoints,
        int matchGamesWon,
        String matchLinkageId,
        String matchPlayerLetter
) {
}
