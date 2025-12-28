package org.cttelsamicsterrassa.data.api.rest.match;

import org.albertsanso.commons.query.QueryBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/match")
public class MatchController {

    @Autowired
    private QueryBus queryBus;

    @GetMapping("/{id}")
    public MatchDto getMatch(@PathVariable("id") UUID id) {
        return new MatchDto(id, "Team A", "Team B", 3, 2, "2024-06-15");
    }

    public List<MatchDto> findAllMatches() {
        return List.of(
            new MatchDto(UUID.randomUUID(), "Team A", "Team B", 3, 2, "2024-06-15"),
            new MatchDto(UUID.randomUUID(), "Team C", "Team D", 1, 1, "2024-06-16")
        );
    }

    @PostMapping
    public MatchDto createPlayer(@RequestBody MatchDto match) {
        return new MatchDto(UUID.randomUUID(), match.homeTeam(), match.awayTeam(), match.homeScore(), match.awayScore(), match.matchDate());
    }
}
