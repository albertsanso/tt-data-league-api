package org.cttelsamicsterrassa.data.api.rest.club;

import org.albertsanso.commons.command.CommandBus;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.albertsanso.commons.query.QueryBus;
import org.cttelsamicsterrassa.data.api.core.club.application.create.CreateClubCommand;
import org.cttelsamicsterrassa.data.core.domain.model.Club;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/club")
public class ClubController {

    @Autowired
    private QueryBus queryBus;

    @Autowired
    private CommandBus commandBus;

    @PostMapping("/create")
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
}
