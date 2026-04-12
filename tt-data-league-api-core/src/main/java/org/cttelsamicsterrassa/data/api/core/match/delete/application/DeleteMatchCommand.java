package org.cttelsamicsterrassa.data.api.core.match.delete.application;

import org.albertsanso.commons.command.DomainCommand;

import java.time.ZonedDateTime;
import java.util.UUID;

public class DeleteMatchCommand extends DomainCommand {

    private final UUID matchId;

    public DeleteMatchCommand(UUID matchId) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.matchId = matchId;
    }

    public UUID getMatchId() {
        return matchId;
    }
}

