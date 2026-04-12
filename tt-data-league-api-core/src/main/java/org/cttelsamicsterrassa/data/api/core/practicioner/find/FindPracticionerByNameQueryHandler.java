package org.cttelsamicsterrassa.data.api.core.practicioner.find;

import org.albertsanso.commons.query.DomainQueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.Practicioner;
import org.cttelsamicsterrassa.data.core.domain.repository.PracticionerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

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
        String normalizedName = findPracticionerByNameQuery.getName() == null
                ? ""
                : findPracticionerByNameQuery.getName().trim().toLowerCase(Locale.ROOT);

        List<Practicioner> allPracticioners = practicionerRepository.findAll();
        List<Practicioner> resultList = allPracticioners == null
                ? List.of()
                : allPracticioners.stream()
                .filter(practicioner -> practicioner.getFullName() != null)
                .filter(practicioner -> practicioner.getFullName().toLowerCase(Locale.ROOT).contains(normalizedName))
                .toList();

        return DomainQueryResponse.sucessResponse(resultList);
    }
}



