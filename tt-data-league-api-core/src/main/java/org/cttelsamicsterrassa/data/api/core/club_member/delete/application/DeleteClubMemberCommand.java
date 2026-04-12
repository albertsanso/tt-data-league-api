package org.cttelsamicsterrassa.data.api.core.club_member.delete.application;

import org.albertsanso.commons.command.DomainCommand;

import java.time.ZonedDateTime;
import java.util.UUID;

public class DeleteClubMemberCommand extends DomainCommand {

    private final UUID clubMemberId;

    public DeleteClubMemberCommand(UUID clubMemberId) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.clubMemberId = clubMemberId;
    }

    public UUID getClubMemberId() {
        return clubMemberId;
    }
}

