package org.cttelsamicsterrassa.data.api.rest.shared;

import org.cttelsamicsterrassa.data.core.domain.model.CompetitionInfo;

public record CompetitionInfoDto(
        String type,
        String category,
        String scope,
        String scopeTag,
        String group,
        String gender
) {
    public static CompetitionInfoDto fromDomain(CompetitionInfo competitionInfo) {
        return new CompetitionInfoDto(
                competitionInfo.competitionType(),
                competitionInfo.competitionCategory(),
                competitionInfo.competitionScope(),
                competitionInfo.competitionScopeTag(),
                competitionInfo.competitionGroup(),
                competitionInfo.competitionGender()
        );
    }

    public static CompetitionInfoDto fromDomain(String competitionType, String competitionCategory, String competitionScope, String competitionScopeTag, String competitionGroup, String competitionGender) {
        return new CompetitionInfoDto(
                competitionType,
                competitionCategory,
                competitionScope,
                competitionScopeTag,
                competitionGroup,
                competitionGender
        );
    }
}
