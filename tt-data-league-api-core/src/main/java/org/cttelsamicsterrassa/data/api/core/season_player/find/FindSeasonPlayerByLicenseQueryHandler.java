package org.cttelsamicsterrassa.data.api.core.season_player.find;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.repository.SeasonPlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindSeasonPlayerByLicenseQueryHandler extends DomainQueryHandler<FindSeasonPlayerByLicenseQuery> {

    private final SeasonPlayerRepository seasonPlayerRepository;

    @Autowired
    public FindSeasonPlayerByLicenseQueryHandler(SeasonPlayerRepository seasonPlayerRepository) {
        this.seasonPlayerRepository = seasonPlayerRepository;
    }

    @Override
    public DomainQueryResponse handle(FindSeasonPlayerByLicenseQuery findSeasonPlayerByLicenseQuery) {
        return DomainQueryResponse.sucessResponse(
                seasonPlayerRepository.findByLicense(
                        findSeasonPlayerByLicenseQuery.getLicenseId(),
                        findSeasonPlayerByLicenseQuery.getLicenseTag()));
    }
}
