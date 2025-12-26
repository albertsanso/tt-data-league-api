package org.cttelsamicsterrassa.data.api.core.player.application;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class FindPlayerByNamesQuery extends DomainQuery {

    private final List<String> stringToSearch;

    public FindPlayerByNamesQuery(ZonedDateTime occurredOn, UUID uuid, List<String> stringToSearch) {
        super(occurredOn, uuid.toString());
        this.stringToSearch = stringToSearch;
    }

    public List<String> getStringToSearch() {
        return stringToSearch;
    }
}
