package org.cttelsamicsterrassa.data.api.rest.match;

import java.util.UUID;

public record MatchDto(UUID id, String homeTeam, String awayTeam, Integer homeScore, Integer awayScore, String matchDate) {
}
