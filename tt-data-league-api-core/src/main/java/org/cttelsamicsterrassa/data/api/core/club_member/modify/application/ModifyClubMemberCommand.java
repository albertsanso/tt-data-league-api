package org.cttelsamicsterrassa.data.api.core.club_member.modify.application;

import org.albertsanso.commons.command.DomainCommand;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class ModifyClubMemberCommand extends DomainCommand {

    private final UUID id;
    private final UUID clubId;
    private final UUID practicionerId;
    private final List<String> yearRanges;

    public ModifyClubMemberCommand(UUID id, UUID clubId, UUID practicionerId, List<String> yearRanges) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.id = id;
        this.clubId = clubId;
        this.practicionerId = practicionerId;
        this.yearRanges = yearRanges;
    }

    public UUID getId() {
        return id;
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

