package org.cttelsamicsterrassa.data.api.core.club.application.create;

import org.albertsanso.commons.command.DomainCommand;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class CreateClubCommand extends DomainCommand {

    private final UUID id;

    private final String name;

    private final List<String> yearRanges;

    public CreateClubCommand(UUID clubId, String name, List<String> yearRanges) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.id = clubId;
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
