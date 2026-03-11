package org.cttelsamicsterrassa.data.api.core.match.find.application;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindMatchByIdQuery extends DomainQuery {
    private final UUID matchId;

    public FindMatchByIdQuery(UUID matchId) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.matchId = matchId;
    }

    public UUID getMatchId() {
        return matchId;
    }
}
