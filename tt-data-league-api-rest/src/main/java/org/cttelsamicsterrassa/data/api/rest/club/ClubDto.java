package org.cttelsamicsterrassa.data.api.rest.club;

import org.cttelsamicsterrassa.data.core.domain.model.Club;

import java.util.List;
import java.util.UUID;

public record ClubDto(
        UUID id,
        String name,
        List<String> yearRanges) {

    public static ClubDto fromObject(Club club) {
        return new ClubDto(
                club.getId(),
                club.getName(),
                club.getYearRanges()
        );
    }
}
