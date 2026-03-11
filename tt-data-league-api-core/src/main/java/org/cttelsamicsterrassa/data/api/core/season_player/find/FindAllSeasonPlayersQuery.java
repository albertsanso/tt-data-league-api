package org.cttelsamicsterrassa.data.api.core.season_player.find;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindAllSeasonPlayersQuery extends DomainQuery {
    public FindAllSeasonPlayersQuery() {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
    }
}
