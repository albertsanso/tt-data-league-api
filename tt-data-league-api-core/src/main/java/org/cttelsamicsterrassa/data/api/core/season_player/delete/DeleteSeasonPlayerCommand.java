package org.cttelsamicsterrassa.data.api.core.season_player.delete;

import org.albertsanso.commons.command.DomainCommand;

import java.time.ZonedDateTime;
import java.util.UUID;

public class DeleteSeasonPlayerCommand extends DomainCommand {
    private final UUID seasonPlayerId;

    public DeleteSeasonPlayerCommand(UUID seasonPlayerId) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.seasonPlayerId = seasonPlayerId;
    }

    public UUID getSeasonPlayerId() {
        return seasonPlayerId;
    }
}
