package org.cttelsamicsterrassa.data.api.core.club.application.find;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindClubBySimilarNameQuery extends DomainQuery {

    private final String nameToSearch;

    public FindClubBySimilarNameQuery(String nameToSearch) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.nameToSearch = nameToSearch;
    }

    public String getNameToSearch() {
        return nameToSearch;
    }
}
