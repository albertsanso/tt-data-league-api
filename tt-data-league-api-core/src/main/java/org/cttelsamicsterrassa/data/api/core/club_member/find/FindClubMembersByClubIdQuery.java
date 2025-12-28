package org.cttelsamicsterrassa.data.api.core.club_member.find;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindClubMembersByClubIdQuery extends DomainQuery {

    private final UUID clubId;

    public FindClubMembersByClubIdQuery(UUID clubId) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.clubId = clubId;
    }

    public UUID getClubId() {
        return clubId;
    }
}
