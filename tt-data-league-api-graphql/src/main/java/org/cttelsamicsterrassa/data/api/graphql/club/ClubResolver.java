package org.cttelsamicsterrassa.data.api.graphql.club;

import org.albertsanso.commons.query.QueryBus;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.api.core.club.find.application.FindAllClubsQuery;
import org.cttelsamicsterrassa.data.api.core.club.find.application.FindClubByIdQuery;
import org.cttelsamicsterrassa.data.api.core.club.find.application.FindClubByNameQuery;
import org.cttelsamicsterrassa.data.api.core.club_member.find.application.FindClubMembersByClubIdQuery;
import org.cttelsamicsterrassa.data.core.domain.model.Club;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for resolving Club-related GraphQL queries.
 * This service coordinates with the QueryBus to fetch club data from the domain.
 */
@Controller
public class ClubResolver {

    @Autowired
    private QueryBus queryBus;

    @QueryMapping
    public ClubGraphQLDto findClubById(String id) {
        try {
            FindClubByIdQuery query = new FindClubByIdQuery(UUID.fromString(id));
            DomainQueryResponse response = queryBus.push(query);

            if (response.isSuccess() && response.getResponse() != null) {
                Club club = (Club) response.getResponse();
                return ClubGraphQLDto.fromDomain(club);
            }
        } catch (Exception e) {
            System.err.println("Error finding club by ID: " + e.getMessage());
        }
        return null;
    }

    @QueryMapping
    public List<ClubGraphQLDto> findClubByName(@Argument("name") String name) {
        try {
            FindClubByNameQuery query = new FindClubByNameQuery(name);
            DomainQueryResponse response = queryBus.push(query);

            if (response.isSuccess() && response.getResponse() != null) {
                Object payload = response.getResponse();
                if (payload instanceof List<?> clubs) {
                    return clubs.stream()
                        .map(c -> ClubGraphQLDto.fromDomain((Club) c))
                        .collect(Collectors.toList());
                } else {
                    Club club = (Club) payload;
                    return Collections.singletonList(ClubGraphQLDto.fromDomain(club));
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding club by name: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    @QueryMapping
    public List<ClubGraphQLDto> listAllClubs() {
        try {
            FindAllClubsQuery query = new FindAllClubsQuery();
            DomainQueryResponse response = queryBus.push(query);

            if (response.isSuccess() && response.getResponse() != null) {
                Object payload = response.getResponse();
                if (payload instanceof List<?> clubs) {
                    return clubs.stream()
                        .map(c -> ClubGraphQLDto.fromDomain((Club) c))
                        .collect(Collectors.toList());
                }
            }
        } catch (Exception e) {
            System.err.println("Error listing all clubs: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<?> getClubMembers(ClubGraphQLDto club) {
        try {
            FindClubMembersByClubIdQuery query = new FindClubMembersByClubIdQuery(club.id());
            DomainQueryResponse response = queryBus.push(query);

            if (response.isSuccess() && response.getResponse() != null) {
                Object payload = response.getResponse();
                if (payload instanceof List<?> members) {
                    return new ArrayList<>(members);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching club members: " + e.getMessage());
        }
        return new ArrayList<>();
    }
}


