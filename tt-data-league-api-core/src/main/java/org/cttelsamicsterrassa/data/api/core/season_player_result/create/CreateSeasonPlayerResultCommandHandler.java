package org.cttelsamicsterrassa.data.api.core.season_player_result.create;

import org.albertsanso.commons.command.DomainCommand;
import org.albertsanso.commons.command.DomainCommandHandler;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.cttelsamicsterrassa.data.api.core.season_player_result.find.SeasonPlayerResultFinder;
import org.cttelsamicsterrassa.data.core.domain.model.CompetitionInfo;
import org.cttelsamicsterrassa.data.core.domain.model.MatchInfo;
import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayer;
import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayerResult;
import org.cttelsamicsterrassa.data.core.domain.repository.SeasonPlayerRepository;
import org.cttelsamicsterrassa.data.core.domain.repository.SeasonPlayerResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CreateSeasonPlayerResultCommandHandler extends DomainCommandHandler<CreateSeasonPlayerResultCommand> {

    private final SeasonPlayerResultRepository seasonPlayerResultRepository;

    private final SeasonPlayerRepository seasonPlayerRepository;

    private final SeasonPlayerResultFinder seasonPlayerResultFinder;

    @Autowired
    public CreateSeasonPlayerResultCommandHandler(SeasonPlayerResultRepository seasonPlayerResultRepository, SeasonPlayerRepository seasonPlayerRepository, SeasonPlayerResultFinder seasonPlayerResultFinder) {
        this.seasonPlayerResultRepository = seasonPlayerResultRepository;
        this.seasonPlayerRepository = seasonPlayerRepository;
        this.seasonPlayerResultFinder = seasonPlayerResultFinder;
    }

    @Override
    public DomainCommandResponse handle(CreateSeasonPlayerResultCommand createSeasonPlayerResultCommand) {

        Optional<SeasonPlayer> seasonPlayer = seasonPlayerRepository.findById(createSeasonPlayerResultCommand.getSeasonPlayerId());

        if (seasonPlayer.isPresent()) {
            String uniqueRowId = "%s-%s-%s-%d-%s-%s-%s".formatted(
                    seasonPlayer.get().getYearRange(),
                    createSeasonPlayerResultCommand.getCompetitionInfo().competitionType(),
                    createSeasonPlayerResultCommand.getCompetitionInfo().competitionCategory(),
                    createSeasonPlayerResultCommand.getMatchDayNumber(),
                    createSeasonPlayerResultCommand.getCompetitionInfo().competitionGroup(),
                    seasonPlayer.get().getLicense(),
                    createSeasonPlayerResultCommand.getMatchPlayerLetter()
            );

            SeasonPlayerResult seasonPlayerResult = SeasonPlayerResult.createNew(
                    seasonPlayer.get().getYearRange(),
                    createSeasonPlayerResultCommand.getCompetitionInfo().createCopy(),
                    seasonPlayer.get(),
                    new MatchInfo(
                            createSeasonPlayerResultCommand.getMatchDayNumber(),
                            "",
                            createSeasonPlayerResultCommand.getMatchPlayerLetter(),
                            new int[]{},
                            createSeasonPlayerResultCommand.getMatchGamesWon(),
                            uniqueRowId
                    )
            );


            return seasonPlayerResultFinder.findByUniqueKey(
                            seasonPlayerResult.getSeasonPlayer(),
                            seasonPlayerResult.getCompetitionInfo(),
                            seasonPlayerResult.getMatchInfo().matchDayNumber(),
                            seasonPlayerResult.getMatchInfo().playerLetter()

                    ).map(existingSeasonPlayerResult -> DomainCommandResponse.failResponse("SeasonPlayerResult with the same uniqueRowId already exists"))
                    .orElseGet(() -> {
                        seasonPlayerResultRepository.save(seasonPlayerResult);
                        return DomainCommandResponse.successResponse(seasonPlayerResult);
                    });
        }
        return DomainCommandResponse.failResponse("SeasonPlayer with that ID doesn't exist");
    }
}
