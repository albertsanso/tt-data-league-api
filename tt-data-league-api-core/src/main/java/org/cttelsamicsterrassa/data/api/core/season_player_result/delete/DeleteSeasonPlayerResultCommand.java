package org.cttelsamicsterrassa.data.api.core.season_player_result.delete;

import org.albertsanso.commons.command.DomainCommand;

import java.time.ZonedDateTime;
import java.util.UUID;

public class DeleteSeasonPlayerResultCommand extends DomainCommand {

    private final UUID seasonPlayerResultId;

    public DeleteSeasonPlayerResultCommand(UUID seasonPlayerResultId) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.seasonPlayerResultId = seasonPlayerResultId;
    }

    public UUID getSeasonPlayerResultId() {
        return seasonPlayerResultId;
    }
}

