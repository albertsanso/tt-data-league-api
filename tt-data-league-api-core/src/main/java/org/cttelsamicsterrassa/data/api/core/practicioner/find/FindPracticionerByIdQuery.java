package org.cttelsamicsterrassa.data.api.core.practicioner.find;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindPracticionerByIdQuery extends DomainQuery {

    private final UUID practicionerId;

    public FindPracticionerByIdQuery(UUID practicionerId) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.practicionerId = practicionerId;
    }

    public UUID getPracticionerId() {
        return practicionerId;
    }
}
