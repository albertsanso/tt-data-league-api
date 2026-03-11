package org.cttelsamicsterrassa.data.api.core.match.find.application;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Query for finding matches by team name.
 */
public class FindMatchesByTeamNameQuery extends DomainQuery {

    private final String teamName;

    public FindMatchesByTeamNameQuery(String teamName) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }
}

