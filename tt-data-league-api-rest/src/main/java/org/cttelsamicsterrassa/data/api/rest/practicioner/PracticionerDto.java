package org.cttelsamicsterrassa.data.api.rest.practicioner;

import java.util.UUID;

public record PracticionerDto(
        UUID id,
        String firstName,
        String secondName,
        String fullName,
        String birthDate
) {
    public static PracticionerDto fromDomain(org.cttelsamicsterrassa.data.core.domain.model.Practicioner practicioner) {
        return new PracticionerDto(
                practicioner.getId(),
                practicioner.getFirstName(),
                practicioner.getSecondName(),
                practicioner.getFullName(),
                practicioner.getBirthDate().toString()
        );
    }
}
