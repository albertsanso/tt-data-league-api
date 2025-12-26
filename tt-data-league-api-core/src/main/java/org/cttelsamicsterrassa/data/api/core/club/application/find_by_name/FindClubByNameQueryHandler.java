package org.cttelsamicsterrassa.data.api.core.club.application.find_by_name;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.springframework.stereotype.Component;

@Component
public class FindClubByNameQueryHandler extends DomainQueryHandler<FindClubByNameQuery> {

    @Override
    public DomainQueryResponse handle(FindClubByNameQuery findClubByNameQuery) {
        return null;
    }
}
