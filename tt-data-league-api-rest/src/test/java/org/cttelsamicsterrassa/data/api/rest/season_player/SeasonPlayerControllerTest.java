package org.cttelsamicsterrassa.data.api.rest.season_player;

import org.albertsanso.commons.command.CommandBus;
import org.albertsanso.commons.command.DomainCommandResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SeasonPlayerControllerTest {

    @Mock
    private QueryBus queryBus;

    @Mock
    private CommandBus commandBus;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SeasonPlayerController controller = new SeasonPlayerController();
        ReflectionTestUtils.setField(controller, "queryBus", queryBus);
        ReflectionTestUtils.setField(controller, "commandBus", commandBus);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void modifyReturnsInternalServerErrorWhenCommandFails() throws Exception {
        when(commandBus.push(any())).thenReturn(DomainCommandResponse.failResponse("error"));

        String requestBody = """
                {
                  "id": "%s",
                  "clubMemberId": "%s",
                  "licenseId": "1234",
                  "licenseTag": "ABC",
                  "yearRange": "2025-2026"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID());

        mockMvc.perform(put("/api/v1/season_player")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError());

        verify(commandBus).push(any());
    }
}

