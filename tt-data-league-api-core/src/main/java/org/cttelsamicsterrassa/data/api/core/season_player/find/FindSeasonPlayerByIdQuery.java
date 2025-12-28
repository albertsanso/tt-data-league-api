package org.cttelsamicsterrassa.data.api.core.season_player.find;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindSeasonPlayerByIdQuery extends DomainQuery {

    private final UUID seasonPlayerId;

    public FindSeasonPlayerByIdQuery(UUID seasonPlayerId) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.seasonPlayerId = seasonPlayerId;
    }

    public UUID getSeasonPlayerId() {
        return seasonPlayerId;
    }
}
