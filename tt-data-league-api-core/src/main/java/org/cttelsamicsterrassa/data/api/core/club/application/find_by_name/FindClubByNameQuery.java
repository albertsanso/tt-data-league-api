package org.cttelsamicsterrassa.data.api.core.club.application.find_by_name;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindClubByNameQuery extends DomainQuery {

    private final String nameToSearch;

    public FindClubByNameQuery(ZonedDateTime occurredOn, UUID uuid, String nameToSearch) {
        super(occurredOn, uuid.toString());
        this.nameToSearch = nameToSearch;
    }

    public String getNameToSearch() {
        return nameToSearch;
    }
}
