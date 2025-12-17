package org.cttelsamicsterrassa.data.api.core.player.application;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindPlayerByNameQuery extends DomainQuery {

    private final String name;

    public FindPlayerByNameQuery(String name) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
