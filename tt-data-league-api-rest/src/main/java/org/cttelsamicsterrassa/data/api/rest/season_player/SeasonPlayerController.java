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
import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/season_player")
public class SeasonPlayerController {

    @Autowired
    private QueryBus queryBus;

    @Autowired
    private CommandBus commandBus;

    @PostMapping
    public ResponseEntity<SeasonPlayerDto> createSeasonPlayer(@RequestBody SeasonPlayerDto seasonPlayerDto) {

        CreateSeasonPlayerCommand command = new CreateSeasonPlayerCommand(
                seasonPlayerDto.clubMemberId(),
                seasonPlayerDto.licenseId(),
                seasonPlayerDto.licenseTag(),
                seasonPlayerDto.yearRange()
        );
        DomainCommandResponse domainCommandResponse = commandBus.push(command);
        return domainCommandResponse.isSuccess() ?
                ResponseEntity.ok(SeasonPlayerDto.fromObject((SeasonPlayer) domainCommandResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeasonPlayerDto> findById(@PathVariable("id") UUID id) {
        FindSeasonPlayerByIdQuery domainQuery = new FindSeasonPlayerByIdQuery(id);
        DomainQueryResponse queryResponse = queryBus.push(domainQuery);
        return queryResponse.isSuccess() ?
                ResponseEntity.ok(SeasonPlayerDto.fromObject((SeasonPlayer) queryResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeasonPlayer(@PathVariable("id") UUID id) {
        DeleteSeasonPlayerCommand command = new DeleteSeasonPlayerCommand(id);
        DomainCommandResponse commandResponse = commandBus.push(command);
        return commandResponse.isSuccess() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/search_by_name/{username}")
    public ResponseEntity<List<SeasonPlayerDto>> findAllPlayersBySimilarName(@PathVariable("username") String name) {

        FindPlayerByNameQuery domainQuery = new FindPlayerByNameQuery(name);
        DomainQueryResponse queryResponse = queryBus.push(domainQuery);

        return queryResponse.isSuccess() ?
                ResponseEntity.ok(SeasonPlayerDto.fromObjectList(queryResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/search_by_names/{names}")
    public ResponseEntity<List<SeasonPlayerDto>> findAllPlayersBySimilarNames(@PathVariable("names") List<String> names) {
        FindPlayerByNamesQuery domainQuery = new FindPlayerByNamesQuery(ZonedDateTime.now(), UUID.randomUUID(), names);
        DomainQueryResponse queryResponse = queryBus.push(domainQuery);

        return queryResponse.isSuccess() ?
                ResponseEntity.ok(SeasonPlayerDto.fromObjectList(queryResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping("/find_by_license")
    public ResponseEntity<List<SeasonPlayerDto>> findByLicense(@RequestBody LicenseDto license) {
        FindSeasonPlayerByLicenseQuery domainQuery = new FindSeasonPlayerByLicenseQuery(license.licenseTag(), license.licenseId());
        DomainQueryResponse queryResponse = queryBus.push(domainQuery);
        return queryResponse.isSuccess() ?
                ResponseEntity.ok(SeasonPlayerDto.fromObjectList(queryResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/find_by_practicioner/{practicionerId}")
    public ResponseEntity<List<SeasonPlayerDto>> findByPracticionerId(@PathVariable("practicionerId") UUID practicionerId) {
        FindSeasonPlayerByPracticionerIdQuery domainQuery = new FindSeasonPlayerByPracticionerIdQuery(practicionerId);
        DomainQueryResponse queryResponse = queryBus.push(domainQuery);
        return queryResponse.isSuccess() ?
                ResponseEntity.ok(SeasonPlayerDto.fromObjectList(queryResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
