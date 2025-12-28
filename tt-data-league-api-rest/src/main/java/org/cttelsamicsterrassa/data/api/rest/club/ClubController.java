package org.cttelsamicsterrassa.data.api.rest.club;

import org.albertsanso.commons.command.CommandBus;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.albertsanso.commons.query.QueryBus;
import org.cttelsamicsterrassa.data.api.core.club.application.create.CreateClubCommand;
import org.cttelsamicsterrassa.data.api.core.club.application.delete.DeleteClubCommand;
import org.cttelsamicsterrassa.data.api.core.club.application.find.FindClubByIdQuery;
import org.cttelsamicsterrassa.data.api.core.club.application.find.FindClubByNameQuery;
import org.cttelsamicsterrassa.data.api.core.club.application.modify.ModifyClubCommand;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/club")
public class ClubController {

    @Autowired
    private QueryBus queryBus;

    @Autowired
    private CommandBus commandBus;

    @GetMapping("/find_by_id")
    public ResponseEntity<ClubDto> findClubById(@RequestParam("id") UUID id) {
        FindClubByIdQuery findClubByIdQuery = new FindClubByIdQuery(id);
        DomainQueryResponse queryResponse = queryBus.push(findClubByIdQuery);
        return queryResponse.isSuccess() ?
                ResponseEntity.ok(ClubDto.fromObject((Club) queryResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/find_by_name")
    public ResponseEntity<ClubDto> findClubByName(@RequestParam("name") String name) {
        FindClubByNameQuery findClubByNameQuery = new FindClubByNameQuery(name);
        DomainQueryResponse queryResponse = queryBus.push(findClubByNameQuery);
        return queryResponse.isSuccess() ?
                ResponseEntity.ok(ClubDto.fromObject((Club) queryResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

    @PostMapping
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
    public ResponseEntity<Void> deleteClub(@PathVariable("id") UUID id) {
        DeleteClubCommand deleteClubCommand = new DeleteClubCommand(id);
        DomainCommandResponse domainCommandResponse = commandBus.push(deleteClubCommand);
        return domainCommandResponse.isSuccess() ?
                ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PutMapping
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
