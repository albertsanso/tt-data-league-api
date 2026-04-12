package org.cttelsamicsterrassa.data.api.core.season_player_result.modify;

import org.albertsanso.commons.command.DomainCommand;
import org.cttelsamicsterrassa.data.core.domain.model.CompetitionInfo;
import org.cttelsamicsterrassa.data.core.domain.model.TeamRole;

import java.time.ZonedDateTime;
import java.util.UUID;

public class ModifySeasonPlayerResultCommand extends DomainCommand {

    private final UUID id;
    private final CompetitionInfo competitionInfo;
    private final UUID seasonPlayerId;
    private final String matchDay;
    private final int matchDayNumber;
    private final String matchGamePoints;
    private final int matchGamesWon;
    private final String matchPlayerLetter;
    private final TeamRole matchPlayerRole;

    public ModifySeasonPlayerResultCommand(UUID id,
                                           CompetitionInfo competitionInfo,
                                           UUID seasonPlayerId,
                                           String matchDay,
                                           int matchDayNumber,
                                           String matchGamePoints,
                                           int matchGamesWon,
                                           String matchPlayerLetter,
                                           TeamRole matchPlayerRole) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.id = id;
        this.competitionInfo = competitionInfo;
        this.seasonPlayerId = seasonPlayerId;
        this.matchDay = matchDay;
        this.matchDayNumber = matchDayNumber;
        this.matchGamePoints = matchGamePoints;
        this.matchGamesWon = matchGamesWon;
        this.matchPlayerLetter = matchPlayerLetter;
        this.matchPlayerRole = matchPlayerRole;
    }

    public UUID getId() {
        return id;
    }

    public CompetitionInfo getCompetitionInfo() {
        return competitionInfo;
    }

    public UUID getSeasonPlayerId() {
        return seasonPlayerId;
    }

    public String getMatchDay() {
        return matchDay;
    }

    public int getMatchDayNumber() {
        return matchDayNumber;
    }

    public String getMatchGamePoints() {
        return matchGamePoints;
    }

    public int getMatchGamesWon() {
        return matchGamesWon;
    }

    public String getMatchPlayerLetter() {
        return matchPlayerLetter;
    }

    public TeamRole getMatchPlayerRole() {
        return matchPlayerRole;
    }
}

