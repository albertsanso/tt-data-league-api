package org.cttelsamicsterrassa.data.api.graphql.club_member;

import org.albertsanso.commons.query.QueryBus;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.api.core.club.find.application.FindClubByIdQuery;
import org.cttelsamicsterrassa.data.api.core.club_member.find.application.FindAllClubMembersQuery;
import org.cttelsamicsterrassa.data.api.core.club_member.find.application.FindClubMemberByIdQuery;
import org.cttelsamicsterrassa.data.api.core.practicioner.find.FindPracticionerByIdQuery;
import org.cttelsamicsterrassa.data.api.core.season_player.find.FindSeasonPlayerByPracticionerIdQuery;
import org.cttelsamicsterrassa.data.core.domain.model.ClubMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for resolving ClubMember-related GraphQL queries.
 * This service coordinates with the QueryBus to fetch club member data from the domain.
 */
@Service
public class ClubMemberResolver {

    @Autowired
    private QueryBus queryBus;

    @QueryMapping
    public ClubMemberGraphQLDto findClubMemberById(String id) {
        try {
            FindClubMemberByIdQuery query = new FindClubMemberByIdQuery(UUID.fromString(id));
            DomainQueryResponse response = queryBus.push(query);

            if (response.isSuccess() && response.getResponse() != null) {
                ClubMember clubMember = (ClubMember) response.getResponse();
                return ClubMemberGraphQLDto.fromDomain(clubMember);
            }
        } catch (Exception e) {
            System.err.println("Error finding club member by ID: " + e.getMessage());
        }
        return null;
    }

    @QueryMapping
    public List<ClubMemberGraphQLDto> listAllClubMembers() {
        try {
            FindAllClubMembersQuery query = new FindAllClubMembersQuery();
            DomainQueryResponse response = queryBus.push(query);

            if (response.isSuccess() && response.getResponse() != null) {
                Object payload = response.getResponse();
                if (payload instanceof List<?> clubMembers) {
                    return clubMembers.stream()
                        .map(cm -> ClubMemberGraphQLDto.fromDomain((ClubMember) cm))
                        .collect(Collectors.toList());
                }
            }
        } catch (Exception e) {
            System.err.println("Error listing all club members: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public Object getClub(ClubMemberGraphQLDto clubMember) {
        try {
            if (clubMember != null && clubMember.clubId() != null) {
                FindClubByIdQuery query = new FindClubByIdQuery(clubMember.clubId());
                DomainQueryResponse response = queryBus.push(query);

                if (response.isSuccess() && response.getResponse() != null) {
                    return response.getResponse();
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching club for club member: " + e.getMessage());
        }
        return null;
    }

    public Object getPracticioner(ClubMemberGraphQLDto clubMember) {
        try {
            if (clubMember != null && clubMember.practicionerId() != null) {
                FindPracticionerByIdQuery query = new FindPracticionerByIdQuery(clubMember.practicionerId());
                DomainQueryResponse response = queryBus.push(query);

                if (response.isSuccess() && response.getResponse() != null) {
                    return response.getResponse();
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching practicioner for club member: " + e.getMessage());
        }
        return null;
    }

    public List<?> getSeasonPlayers(ClubMemberGraphQLDto clubMember) {
        try {
            if (clubMember != null && clubMember.practicionerId() != null) {
                FindSeasonPlayerByPracticionerIdQuery query = new FindSeasonPlayerByPracticionerIdQuery(clubMember.practicionerId());
                DomainQueryResponse response = queryBus.push(query);

                if (response.isSuccess() && response.getResponse() != null) {
                    Object payload = response.getResponse();
                    if (payload instanceof List<?> seasonPlayers) {
                        return new ArrayList<>(seasonPlayers);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching season players for club member: " + e.getMessage());
        }
        return new ArrayList<>();
    }
}





