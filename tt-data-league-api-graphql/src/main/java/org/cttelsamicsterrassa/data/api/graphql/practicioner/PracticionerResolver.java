package org.cttelsamicsterrassa.data.api.graphql.practicioner;

import org.albertsanso.commons.query.QueryBus;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.api.core.club_member.find.application.FindClubMembersByPracticionerIdQuery;
import org.cttelsamicsterrassa.data.api.core.practicioner.find.FindAllPracticionersQuery;
import org.cttelsamicsterrassa.data.api.core.practicioner.find.FindPracticionerByIdQuery;
import org.cttelsamicsterrassa.data.api.core.practicioner.find.FindPracticionerByNameQuery;
import org.cttelsamicsterrassa.data.core.domain.model.Practicioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for resolving Practicioner-related GraphQL queries.
 * This service coordinates with the QueryBus to fetch practicioner data from the domain.
 */
@Service
public class PracticionerResolver {

    @Autowired
    private QueryBus queryBus;

    @QueryMapping
    public PracticionerGraphQLDto findPracticionerById(String id) {
        try {
            FindPracticionerByIdQuery query = new FindPracticionerByIdQuery(UUID.fromString(id));
            DomainQueryResponse response = queryBus.push(query);

            if (response.isSuccess() && response.getResponse() != null) {
                Practicioner practicioner = (Practicioner) response.getResponse();
                return PracticionerGraphQLDto.fromDomain(practicioner);
            }
        } catch (Exception e) {
            System.err.println("Error finding practicioner by ID: " + e.getMessage());
        }
        return null;
    }

    @QueryMapping
    public List<PracticionerGraphQLDto> findPracticionerByName(String name) {
        try {
            FindPracticionerByNameQuery query = new FindPracticionerByNameQuery(name);
            DomainQueryResponse response = queryBus.push(query);

            if (response.isSuccess() && response.getResponse() != null) {
                Object payload = response.getResponse();
                if (payload instanceof List<?> practicioners) {
                    return practicioners.stream()
                        .map(p -> PracticionerGraphQLDto.fromDomain((Practicioner) p))
                        .collect(Collectors.toList());
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding practicioner by name: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    @QueryMapping
    public List<PracticionerGraphQLDto> listAllPracticioners() {
        try {
            FindAllPracticionersQuery query = new FindAllPracticionersQuery();
            DomainQueryResponse response = queryBus.push(query);

            if (response.isSuccess() && response.getResponse() != null) {
                Object payload = response.getResponse();
                if (payload instanceof List<?> practicioners) {
                    return practicioners.stream()
                        .map(p -> PracticionerGraphQLDto.fromDomain((Practicioner) p))
                        .collect(Collectors.toList());
                }
            }
        } catch (Exception e) {
            System.err.println("Error listing all practicioners: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<?> getPracticionerMemberships(PracticionerGraphQLDto practicioner) {
        try {
            // Extract the UUID from the PracticionerGraphQLDto
            // Note: PracticionerGraphQLDto needs to expose the ID - adjust if needed
            FindClubMembersByPracticionerIdQuery query = new FindClubMembersByPracticionerIdQuery(practicioner.id());
            DomainQueryResponse response = queryBus.push(query);

            if (response.isSuccess() && response.getResponse() != null) {
                Object payload = response.getResponse();
                if (payload instanceof List<?> memberships) {
                    return new ArrayList<>(memberships);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching practicioner memberships: " + e.getMessage());
        }
        return new ArrayList<>();
    }
}


