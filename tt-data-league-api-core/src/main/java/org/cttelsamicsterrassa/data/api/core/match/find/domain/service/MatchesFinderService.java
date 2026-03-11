package org.cttelsamicsterrassa.data.api.core.match.find.domain.service;

import org.cttelsamicsterrassa.data.api.core.match.find.domain.model.MatchResultForDashboard;
import org.cttelsamicsterrassa.data.core.domain.model.CompetitionInfo;
import org.cttelsamicsterrassa.data.core.domain.repository.PlayersSingleMatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MatchesFinderService {

    private final PlayersSingleMatchRepository playersSingleMatchRepository;

    @Autowired
    public MatchesFinderService(PlayersSingleMatchRepository playersSingleMatchRepository) {
        this.playersSingleMatchRepository = playersSingleMatchRepository;
    }

    public List<MatchResultForDashboard> findMatchesForDashboard(String season, CompetitionInfo competitionInfo, String practitionerName) {
        return playersSingleMatchRepository.findBySeasonAndCompetitionAndMatchDayNumber(season, competitionInfo, 1, practitionerName)
                .stream()
                .map(MatchResultForDashboard::fromPlayersSingleMatch)
                .toList();
    }
}
