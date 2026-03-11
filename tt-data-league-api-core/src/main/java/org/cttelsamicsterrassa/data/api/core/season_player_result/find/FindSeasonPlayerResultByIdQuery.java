package org.cttelsamicsterrassa.data.api.core.season_player_result.find;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindSeasonPlayerResultByIdQuery extends DomainQuery {

    private final UUID seasonPlayerResultId;

    public FindSeasonPlayerResultByIdQuery(UUID seasonPlayerResultId) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.seasonPlayerResultId = seasonPlayerResultId;
    }

    public UUID getSeasonPlayerResultId() {
        return seasonPlayerResultId;
    }
}
