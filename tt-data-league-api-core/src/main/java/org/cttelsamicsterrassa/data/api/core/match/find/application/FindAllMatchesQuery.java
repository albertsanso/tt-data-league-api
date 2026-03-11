package org.cttelsamicsterrassa.data.api.core.match.find.application;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindAllMatchesQuery extends DomainQuery {
    public FindAllMatchesQuery() {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
    }
}
