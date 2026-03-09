package org.cttelsamicsterrassa.data.api.rest.season_player_result;

import io.swagger.v3.oas.annotations.Operation;
import org.albertsanso.commons.command.CommandBus;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.albertsanso.commons.query.QueryBus;
import org.cttelsamicsterrassa.data.api.core.season_player_result.create.CreateSeasonPlayerResultCommand;
import org.cttelsamicsterrassa.data.core.domain.model.CompetitionInfo;
import org.cttelsamicsterrassa.data.core.domain.model.TeamRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@SeasonPlayerResultOpenAPIv1Controller
public class SeasonPlayerResultController {

    @Autowired
    private QueryBus queryBus;

    @Autowired
    private CommandBus commandBus;

    @Operation(summary = "Create season player result", description = "Create a SeasonPlayerResult from DTO")
    public ResponseEntity<SeasonPlayerResultDto> createSeasonPlayerResult(@RequestBody SeasonPlayerResultDto seasonPlayerDto) {
        CompetitionInfo competitionInfo = new CompetitionInfo(
                seasonPlayerDto.competitionInfo().type(),
                seasonPlayerDto.competitionInfo().category(),
                seasonPlayerDto.competitionInfo().scope(),
                seasonPlayerDto.competitionInfo().scopeTag(),
                seasonPlayerDto.competitionInfo().group(),
                seasonPlayerDto.competitionInfo().gender()
        );

        CreateSeasonPlayerResultCommand createSeasonPlayerResultCommand = new CreateSeasonPlayerResultCommand(
                competitionInfo,
                seasonPlayerDto.seasonPlayer().id(),
                seasonPlayerDto.matchDay(),
                seasonPlayerDto.matchDayNumber(),
                seasonPlayerDto.matchGamePoints(),
                seasonPlayerDto.matchGamesWon(),
                seasonPlayerDto.matchPlayerLetter(),
                TeamRole.LOCAL
        );

        DomainCommandResponse domainCommandResponse = commandBus.push(createSeasonPlayerResultCommand);
        return domainCommandResponse.isSuccess() ?
                ResponseEntity.ok((SeasonPlayerResultDto) domainCommandResponse.getResponse()) :
                ResponseEntity.badRequest().build();
    }
}
