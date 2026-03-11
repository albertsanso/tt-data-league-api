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
 * Query handler for retrieving all practicioners.
 * Executes the FindAllPracticionersQuery and returns all practicioners from the repository.
 */
@Component
public class FindAllPracticionersQueryHandler extends DomainQueryHandler<FindAllPracticionersQuery, List<Practicioner>> {

    private final PracticionerRepository practicionerRepository;

    @Autowired
    public FindAllPracticionersQueryHandler(PracticionerRepository practicionerRepository) {
        this.practicionerRepository = practicionerRepository;
    }

    @Override
    public DomainQueryResponse<List<Practicioner>> handle(FindAllPracticionersQuery findAllPracticionersQuery) {
        // TODO: Implement once findAll() is available in PracticionerRepository
        // For now return empty list as a placeholder
        return DomainQueryResponse.sucessResponse(new ArrayList<>());
    }
}

