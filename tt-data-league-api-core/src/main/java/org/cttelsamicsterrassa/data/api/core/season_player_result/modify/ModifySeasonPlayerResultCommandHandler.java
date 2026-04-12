package org.cttelsamicsterrassa.data.api.core.season_player_result.modify;

import org.albertsanso.commons.command.DomainCommandHandler;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.cttelsamicsterrassa.data.core.domain.model.MatchInfo;
import org.cttelsamicsterrassa.data.core.domain.repository.SeasonPlayerRepository;
import org.cttelsamicsterrassa.data.core.domain.repository.SeasonPlayerResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModifySeasonPlayerResultCommandHandler extends DomainCommandHandler<ModifySeasonPlayerResultCommand> {

    private final SeasonPlayerResultRepository seasonPlayerResultRepository;
    private final SeasonPlayerRepository seasonPlayerRepository;

    @Autowired
    public ModifySeasonPlayerResultCommandHandler(SeasonPlayerResultRepository seasonPlayerResultRepository,
                                                  SeasonPlayerRepository seasonPlayerRepository) {
        this.seasonPlayerResultRepository = seasonPlayerResultRepository;
        this.seasonPlayerRepository = seasonPlayerRepository;
    }

    @Override
    public DomainCommandResponse handle(ModifySeasonPlayerResultCommand command) {
        return seasonPlayerResultRepository.findById(command.getId())
                .map(existing -> seasonPlayerRepository.findById(command.getSeasonPlayerId())
                        .map(seasonPlayer -> {
                            var updated = org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayerResult.createExisting(
                                    existing.getId(),
                                    existing.getSeason(),
                                    command.getCompetitionInfo(),
                                    seasonPlayer,
                                    new MatchInfo(
                                    command.getMatchDayNumber(),
                                    command.getMatchDay(),
                                    command.getMatchPlayerLetter(),
                                    parseGamePoints(command.getMatchGamePoints()),
                                    command.getMatchGamesWon(),
                                    existing.getMatchInfo().playersPairing()
                                    ),
                                    command.getMatchPlayerRole()
                            );
                            seasonPlayerResultRepository.save(updated);
                            return DomainCommandResponse.successResponse(updated);
                        })
                        .orElseGet(() -> DomainCommandResponse.failResponse("SeasonPlayer not found")))
                .orElseGet(() -> DomainCommandResponse.failResponse("SeasonPlayerResult not found"));
    }

    private int[] parseGamePoints(String matchGamePoints) {
        if (matchGamePoints == null || matchGamePoints.isBlank()) {
            return new int[]{};
        }

        String sanitized = matchGamePoints.replace("[", "").replace("]", "").trim();
        if (sanitized.isEmpty()) {
            return new int[]{};
        }

        String[] values = sanitized.split(",");
        int[] parsed = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            parsed[i] = Integer.parseInt(values[i].trim());
        }
        return parsed;
    }
}


