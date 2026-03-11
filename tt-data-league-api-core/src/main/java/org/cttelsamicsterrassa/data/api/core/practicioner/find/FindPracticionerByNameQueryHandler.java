package org.cttelsamicsterrassa.data.api.core.practicioner.find;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.Practicioner;
import org.cttelsamicsterrassa.data.core.domain.repository.PracticionerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Query handler for retrieving practicioners by name.
 * Executes the FindPracticionerByNameQuery and returns practicioners matching the name criteria.
 */
@Component
public class FindPracticionerByNameQueryHandler extends DomainQueryHandler<FindPracticionerByNameQuery, List<Practicioner>> {

    private final PracticionerRepository practicionerRepository;

    @Autowired
    public FindPracticionerByNameQueryHandler(PracticionerRepository practicionerRepository) {
        this.practicionerRepository = practicionerRepository;
    }

    @Override
    public DomainQueryResponse<List<Practicioner>> handle(FindPracticionerByNameQuery findPracticionerByNameQuery) {
        // TODO: Implement once findBySimilarName() is available in PracticionerRepository
        // For now return empty list as a placeholder
        return DomainQueryResponse.sucessResponse(new ArrayList<>());
    }
}



