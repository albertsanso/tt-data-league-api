package org.cttelsamicsterrassa.data.api.core.season_player.find;

import org.albertsanso.commons.query.DomainQuery;

import java.time.ZonedDateTime;
import java.util.UUID;

public class FindSeasonPlayerByLicenseQuery extends DomainQuery {
    private final String licenseTag;
    private final String licenseId;

    public FindSeasonPlayerByLicenseQuery(String licenseTag, String licenseId) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.licenseTag = licenseTag;
        this.licenseId = licenseId;
    }

    public String getLicenseTag() {
        return licenseTag;
    }

    public String getLicenseId() {
        return licenseId;
    }
}
