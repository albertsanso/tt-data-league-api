package org.cttelsamicsterrassa.data.api.core.club_member.find.application;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindClubMemberByPracticionerIdAndClubIdQuery extends DomainQuery {
    private final UUID practitionerId;
    private final UUID clubId;

    public FindClubMemberByPracticionerIdAndClubIdQuery(UUID practitionerId, UUID clubId) {
        super(ZonedDateTime.now(), java.util.UUID.randomUUID().toString());
        this.practitionerId = practitionerId;
        this.clubId = clubId;
    }

    public UUID getPractitionerId() {
        return practitionerId;
    }

    public UUID getClubId() {
        return clubId;
    }
}
