package org.cttelsamicsterrassa.data.api.core.season_player_result.find;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindAllSeasonPlayerResultsQuery extends DomainQuery {
    public FindAllSeasonPlayerResultsQuery() {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
    }
}
