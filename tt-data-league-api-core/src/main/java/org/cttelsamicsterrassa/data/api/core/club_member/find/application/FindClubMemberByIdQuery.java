package org.cttelsamicsterrassa.data.api.core.club_member.find.application;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindClubMemberByIdQuery extends DomainQuery {

    private final UUID clubMemberId;

    public FindClubMemberByIdQuery(UUID clubMemberId) {
        super(ZonedDateTime.now(), java.util.UUID.randomUUID().toString());
        this.clubMemberId = clubMemberId;
    }

    public UUID getClubMemberId() {
        return clubMemberId;
    }
}
