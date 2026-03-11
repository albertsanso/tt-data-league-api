package org.cttelsamicsterrassa.data.api.core.match.find.domain.model;

import org.cttelsamicsterrassa.data.core.domain.model.PlayersSingleMatch;

import java.time.ZonedDateTime;
import java.util.UUID;

public record MatchResultForDashboard(
        ZonedDateTime matchDate,
        UUID homeTeamId,
        UUID awayTeamId,
        String homeTeamName,
        String awayTeamName,
        int homeTeamScore,
        int awayTeamScore,
        String homePlayerName,
        String awayPlayerName
) {
    public static MatchResultForDashboard fromPlayersSingleMatch(PlayersSingleMatch playersSingleMatch) {
        return new MatchResultForDashboard(
                ZonedDateTime.now(),
                playersSingleMatch.getSeasonPlayerResultLocal().getSeasonPlayer().getId(),
                playersSingleMatch.getSeasonPlayerResultVisitor().getSeasonPlayer().getId(),
                playersSingleMatch.getSeasonPlayerResultLocal().getSeasonPlayer().getClubMember().getFullName(),
                playersSingleMatch.getSeasonPlayerResultVisitor().getSeasonPlayer().getClubMember().getFullName(),
                playersSingleMatch.getSeasonPlayerResultLocal().getGamesWon(),
                playersSingleMatch.getSeasonPlayerResultVisitor().getGamesWon(),
                playersSingleMatch.getSeasonPlayerResultLocal().getSeasonPlayer().getClubMember().getFullName(),
                playersSingleMatch.getSeasonPlayerResultVisitor().getSeasonPlayer().getClubMember().getFullName()
        );

    }
}
