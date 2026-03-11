package org.cttelsamicsterrassa.data.api.core.practicioner.find;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.Practicioner;
import org.cttelsamicsterrassa.data.core.domain.repository.PracticionerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Query handler for retrieving a practicioner by ID.
 * Executes the FindPracticionerByIdQuery and returns the specific practicioner from the repository.
 */
@Component
public class FindPracticionerByIdQueryHandler extends DomainQueryHandler<FindPracticionerByIdQuery, Practicioner> {

    private final PracticionerRepository practicionerRepository;

    @Autowired
    public FindPracticionerByIdQueryHandler(PracticionerRepository practicionerRepository) {
        this.practicionerRepository = practicionerRepository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DomainQueryResponse<Practicioner> handle(FindPracticionerByIdQuery findPracticionerByIdQuery) {
        return practicionerRepository.findById(findPracticionerByIdQuery.getPracticionerId())
                .map(DomainQueryResponse::sucessResponse)
                .orElseGet(() -> (DomainQueryResponse<Practicioner>) (DomainQueryResponse<?>)
                    DomainQueryResponse.failResponse("Practicioner with that ID doesn't exist"));
    }
}

