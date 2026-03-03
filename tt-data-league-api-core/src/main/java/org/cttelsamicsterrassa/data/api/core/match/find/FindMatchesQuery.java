package org.cttelsamicsterrassa.data.api.core.match.find;

import org.albertsanso.commons.query.DomainQuery;
import org.cttelsamicsterrassa.data.core.domain.model.CompetitionInfo;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindMatchesQuery extends DomainQuery {

    private final String season;

    private final CompetitionInfo competitionInfo;

    private final int matchDayNumber;

    public FindMatchesQuery(String season, CompetitionInfo competitionInfo, int matchDayNumber) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.season = season;
        this.competitionInfo = competitionInfo;
        this.matchDayNumber = matchDayNumber;
    }

    public int getMatchDayNumber() {
        return matchDayNumber;
    }

    public CompetitionInfo getCompetitionInfo() {
        return competitionInfo;
    }

    public String getSeason() {
        return season;
    }
}
