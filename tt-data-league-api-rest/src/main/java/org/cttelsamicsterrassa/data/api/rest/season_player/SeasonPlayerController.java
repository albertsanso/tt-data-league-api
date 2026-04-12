package org.cttelsamicsterrassa.data.api.rest.season_player;

import org.albertsanso.commons.command.CommandBus;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.albertsanso.commons.query.QueryBus;
import org.cttelsamicsterrassa.data.api.core.season_player.create.CreateSeasonPlayerCommand;
import org.cttelsamicsterrassa.data.api.core.season_player.delete.DeleteSeasonPlayerCommand;
import org.cttelsamicsterrassa.data.api.core.season_player.find.FindPlayerByNameQuery;
import org.cttelsamicsterrassa.data.api.core.season_player.find.FindPlayerByNamesQuery;
import org.cttelsamicsterrassa.data.api.core.season_player.find.FindSeasonPlayerByIdQuery;
import org.cttelsamicsterrassa.data.api.core.season_player.find.FindSeasonPlayerByLicenseQuery;
import org.cttelsamicsterrassa.data.api.core.season_player.find.FindSeasonPlayerByPracticionerIdQuery;
import org.cttelsamicsterrassa.data.api.core.season_player.modify.ModifySeasonPlayerCommand;
import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.Operation;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@SeasonPlayerOpenAPIv1Controller
public class SeasonPlayerController {

    @Autowired
    private QueryBus queryBus;

    @Autowired
    private CommandBus commandBus;

    @PostMapping
    @Operation(summary = "Create season player", description = "Create a season player from SeasonPlayerDto")
    public ResponseEntity<SeasonPlayerDto> createSeasonPlayer(@RequestBody SeasonPlayerDto seasonPlayerDto) {

        CreateSeasonPlayerCommand command = new CreateSeasonPlayerCommand(
                seasonPlayerDto.clubMemberId(),
                seasonPlayerDto.licenseId(),
                seasonPlayerDto.licenseTag(),
                seasonPlayerDto.yearRange()
        );
        DomainCommandResponse domainCommandResponse = commandBus.push(command);
        return domainCommandResponse.isSuccess() ?
                ResponseEntity.ok(SeasonPlayerDto.fromDomain((SeasonPlayer) domainCommandResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get season player by id", description = "Returns a season player by UUID")
    public ResponseEntity<SeasonPlayerDto> findById(@PathVariable("id") UUID id) {
        FindSeasonPlayerByIdQuery domainQuery = new FindSeasonPlayerByIdQuery(id);
        DomainQueryResponse queryResponse = queryBus.push(domainQuery);
        return queryResponse.isSuccess() ?
                ResponseEntity.ok(SeasonPlayerDto.fromDomain((SeasonPlayer) queryResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete season player", description = "Delete a season player by id")
    public ResponseEntity<Void> deleteSeasonPlayer(@PathVariable("id") UUID id) {
        DeleteSeasonPlayerCommand command = new DeleteSeasonPlayerCommand(id);
        DomainCommandResponse commandResponse = commandBus.push(command);
        return commandResponse.isSuccess() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PutMapping
    @Operation(summary = "Modify season player", description = "Modify a season player using SeasonPlayerDto")
    public ResponseEntity<SeasonPlayerDto> modifySeasonPlayer(@RequestBody SeasonPlayerDto seasonPlayerDto) {
        ModifySeasonPlayerCommand command = new ModifySeasonPlayerCommand(
                seasonPlayerDto.id(),
                seasonPlayerDto.clubMemberId(),
                seasonPlayerDto.licenseId(),
                seasonPlayerDto.licenseTag(),
                seasonPlayerDto.yearRange()
        );

        DomainCommandResponse response = commandBus.push(command);
        return response.isSuccess()
                ? ResponseEntity.ok(SeasonPlayerDto.fromDomain((SeasonPlayer) response.getResponse()))
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/search_by_name/{username}")
    @Operation(summary = "Search season players by name", description = "Search players by similar name")
    public ResponseEntity<List<SeasonPlayerDto>> findAllPlayersBySimilarName(@PathVariable("username") String name) {

        FindPlayerByNameQuery domainQuery = new FindPlayerByNameQuery(name);
        DomainQueryResponse queryResponse = queryBus.push(domainQuery);

        return queryResponse.isSuccess() ?
                ResponseEntity.ok(SeasonPlayerDto.fromObjectList(queryResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/search_by_names/{names}")
    @Operation(summary = "Search season players by names", description = "Search by a list of names (comma-separated)")
    public ResponseEntity<List<SeasonPlayerDto>> findAllPlayersBySimilarNames(@PathVariable("names") List<String> names) {
        FindPlayerByNamesQuery domainQuery = new FindPlayerByNamesQuery(ZonedDateTime.now(), UUID.randomUUID(), names);
        DomainQueryResponse queryResponse = queryBus.push(domainQuery);

        return queryResponse.isSuccess() ?
                ResponseEntity.ok(SeasonPlayerDto.fromObjectList(queryResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping("/find_by_license")
    @Operation(summary = "Find season players by license", description = "Return players matching license info")
    public ResponseEntity<List<SeasonPlayerDto>> findByLicense(@RequestBody LicenseDto license) {
        FindSeasonPlayerByLicenseQuery domainQuery = new FindSeasonPlayerByLicenseQuery(license.licenseTag(), license.licenseId());
        DomainQueryResponse queryResponse = queryBus.push(domainQuery);
        return queryResponse.isSuccess() ?
                ResponseEntity.ok(SeasonPlayerDto.fromObjectList(queryResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/find_by_practicioner/{practicionerId}")
    @Operation(summary = "Find season players by practicioner id", description = "Return season players for a practicioner")
    public ResponseEntity<List<SeasonPlayerDto>> findByPracticionerId(@PathVariable("practicionerId") UUID practicionerId) {
        FindSeasonPlayerByPracticionerIdQuery domainQuery = new FindSeasonPlayerByPracticionerIdQuery(practicionerId);
        DomainQueryResponse queryResponse = queryBus.push(domainQuery);
        return queryResponse.isSuccess() ?
                ResponseEntity.ok(SeasonPlayerDto.fromObjectList(queryResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
