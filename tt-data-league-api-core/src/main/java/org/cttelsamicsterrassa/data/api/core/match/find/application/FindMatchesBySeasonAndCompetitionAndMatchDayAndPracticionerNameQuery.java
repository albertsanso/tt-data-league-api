package org.cttelsamicsterrassa.data.api.core.match.find.application;

import org.albertsanso.commons.query.DomainQuery;
import org.cttelsamicsterrassa.data.core.domain.model.CompetitionInfo;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindMatchesBySeasonAndCompetitionAndMatchDayAndPracticionerNameQuery extends DomainQuery {

    private final String season;

    private final CompetitionInfo competitionInfo;

    private final Integer matchDayNumber;

    private final String practitionerName;

    public FindMatchesBySeasonAndCompetitionAndMatchDayAndPracticionerNameQuery(String season, CompetitionInfo competitionInfo, Integer matchDayNumber, String practitionerName) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.season = season;
        this.competitionInfo = competitionInfo;
        this.matchDayNumber = matchDayNumber;
        this.practitionerName = practitionerName;
    }

    public Integer getMatchDayNumber() {
        return matchDayNumber;
    }

    public CompetitionInfo getCompetitionInfo() {
        return competitionInfo;
    }

    public String getSeason() {
        return season;
    }

    public String getPractitionerName() {
        return practitionerName;
    }
}
