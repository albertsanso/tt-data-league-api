package org.cttelsamicsterrassa.data.api.graphql.practicioner;

import java.util.UUID;

public record PracticionerGraphQLDto(
        UUID id,
        String firstName,
        String secondName,
        String fullName,
        String birthDate
) {
    public static PracticionerGraphQLDto fromDomain(org.cttelsamicsterrassa.data.core.domain.model.Practicioner practicioner) {
        return new PracticionerGraphQLDto(
                practicioner.getId(),
                practicioner.getFirstName(),
                practicioner.getSecondName(),
                practicioner.getFullName(),
                practicioner.getBirthDate().toString()
        );
    }
}

