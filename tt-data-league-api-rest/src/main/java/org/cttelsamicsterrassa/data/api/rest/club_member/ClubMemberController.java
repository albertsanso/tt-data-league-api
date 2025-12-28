package org.cttelsamicsterrassa.data.api.rest.club_member;

import org.albertsanso.commons.command.CommandBus;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.albertsanso.commons.query.QueryBus;
import org.cttelsamicsterrassa.data.api.core.club_member.create.CreateClubMemberCommand;
import org.cttelsamicsterrassa.data.core.domain.model.ClubMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/club_member")
public class ClubMemberController {

    @Autowired
    private QueryBus queryBus;

    @Autowired
    private CommandBus commandBus;

    @PostMapping
    public ResponseEntity<ClubMemberDto> createClubMember(@RequestBody ClubMemberDto clubMemberDto) {

        CreateClubMemberCommand createClubMemberCommand = new CreateClubMemberCommand(
                clubMemberDto.clubId(),
                clubMemberDto.practicionerId(),
                clubMemberDto.yearRanges()
        );
        DomainCommandResponse domainCommandResponse = commandBus.push(createClubMemberCommand);
        return domainCommandResponse.isSuccess() ?
                ResponseEntity.ok(ClubMemberDto.fromObject((ClubMember) domainCommandResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }
}
