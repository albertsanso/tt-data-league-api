package org.cttelsamicsterrassa.data.api.core.club.find.application;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindAllClubsQuery extends DomainQuery {

    public FindAllClubsQuery() {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
    }
}
