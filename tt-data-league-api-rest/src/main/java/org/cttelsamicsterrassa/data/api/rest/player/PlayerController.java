package org.cttelsamicsterrassa.data.api.rest.player;

import org.albertsanso.commons.query.DomainQueryResponse;
import org.albertsanso.commons.query.QueryBus;
import org.cttelsamicsterrassa.data.api.core.player.application.FindPlayerByNameQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/player")
public class PlayerController {

    @Autowired
    private QueryBus queryBus;

    @GetMapping("/searchByName")
    public DomainQueryResponse findAllPlayersBySimilarName(@RequestParam("username") String name) {

        FindPlayerByNameQuery domainQuery = new FindPlayerByNameQuery(name);
        return queryBus.push(domainQuery);
    }
}
