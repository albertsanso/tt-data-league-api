package org.cttelsamicsterrassa.data.api.rest.match;

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
