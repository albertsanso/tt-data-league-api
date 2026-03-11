package org.cttelsamicsterrassa.data.api.core.club_member.find.application;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindAllClubMembersQuery extends DomainQuery {

    public FindAllClubMembersQuery() {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
    }
}
