package org.cttelsamicsterrassa.data.api.rest.player;

import org.albertsanso.commons.query.DomainQueryResponse;
import org.albertsanso.commons.query.QueryBus;
import org.cttelsamicsterrassa.data.api.core.player.application.FindPlayerByNameQuery;
import org.cttelsamicsterrassa.data.api.core.player.application.FindPlayerByNamesQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/player")
public class PlayerController {

    @Autowired
    private QueryBus queryBus;

    @GetMapping("/searchByName")
    public ResponseEntity<List<PlayerDto>> findAllPlayersBySimilarName(@RequestParam("username") String name) {

        FindPlayerByNameQuery domainQuery = new FindPlayerByNameQuery(name);
        DomainQueryResponse queryResponse = queryBus.push(domainQuery);

        return queryResponse.isSuccess() ?
                ResponseEntity.ok(PlayerDto.fromObjectList(queryResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("searchByNames")
    public ResponseEntity<List<PlayerDto>> findAllPlayersBySimilarNames(@RequestParam("usernames") List<String> names) {
        FindPlayerByNamesQuery domainQuery = new FindPlayerByNamesQuery(ZonedDateTime.now(), UUID.randomUUID(), names);
        DomainQueryResponse queryResponse = queryBus.push(domainQuery);

        return queryResponse.isSuccess() ?
                ResponseEntity.ok(PlayerDto.fromObjectList(queryResponse.getResponse())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
