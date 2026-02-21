package org.cttelsamicsterrassa.data.api.core.season_player_result.find;

import org.cttelsamicsterrassa.data.core.domain.model.CompetitionInfo;
import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayer;
import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayerResult;
import org.cttelsamicsterrassa.data.core.domain.repository.SeasonPlayerResultRepository;
import org.cttelsamicsterrassa.data.core.domain.service.SeasonPlayerResultUniqueKeyBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SeasonPlayerResultFinder {

    private final SeasonPlayerResultRepository seasonPlayerResultRepository;

    @Autowired
    public SeasonPlayerResultFinder(SeasonPlayerResultRepository seasonPlayerResultRepository) {
        this.seasonPlayerResultRepository = seasonPlayerResultRepository;
    }

    public Optional<SeasonPlayerResult> findByUniqueKey(SeasonPlayer seasonPlayer, CompetitionInfo competitionInfo, int matchDayNumber, String matchPlayerLetter) {

        String uniqueKey = SeasonPlayerResultUniqueKeyBuilder.buildUniqueKey(
                seasonPlayer,
                competitionInfo,
                matchDayNumber,
                matchPlayerLetter
        );

        return seasonPlayerResultRepository.findByUniqueKey(uniqueKey);
    }
}
