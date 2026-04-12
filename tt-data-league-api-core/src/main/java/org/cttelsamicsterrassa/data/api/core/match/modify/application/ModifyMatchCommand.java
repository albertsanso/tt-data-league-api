package org.cttelsamicsterrassa.data.api.core.match.modify.application;

import org.albertsanso.commons.command.DomainCommand;
import org.cttelsamicsterrassa.data.core.domain.model.CompetitionInfo;

import java.time.ZonedDateTime;
import java.util.UUID;

public class ModifyMatchCommand extends DomainCommand {

    private final UUID id;
    private final String season;
    private final CompetitionInfo competitionInfo;
    private final int matchDayNumber;
    private final String uniqueRowMatchId;

    public ModifyMatchCommand(UUID id, String season, CompetitionInfo competitionInfo, int matchDayNumber, String uniqueRowMatchId) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.id = id;
        this.season = season;
        this.competitionInfo = competitionInfo;
        this.matchDayNumber = matchDayNumber;
        this.uniqueRowMatchId = uniqueRowMatchId;
    }

    public UUID getId() {
        return id;
    }

    public String getSeason() {
        return season;
    }

    public CompetitionInfo getCompetitionInfo() {
        return competitionInfo;
    }

    public int getMatchDayNumber() {
        return matchDayNumber;
    }

    public String getUniqueRowMatchId() {
        return uniqueRowMatchId;
    }
}

