package org.cttelsamicsterrassa.data.api.core.club_member.create;

import org.albertsanso.commons.command.DomainCommand;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class CreateClubMemberCommand extends DomainCommand {

    private final UUID clubId;

    private final UUID practicionerId;

    private final List<String> yearRanges;

    public CreateClubMemberCommand(UUID clubId, UUID practicionerId, List<String> yearRanges) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.clubId = clubId;
        this.practicionerId = practicionerId;
        this.yearRanges = yearRanges;
    }

    public UUID getClubId() {
        return clubId;
    }

    public UUID getPracticionerId() {
        return practicionerId;
    }

    public List<String> getYearRanges() {
        return yearRanges;
    }
}
