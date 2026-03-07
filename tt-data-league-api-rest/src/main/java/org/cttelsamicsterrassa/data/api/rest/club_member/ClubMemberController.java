package org.cttelsamicsterrassa.data.api.rest.club_member;

import org.albertsanso.commons.command.CommandBus;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.albertsanso.commons.query.QueryBus;
import org.cttelsamicsterrassa.data.api.core.club_member.create.CreateClubMemberCommand;
import org.cttelsamicsterrassa.data.api.core.club_member.find.FindClubMembersByClubIdQuery;
import org.cttelsamicsterrassa.data.api.core.club_member.find.FindClubMembersByPracticionerIdQuery;
import org.cttelsamicsterrassa.data.core.domain.model.ClubMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.UUID;

@ClubMemberOpenAPIv1Controller
public class ClubMemberController {

    @Autowired
    private QueryBus queryBus;

    @Autowired
    private CommandBus commandBus;

    @PostMapping
    @Operation(summary = "Create club member", description = "Create a new club member from ClubMemberDto")
    public ResponseEntity<ClubMemberDto> createClubMember(@RequestBody ClubMemberDto clubMemberDto) {

        CreateClubMemberCommand createClubMemberCommand = new CreateClubMemberCommand(
                clubMemberDto.clubId(),
                clubMemberDto.practicionerId(),
                clubMemberDto.yearRanges()
        );
        DomainCommandResponse domainCommandResponse = commandBus.push(createClubMemberCommand);
        return domainCommandResponse.isSuccess() ?
                ResponseEntity.ok(ClubMemberDto.fromDomain((ClubMember) domainCommandResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/find_by_club_id/{clubId}")
    @Operation(summary = "Find club members by club id", description = "Returns list of club members for a club")
    public ResponseEntity<List<ClubMemberDto>> getClubMembersByClubId(@PathVariable("clubId") UUID clubId) {
        FindClubMembersByClubIdQuery query = new FindClubMembersByClubIdQuery(clubId);
        return returnMultipleClubMembersResponse(queryBus.push(query));
    }

    @GetMapping("/enriched/find_by_club_id/{clubId}")
    @Operation(summary = "Find enriched club members by club id", description = "Returns enriched members with practicioner and club details")
    public ResponseEntity<List<EnrichedClubMemberDto>> getEnrichedClubMembersByClubId(@PathVariable("clubId") UUID clubId) {
        FindClubMembersByClubIdQuery query = new FindClubMembersByClubIdQuery(clubId);
        return returnMultipleEnrichedClubMembersResponse(queryBus.push(query));
    }

    @GetMapping("/find_by_practicioner_id/{practicionerId}")
    @Operation(summary = "Find club members by practicioner id", description = "Returns list of club members for the practicioner")
    public ResponseEntity<List<ClubMemberDto>> getClubMembersByPracticionerId(@PathVariable("practicionerId") UUID practicionerId) {
        FindClubMembersByPracticionerIdQuery query = new FindClubMembersByPracticionerIdQuery(practicionerId);
        return returnMultipleClubMembersResponse(queryBus.push(query));
    }

    private ResponseEntity<List<ClubMemberDto>> returnMultipleClubMembersResponse(DomainQueryResponse response) {
        if (response.isSuccess()) {
            List<ClubMember> clubMembers = (List<ClubMember>) response.getResponse();
            List<ClubMemberDto> clubMemberDtos = clubMembers.stream()
                    .map(ClubMemberDto::fromDomain)
                    .toList();
            return ResponseEntity.ok(clubMemberDtos);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<List<EnrichedClubMemberDto>> returnMultipleEnrichedClubMembersResponse(DomainQueryResponse response) {
        if (response.isSuccess()) {
            List<ClubMember> clubMembers = (List<ClubMember>) response.getResponse();
            List<EnrichedClubMemberDto> clubMemberDtos = clubMembers.stream()
                    .map(EnrichedClubMemberDto::fromDomain)
                    .toList();
            return ResponseEntity.ok(clubMemberDtos);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
