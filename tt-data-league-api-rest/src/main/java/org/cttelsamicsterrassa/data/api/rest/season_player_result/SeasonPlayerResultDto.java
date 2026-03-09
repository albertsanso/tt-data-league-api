package org.cttelsamicsterrassa.data.api.rest.season_player_result;

import org.cttelsamicsterrassa.data.api.rest.season_player.SeasonPlayerDto;
import org.cttelsamicsterrassa.data.api.rest.shared.CompetitionInfoDto;
import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayerResult;

import java.util.Arrays;

public record SeasonPlayerResultDto(
        CompetitionInfoDto competitionInfo,
        SeasonPlayerDto seasonPlayer,
        String matchDay,
        int matchDayNumber,
        String matchGamePoints,
        int matchGamesWon,
        String matchLinkageId,
        String matchPlayerLetter
) {
    public static SeasonPlayerResultDto fromDomain(SeasonPlayerResult seasonPlayerResult) {
        return new SeasonPlayerResultDto(
                CompetitionInfoDto.fromDomain(seasonPlayerResult.getCompetitionInfo()),
                SeasonPlayerDto.fromDomain(seasonPlayerResult.getSeasonPlayer()),
                seasonPlayerResult.getMatchDay(),
                seasonPlayerResult.getMatchDayNumber(),
                Arrays.toString(seasonPlayerResult.getGamePoints()),
                seasonPlayerResult.getGamesWon(),
                "",
                seasonPlayerResult.getPlayerLetter()
        );
    }
}
