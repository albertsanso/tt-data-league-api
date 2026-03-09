package org.cttelsamicsterrassa.data.api.core.match.find;

public record FindMatchesRequestBodyDto(
        String season,
        String competitionType,
        String competitionCategory,
        String competitionScope,
        String competitionScopeTag,
        String competitionGroup,
        String competitionGender,
        int matchDayNumber,
        String practitionerName
) {
}
