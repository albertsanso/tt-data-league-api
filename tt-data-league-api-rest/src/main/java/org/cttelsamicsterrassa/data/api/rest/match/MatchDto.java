package org.cttelsamicsterrassa.data.api.rest.match;

import org.cttelsamicsterrassa.data.core.domain.model.PlayersSingleMatch;

import java.util.UUID;

public record MatchDto(
        UUID id,
        String homeTeam,
        String awayTeam,
        Integer homeScore,
        Integer awayScore,
        String matchDate) {

    public static MatchDto fromDomain(PlayersSingleMatch match) {
        return new MatchDto(
                match.getId(),
                match.getSeasonPlayerResultLocal().getSeasonPlayer().getClubMember().getFullName(),
                match.getSeasonPlayerResultVisitor().getSeasonPlayer().getClubMember().getFullName(),
                match.getSeasonPlayerResultLocal().getGamesWon(),
                match.getSeasonPlayerResultVisitor().getGamesWon(),
                match.getSeasonPlayerResultLocal().getMatchDay()
        );
    }
}
