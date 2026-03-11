package org.cttelsamicsterrassa.data.api.graphql.season_player;

import org.albertsanso.commons.query.QueryBus;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.api.core.club_member.find.application.FindClubMemberByIdQuery;
import org.cttelsamicsterrassa.data.api.core.season_player.find.FindAllSeasonPlayersQuery;
import org.cttelsamicsterrassa.data.api.core.season_player.find.FindSeasonPlayerByIdQuery;
import org.cttelsamicsterrassa.data.core.domain.model.SeasonPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for resolving SeasonPlayer-related GraphQL queries.
 * This service coordinates with the QueryBus to fetch season player data from the domain.
 */
@Service
public class SeasonPlayerResolver {

    @Autowired
    private QueryBus queryBus;

    @QueryMapping
    public SeasonPlayerGraphQLDto findSeasonPlayerById(String id) {
        try {
            FindSeasonPlayerByIdQuery query = new FindSeasonPlayerByIdQuery(UUID.fromString(id));
            DomainQueryResponse response = queryBus.push(query);

            if (response.isSuccess() && response.getResponse() != null) {
                SeasonPlayer seasonPlayer = (SeasonPlayer) response.getResponse();
                return SeasonPlayerGraphQLDto.fromDomain(seasonPlayer);
            }
        } catch (Exception e) {
            System.err.println("Error finding season player by ID: " + e.getMessage());
        }
        return null;
    }

    @QueryMapping
    public List<SeasonPlayerGraphQLDto> findSeasonPlayerByYearRange(String yearRange) {
        try {
            FindAllSeasonPlayersQuery query = new FindAllSeasonPlayersQuery();
            DomainQueryResponse response = queryBus.push(query);

            if (response.isSuccess() && response.getResponse() != null) {
                Object payload = response.getResponse();
                if (payload instanceof List<?> seasonPlayers) {
                    return seasonPlayers.stream()
                        .filter(sp -> sp instanceof SeasonPlayer && ((SeasonPlayer) sp).getYearRange().equals(yearRange))
                        .map(sp -> SeasonPlayerGraphQLDto.fromDomain((SeasonPlayer) sp))
                        .collect(Collectors.toList());
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding season players by year range: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    @QueryMapping
    public List<SeasonPlayerGraphQLDto> listAllSeasonPlayers() {
        try {
            FindAllSeasonPlayersQuery query = new FindAllSeasonPlayersQuery();
            DomainQueryResponse response = queryBus.push(query);

            if (response.isSuccess() && response.getResponse() != null) {
                Object payload = response.getResponse();
                if (payload instanceof List<?> seasonPlayers) {
                    return seasonPlayers.stream()
                        .map(sp -> SeasonPlayerGraphQLDto.fromDomain((SeasonPlayer) sp))
                        .collect(Collectors.toList());
                }
            }
        } catch (Exception e) {
            System.err.println("Error listing all season players: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public Object getClubMember(SeasonPlayerGraphQLDto seasonPlayer) {
        try {
            if (seasonPlayer != null && seasonPlayer.clubMemberId() != null) {
                FindClubMemberByIdQuery query = new FindClubMemberByIdQuery(seasonPlayer.clubMemberId());
                DomainQueryResponse response = queryBus.push(query);

                if (response.isSuccess() && response.getResponse() != null) {
                    return response.getResponse();
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching club member for season player: " + e.getMessage());
        }
        return null;
    }

    public LicenseGraphQLDto getLicense(SeasonPlayerGraphQLDto seasonPlayer) {
        return new LicenseGraphQLDto(seasonPlayer.licenseId(), seasonPlayer.licenseTag());
    }
}


