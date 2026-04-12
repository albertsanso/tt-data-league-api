package org.cttelsamicsterrassa.data.api.rest.season_player_result;

import org.albertsanso.commons.command.CommandBus;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.albertsanso.commons.query.QueryBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SeasonPlayerResultControllerTest {

    @Mock
    private QueryBus queryBus;

    @Mock
    private CommandBus commandBus;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SeasonPlayerResultController controller = new SeasonPlayerResultController();
        ReflectionTestUtils.setField(controller, "queryBus", queryBus);
        ReflectionTestUtils.setField(controller, "commandBus", commandBus);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createReturnsInternalServerErrorWhenCommandFails() throws Exception {
        when(commandBus.push(any())).thenReturn(DomainCommandResponse.failResponse("error"));

        mockMvc.perform(post("/api/v1/player_result")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload()))
                .andExpect(status().isInternalServerError());

        verify(commandBus).push(any());
    }

    @Test
    void findByIdReturnsInternalServerErrorWhenQueryFails() throws Exception {
        when(queryBus.push(any())).thenReturn(DomainQueryResponse.failResponse("error"));

        mockMvc.perform(get("/api/v1/player_result/{id}", UUID.randomUUID()))
                .andExpect(status().isInternalServerError());

        verify(queryBus).push(any());
    }

    @Test
    void deleteReturnsNoContentWhenCommandSucceeds() throws Exception {
        when(commandBus.push(any())).thenReturn(DomainCommandResponse.successResponse(new Object()));

        mockMvc.perform(delete("/api/v1/player_result/{id}", UUID.randomUUID()))
                .andExpect(status().isNoContent());

        verify(commandBus).push(any());
    }

    private String validPayload() {
        return """
                {
                  "id": "%s",
                  "competitionInfo": {
                    "type": "LEAGUE",
                    "category": "SENIOR",
                    "scope": "CAT",
                    "scopeTag": "A",
                    "group": "G1",
                    "gender": "MIXED"
                  },
                  "seasonPlayer": {
                    "id": "%s",
                    "clubMemberId": "%s",
                    "licenseId": "L-1",
                    "licenseTag": "TAG",
                    "yearRange": "2025-2026"
                  },
                  "matchDay": "DAY-1",
                  "matchDayNumber": 1,
                  "matchGamePoints": "[11,8,11]",
                  "matchGamesWon": 3,
                  "matchLinkageId": "link",
                  "matchPlayerLetter": "A"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    }
}

