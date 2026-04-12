package org.cttelsamicsterrassa.data.api.rest.season_player_result;

import io.swagger.v3.oas.annotations.Operation;
import org.albertsanso.commons.command.CommandBus;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.albertsanso.commons.query.QueryBus;
import org.cttelsamicsterrassa.data.api.core.season_player_result.create.CreateSeasonPlayerResultCommand;
import org.cttelsamicsterrassa.data.api.core.season_player_result.delete.DeleteSeasonPlayerResultCommand;
import org.cttelsamicsterrassa.data.api.core.season_player_result.find.FindSeasonPlayerResultByIdQuery;
import org.cttelsamicsterrassa.data.api.core.season_player_result.modify.ModifySeasonPlayerResultCommand;
import org.cttelsamicsterrassa.data.core.domain.model.CompetitionInfo;
import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayerResult;
import org.cttelsamicsterrassa.data.core.domain.model.TeamRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@SeasonPlayerResultOpenAPIv1Controller
public class SeasonPlayerResultController {

    @Autowired
    private QueryBus queryBus;

    @Autowired
    private CommandBus commandBus;

    @PostMapping
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
                ResponseEntity.ok(SeasonPlayerResultDto.fromDomain((SeasonPlayerResult) domainCommandResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get season player result by id", description = "Returns a season player result by UUID")
    public ResponseEntity<SeasonPlayerResultDto> findById(@PathVariable("id") UUID id) {
        DomainQueryResponse queryResponse = queryBus.push(new FindSeasonPlayerResultByIdQuery(id));
        return queryResponse.isSuccess()
                ? ResponseEntity.ok(SeasonPlayerResultDto.fromDomain((SeasonPlayerResult) queryResponse.getResponse()))
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PutMapping
    @Operation(summary = "Modify season player result", description = "Modify a season player result using SeasonPlayerResultDto")
    public ResponseEntity<SeasonPlayerResultDto> modifySeasonPlayerResult(@RequestBody SeasonPlayerResultDto seasonPlayerDto) {
        CompetitionInfo competitionInfo = new CompetitionInfo(
                seasonPlayerDto.competitionInfo().type(),
                seasonPlayerDto.competitionInfo().category(),
                seasonPlayerDto.competitionInfo().scope(),
                seasonPlayerDto.competitionInfo().scopeTag(),
                seasonPlayerDto.competitionInfo().group(),
                seasonPlayerDto.competitionInfo().gender()
        );

        ModifySeasonPlayerResultCommand command = new ModifySeasonPlayerResultCommand(
                seasonPlayerDto.id(),
                competitionInfo,
                seasonPlayerDto.seasonPlayer().id(),
                seasonPlayerDto.matchDay(),
                seasonPlayerDto.matchDayNumber(),
                seasonPlayerDto.matchGamePoints(),
                seasonPlayerDto.matchGamesWon(),
                seasonPlayerDto.matchPlayerLetter(),
                TeamRole.LOCAL
        );

        DomainCommandResponse commandResponse = commandBus.push(command);
        return commandResponse.isSuccess()
                ? ResponseEntity.ok(SeasonPlayerResultDto.fromDomain((SeasonPlayerResult) commandResponse.getResponse()))
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete season player result", description = "Delete a season player result by UUID")
    public ResponseEntity<Void> deleteSeasonPlayerResult(@PathVariable("id") UUID id) {
        DomainCommandResponse commandResponse = commandBus.push(new DeleteSeasonPlayerResultCommand(id));
        return commandResponse.isSuccess()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
