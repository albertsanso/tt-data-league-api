package org.cttelsamicsterrassa.data.api.core.practicioner.find;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindPracticionerByNameQuery extends DomainQuery {

    private final String name;

    public FindPracticionerByNameQuery(String name) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
