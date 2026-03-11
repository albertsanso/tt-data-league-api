package org.cttelsamicsterrassa.data.api.core.practicioner.find;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindAllPracticionersQuery extends DomainQuery {
    public FindAllPracticionersQuery() {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
    }
}
