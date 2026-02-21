package org.cttelsamicsterrassa.data.api.rest.season_player_result;

public record CompetitionInfoDto(
        String type,
        String category,
        String scope,
        String scopeTag,
        String group,
        String gender
) {
}
