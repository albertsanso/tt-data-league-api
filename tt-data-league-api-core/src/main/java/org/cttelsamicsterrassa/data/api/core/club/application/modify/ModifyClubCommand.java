package org.cttelsamicsterrassa.data.api.core.club.application.modify;

import org.albertsanso.commons.command.DomainCommand;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class ModifyClubCommand extends DomainCommand {

    private final UUID id;

    private final String name;

    private final List<String> yearRanges;

    protected ModifyClubCommand(UUID id, String name, List<String> yearRanges) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.id = id;
        this.name = name;
        this.yearRanges = yearRanges;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getYearRanges() {
        return yearRanges;
    }
}
