package org.cttelsamicsterrassa.data.api.rest.match;

import org.albertsanso.commons.query.DomainQueryResponse;
import org.albertsanso.commons.query.QueryBus;
import org.cttelsamicsterrassa.data.api.core.match.find.FindMatchesQuery;
import org.cttelsamicsterrassa.data.api.core.match.find.FindMatchesRequestBodyDto;
import org.cttelsamicsterrassa.data.core.domain.model.CompetitionInfo;
import org.cttelsamicsterrassa.data.core.domain.model.PlayersSingleMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@MatchOpenAPIv1Controller
public class MatchController {

    @Autowired
    private QueryBus queryBus;

    @GetMapping("/{id}")
    public MatchDto getMatch(@PathVariable("id") UUID id) {
        return new MatchDto(id, "Team A", "Team B", 3, 2, "2024-06-15");
    }

    @PostMapping("/enriched/find_matches")
    public ResponseEntity<List<EnrichedMatchDto>> findMatches(@RequestBody FindMatchesRequestBodyDto requestBody) {

        FindMatchesQuery findMatchesQuery = new FindMatchesQuery(
                requestBody.season(),
                new CompetitionInfo(
                        requestBody.competitionType(),
                        requestBody.competitionCategory(),
                        requestBody.competitionScope(),
                        requestBody.competitionScopeTag(),
                        requestBody.competitionGroup(),
                        requestBody.competitionGender()
                ),
                requestBody.matchDayNumber()
        );

        DomainQueryResponse domainQueryResponse = queryBus.push(findMatchesQuery);
        if (domainQueryResponse.isSuccess()) {
            List<PlayersSingleMatch> matchesList = (List<PlayersSingleMatch>) domainQueryResponse.getResponse();
            List<EnrichedMatchDto> enrichedMatchesDtosList = matchesList.stream()
                    .map(EnrichedMatchDto::fromDomain)
                    .toList();
            return ResponseEntity.ok(enrichedMatchesDtosList);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }
}
