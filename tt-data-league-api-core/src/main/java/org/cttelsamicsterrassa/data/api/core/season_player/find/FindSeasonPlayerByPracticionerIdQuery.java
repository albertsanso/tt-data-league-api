package org.cttelsamicsterrassa.data.api.core.season_player.find;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindSeasonPlayerByPracticionerIdQuery extends DomainQuery {
    private final UUID practicionerId;

    public FindSeasonPlayerByPracticionerIdQuery(UUID practicionerId) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.practicionerId = practicionerId;
    }

    public UUID getPracticionerId() {
        return practicionerId;
    }
}
