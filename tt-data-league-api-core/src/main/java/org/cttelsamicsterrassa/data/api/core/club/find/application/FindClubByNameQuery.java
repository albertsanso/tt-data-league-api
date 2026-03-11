package org.cttelsamicsterrassa.data.api.core.club.find.application;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindClubByNameQuery extends DomainQuery {

    private final String nameToSearch;

    public FindClubByNameQuery(String nameToSearch) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.nameToSearch = nameToSearch;
    }

    public String getNameToSearch() {
        return nameToSearch;
    }
}
