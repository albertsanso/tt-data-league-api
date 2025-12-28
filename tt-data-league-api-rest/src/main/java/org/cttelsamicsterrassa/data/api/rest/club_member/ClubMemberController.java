package org.cttelsamicsterrassa.data.api.rest.club_member;

import org.albertsanso.commons.command.CommandBus;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.albertsanso.commons.query.QueryBus;
import org.cttelsamicsterrassa.data.api.core.club_member.create.CreateClubMemberCommand;
import org.cttelsamicsterrassa.data.api.core.club_member.find.FindClubMembersByClubIdQuery;
import org.cttelsamicsterrassa.data.api.core.club_member.find.FindClubMembersByPracticionerIdQuery;
import org.cttelsamicsterrassa.data.api.core.club_member.find.FindClubMembersByPracticionerIdQueryHandler;
import org.cttelsamicsterrassa.data.core.domain.model.ClubMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

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

    @GetMapping("/find_by_club_id/{clubId}")
    public ResponseEntity<List<ClubMemberDto>> getClubMembersByClubId(@PathVariable("clubId") UUID clubId) {
        FindClubMembersByClubIdQuery query = new FindClubMembersByClubIdQuery(clubId);
        return returnMultipleCLubMembersResponse(queryBus.push(query));
    }

    @GetMapping("/find_by_practicioner_id/{practicionerId")
    public ResponseEntity<List<ClubMemberDto>> getClubMembersByPracticionerId(@PathVariable("practicionerId") UUID practicionerId) {
        FindClubMembersByPracticionerIdQuery query = new FindClubMembersByPracticionerIdQuery(practicionerId);
        return returnMultipleCLubMembersResponse(queryBus.push(query));
    }

    private ResponseEntity<List<ClubMemberDto>> returnMultipleCLubMembersResponse(DomainQueryResponse response) {
        if (response.isSuccess()) {
            List<ClubMember> clubMembers = (List<ClubMember>) response.getResponse();
            List<ClubMemberDto> clubMemberDtos = clubMembers.stream()
                    .map(ClubMemberDto::fromObject)
                    .toList();
            return ResponseEntity.ok(clubMemberDtos);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
