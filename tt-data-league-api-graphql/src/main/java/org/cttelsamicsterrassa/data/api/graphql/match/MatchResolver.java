package org.cttelsamicsterrassa.data.api.graphql.match;

import org.albertsanso.commons.query.QueryBus;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.api.core.match.find.application.FindAllMatchesQuery;
import org.cttelsamicsterrassa.data.api.core.match.find.application.FindMatchByIdQuery;
import org.cttelsamicsterrassa.data.api.core.match.find.application.FindMatchesBySeasonAndCompetitionAndMatchDayAndPracticionerNameQuery;
import org.cttelsamicsterrassa.data.api.core.match.find.application.FindMatchesByTeamNameQuery;
import org.cttelsamicsterrassa.data.core.domain.model.CompetitionInfo;
import org.cttelsamicsterrassa.data.core.domain.model.PlayersSingleMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for resolving Match-related GraphQL queries.
 * This service coordinates with the QueryBus to fetch match data from the domain.
 */
@Controller
public class MatchResolver {

    @Autowired
    private QueryBus queryBus;

    @QueryMapping
    public MatchGraphQLDto findMatchById(String id) {
        try {
            FindMatchByIdQuery query = new FindMatchByIdQuery(UUID.fromString(id));
            DomainQueryResponse response = queryBus.push(query);

            if (response.isSuccess() && response.getResponse() != null) {
                PlayersSingleMatch match = (PlayersSingleMatch) response.getResponse();
                return MatchGraphQLDto.fromDomain(match);
            }
        } catch (Exception e) {
            System.err.println("Error finding match by ID: " + e.getMessage());
        }
        return null;
    }

    @QueryMapping
    public List<MatchGraphQLDto> findMatchesByTeam(@Argument("teamName") String teamName) {
        try {
            FindMatchesByTeamNameQuery query = new FindMatchesByTeamNameQuery(teamName);
            DomainQueryResponse response = queryBus.push(query);

            if (response.isSuccess() && response.getResponse() != null) {
                Object payload = response.getResponse();
                if (payload instanceof List<?> matches) {
                    return matches.stream()
                        .map(m -> MatchGraphQLDto.fromDomain((PlayersSingleMatch) m))
                        .collect(Collectors.toList());
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding matches by team name: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    @QueryMapping
    public List<MatchGraphQLDto> listAllMatches() {
        try {
            FindAllMatchesQuery query = new FindAllMatchesQuery();
            DomainQueryResponse response = queryBus.push(query);

            if (response.isSuccess() && response.getResponse() != null) {
                Object payload = response.getResponse();
                if (payload instanceof List<?> matches) {
                    return matches.stream()
                        .map(m -> MatchGraphQLDto.fromDomain((PlayersSingleMatch) m))
                        .collect(Collectors.toList());
                }
            }
        } catch (Exception e) {
            System.err.println("Error listing all matches: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    @QueryMapping
    public List<MatchGraphQLDto> findMatchesBySeasonAndCompetitionAndMatchDayAndPracticionerName(@Argument("season") String season, @Argument("competitionInfo") CompetitionInfo competitionInfo, @Argument("matchDayNumber") Integer matchDayNumber, @Argument("practitionerName") String practitionerName) {
        try {
            FindMatchesBySeasonAndCompetitionAndMatchDayAndPracticionerNameQuery query =
                    new FindMatchesBySeasonAndCompetitionAndMatchDayAndPracticionerNameQuery(
                            season,
                            competitionInfo,
                            matchDayNumber,
                            practitionerName
                    );
            DomainQueryResponse response = queryBus.push(query);

            if (response.isSuccess() && response.getResponse() != null) {
                Object payload = response.getResponse();
                if (payload instanceof List<?> matches) {
                    return matches.stream()
                            .map(m -> MatchGraphQLDto.fromDomain((PlayersSingleMatch) m))
                            .collect(Collectors.toList());
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding matches by season, competition, match day and practitioner name: " + e.getMessage());
        }
        return new ArrayList<>();
    }
}


