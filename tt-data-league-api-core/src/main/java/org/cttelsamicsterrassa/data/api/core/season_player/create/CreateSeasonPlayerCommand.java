package org.cttelsamicsterrassa.data.api.core.season_player.create;

import org.albertsanso.commons.command.DomainCommand;

import java.time.ZonedDateTime;
import java.util.UUID;

public class CreateSeasonPlayerCommand extends DomainCommand {

    private final UUID clubMemberId;

    private final String licenseId;

    private final String licenseTag;

    private final String yearRange;

    public CreateSeasonPlayerCommand(UUID clubMemberId, String licenseId, String licenseTag, String yearRange) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.clubMemberId = clubMemberId;
        this.licenseId = licenseId;
        this.licenseTag = licenseTag;
        this.yearRange = yearRange;
    }

    public UUID getClubMemberId() {
        return clubMemberId;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public String getLicenseTag() {
        return licenseTag;
    }

    public String getYearRange() {
        return yearRange;
    }
}
