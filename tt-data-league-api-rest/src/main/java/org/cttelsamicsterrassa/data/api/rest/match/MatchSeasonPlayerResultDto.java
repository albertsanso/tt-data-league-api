package org.cttelsamicsterrassa.data.api.rest.match;

import org.cttelsamicsterrassa.data.api.rest.season_player.SeasonPlayerDto;
import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayerResult;

import java.util.Arrays;

public record MatchSeasonPlayerResultDto(
        SeasonPlayerDto seasonPlayer,
        String matchDay,
        int matchDayNumber,
        String matchGamePoints,
        int matchGamesWon,
        String matchPlayerLetter
) {
    public static MatchSeasonPlayerResultDto fromDomain(SeasonPlayerResult seasonPlayerResult) {
        return new MatchSeasonPlayerResultDto(
                SeasonPlayerDto.fromDomain(seasonPlayerResult.getSeasonPlayer()),
                seasonPlayerResult.getMatchDay(),
                seasonPlayerResult.getMatchDayNumber(),
                Arrays.toString(seasonPlayerResult.getGamePoints()),
                seasonPlayerResult.getGamesWon(),
                seasonPlayerResult.getPlayerLetter()
        );
    }
}
