package org.cttelsamicsterrassa.data.api.graphql.match;

import java.util.UUID;

public record MatchGraphQLDto(
        UUID id,
        String season,
        String competitionType,
        String competitionCategory,
        String competitionScope,
        String competitionScopeTag,
        String competitionGroup,
        String competitionGender,
        String matchDayNumber,
        String uniqueRowMatchId,
        String localPlayerName,
        String localPlayerLetter,
        int localPlayerScore,
        String visitorPlayerName,
        String visitorPlayerLetter,
        int visitorPlayerScore,
        String matchDateTime
) {
    public static MatchGraphQLDto fromDomain(org.cttelsamicsterrassa.data.core.domain.model.PlayersSingleMatch match) {
        return new MatchGraphQLDto(
                match.getId(),
                match.getSeason(),
                match.getCompetitionType(),
                match.getCompetitionCategory(),
                match.getCompetitionScope(),
                match.getCompetitionScopeTag(),
                match.getCompetitionGroup(),
                match.getCompetitionGender(),
                String.valueOf(match.getMatchDayNumber()),
                match.getUniqueRowMatchId(),
                match.getSeasonPlayerResultLocal().getSeasonPlayer().getClubMember().getPracticioner().getFullName(),
                match.getSeasonPlayerResultLocal().getMatchInfo().playerLetter(),
                match.getSeasonPlayerResultLocal().getMatchInfo().gamesWon(),
                match.getSeasonPlayerResultVisitor().getSeasonPlayer().getClubMember().getPracticioner().getFullName(),
                match.getSeasonPlayerResultVisitor().getMatchInfo().playerLetter(),
                match.getSeasonPlayerResultVisitor().getMatchInfo().gamesWon(),
                match.getMatchDateTime().toString()
        );
    }
}


