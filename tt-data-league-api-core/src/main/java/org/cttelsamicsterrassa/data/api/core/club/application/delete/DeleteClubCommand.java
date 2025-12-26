package org.cttelsamicsterrassa.data.api.core.club.application.delete;

import org.albertsanso.commons.command.DomainCommand;

import java.time.ZonedDateTime;
import java.util.UUID;

public class DeleteClubCommand extends DomainCommand {

    private final UUID clubId;

    public DeleteClubCommand(UUID clubId) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.clubId = clubId;
    }

    public UUID getClubId() {
        return clubId;
    }
}
