package org.cttelsamicsterrassa.data.api.core.season_player_result.find;

import org.cttelsamicsterrassa.data.core.domain.model.CompetitionInfo;
import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayer;
import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayerResult;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SeasonPlayerResultFinder {

    @SuppressWarnings("unused")
    public Optional<SeasonPlayerResult> findByUniqueKey(SeasonPlayer seasonPlayer, CompetitionInfo competitionInfo, int matchDayNumber, String matchPlayerLetter) {
        // TODO: Implement repository method to find by unique key
        // Currently returning empty as the repository interface doesn't expose this method
        // Once implemented, this should call: seasonPlayerResultRepository.findByUniqueKey(uniqueKey)
        return Optional.empty();
    }
}
