package org.cttelsamicsterrassa.data.api.rest.match;

import org.cttelsamicsterrassa.data.api.rest.shared.CompetitionInfoDto;
import org.cttelsamicsterrassa.data.core.domain.model.PlayersSingleMatch;

import java.util.UUID;

public record EnrichedMatchDto(
        UUID id,
        MatchSeasonPlayerResultDto playerLocalResultDto,
        MatchSeasonPlayerResultDto playerVisitorResultDto,
        String season,
        CompetitionInfoDto competitionInfo,
        int matchDayNumber,
        String uniqueRowMatchId
) {
    public static EnrichedMatchDto fromDomain(PlayersSingleMatch playersSingleMatch) {
        return new EnrichedMatchDto(
                playersSingleMatch.getId(),
                MatchSeasonPlayerResultDto.fromDomain(playersSingleMatch.getSeasonPlayerResultLocal()),
                MatchSeasonPlayerResultDto.fromDomain(playersSingleMatch.getSeasonPlayerResultVisitor()),
                playersSingleMatch.getSeason(),
                CompetitionInfoDto.fromDomain(playersSingleMatch.getCompetitionType()
                        , playersSingleMatch.getCompetitionCategory()
                        , playersSingleMatch.getCompetitionScope()
                        , playersSingleMatch.getCompetitionScopeTag()
                        , playersSingleMatch.getCompetitionGroup()
                        , playersSingleMatch.getCompetitionGender()
                ),
                playersSingleMatch.getMatchDayNumber(),
                playersSingleMatch.getUniqueRowMatchId()
        );
    }
}
