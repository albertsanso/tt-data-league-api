package org.cttelsamicsterrassa.data.api.rest.practicioner;

import io.swagger.v3.oas.annotations.Operation;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.albertsanso.commons.query.QueryBus;
import org.cttelsamicsterrassa.data.api.core.practicioner.find.FindPracticionerByNameQuery;
import org.cttelsamicsterrassa.data.core.domain.model.Practicioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@PracticionerOpenAPIv1Controller
public class PracticionerController {

    @Autowired
    private QueryBus queryBus;

    @GetMapping("/find_by_similar_name")
    @Operation(summary = "Find practicioners by similar name", description = "Search practicioners by fullName fragment (case-insensitive partial match)")
    public ResponseEntity<List<PracticionerDto>> findBySimilarName(@RequestParam("name") String name) {
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        FindPracticionerByNameQuery query = new FindPracticionerByNameQuery(name.trim());
        DomainQueryResponse queryResponse = queryBus.push(query);
        if (!queryResponse.isSuccess()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        Collection<?> response = (Collection<?>) queryResponse.getResponse();
        if (response == null) {
            return ResponseEntity.ok(List.of());
        }

        List<PracticionerDto> dtos = response.stream()
                .filter(Practicioner.class::isInstance)
                .map(Practicioner.class::cast)
                .map(PracticionerDto::fromDomain)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}

