package org.cttelsamicsterrassa.data.api.rest.club;

import org.albertsanso.commons.command.CommandBus;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.albertsanso.commons.query.QueryBus;
import org.cttelsamicsterrassa.data.api.core.club.create.CreateClubCommand;
import org.cttelsamicsterrassa.data.api.core.club.delete.DeleteClubCommand;
import org.cttelsamicsterrassa.data.api.core.club.find.FindClubByIdQuery;
import org.cttelsamicsterrassa.data.api.core.club.find.FindClubByNameQuery;
import org.cttelsamicsterrassa.data.api.core.club.find.FindClubBySimilarNameQuery;
import org.cttelsamicsterrassa.data.api.core.club.modify.ModifyClubCommand;
import org.cttelsamicsterrassa.data.core.domain.model.Club;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.Operation;

import java.util.UUID;
import java.util.List;
import java.util.Collection;
import java.util.stream.Collectors;

@ClubOpenAPIv1Controller
public class ClubController {

    @Autowired
    private QueryBus queryBus;

    @Autowired
    private CommandBus commandBus;

    @GetMapping("/find_by_id")
    @Operation(summary = "Find club by id", description = "Returns a club by its UUID")
    public ResponseEntity<ClubDto> findClubById(@RequestParam("id") UUID id) {
        FindClubByIdQuery findClubByIdQuery = new FindClubByIdQuery(id);
        DomainQueryResponse queryResponse = queryBus.push(findClubByIdQuery);
        return queryResponse.isSuccess() ?
                ResponseEntity.ok(ClubDto.fromObject((Club) queryResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/find_by_name")
    @Operation(summary = "Find club by name", description = "Returns a club that matches the exact name")
    public ResponseEntity<ClubDto> findClubByName(@RequestParam("name") String name) {
        FindClubByNameQuery findClubByNameQuery = new FindClubByNameQuery(name);
        DomainQueryResponse queryResponse = queryBus.push(findClubByNameQuery);
        return queryResponse.isSuccess() ?
                ResponseEntity.ok(ClubDto.fromObject((Club) queryResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

    @GetMapping("/find_by_similar_name")
    @Operation(summary = "Find clubs by similar name", description = "Search clubs with a name similar to the provided value")
    public ResponseEntity<List<ClubDto>> findClubBySimilarName(@RequestParam("name") String name) {
        FindClubBySimilarNameQuery findClubBySimilarNameQuery = new FindClubBySimilarNameQuery(name);
        DomainQueryResponse queryResponse = queryBus.push(findClubBySimilarNameQuery);
        if (!queryResponse.isSuccess()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        Collection<?> response = (Collection<?>) queryResponse.getResponse();
        if (response == null) {
            return ResponseEntity.ok(List.of());
        }

        List<ClubDto> dtos = response.stream()
                .filter(Club.class::isInstance)
                .map(Club.class::cast)
                .map(ClubDto::fromObject)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @Operation(summary = "Create club", description = "Create a new club from ClubDto")
    public ResponseEntity<ClubDto> createClub(@RequestBody ClubDto clubDto) {
        CreateClubCommand createClubCommand = new CreateClubCommand(
                clubDto.id(),
                clubDto.name(),
                clubDto.yearRanges()
        );

        DomainCommandResponse domainCommandResponse = commandBus.push(createClubCommand);
        return domainCommandResponse.isSuccess() ?
                ResponseEntity.ok(ClubDto.fromObject((Club) domainCommandResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete club", description = "Delete an existing club by id")
    public ResponseEntity<Void> deleteClub(@PathVariable("id") UUID id) {
        DeleteClubCommand deleteClubCommand = new DeleteClubCommand(id);
        DomainCommandResponse domainCommandResponse = commandBus.push(deleteClubCommand);
        return domainCommandResponse.isSuccess() ?
                ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PutMapping
    @Operation(summary = "Modify club", description = "Modify an existing club using ClubDto")
    public ResponseEntity<ClubDto> modifyClub(@RequestBody ClubDto clubDto) {
        ModifyClubCommand modifyClubCommand = new ModifyClubCommand(
                clubDto.id(),
                clubDto.name(),
                clubDto.yearRanges()
        );
        DomainCommandResponse domainCommandResponse = commandBus.push(modifyClubCommand);
        return domainCommandResponse.isSuccess() ?
                ResponseEntity.ok(ClubDto.fromObject((Club) domainCommandResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
