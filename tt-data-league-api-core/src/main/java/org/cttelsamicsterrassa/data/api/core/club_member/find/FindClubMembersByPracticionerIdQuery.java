package org.cttelsamicsterrassa.data.api.core.club_member.find;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindClubMembersByPracticionerIdQuery extends DomainQuery {

    private final UUID practitionerId;

    public FindClubMembersByPracticionerIdQuery(UUID practitionerId) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.practitionerId = practitionerId;
    }

    public UUID getPractitionerId() {
        return practitionerId;
    }
}
