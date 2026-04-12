package org.cttelsamicsterrassa.data.api.rest.club_member;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ClubMemberControllerTest {

    @Mock
    private QueryBus queryBus;

    @Mock
    private CommandBus commandBus;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ClubMemberController controller = new ClubMemberController();
        ReflectionTestUtils.setField(controller, "queryBus", queryBus);
        ReflectionTestUtils.setField(controller, "commandBus", commandBus);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void findByIdReturnsInternalServerErrorWhenQueryFails() throws Exception {
        UUID id = UUID.randomUUID();
        when(queryBus.push(any())).thenReturn(DomainQueryResponse.failResponse("not found"));

        mockMvc.perform(get("/api/v1/club_member/{id}", id))
                .andExpect(status().isInternalServerError());

        verify(queryBus).push(any());
    }

    @Test
    void modifyReturnsInternalServerErrorWhenCommandFails() throws Exception {
        when(commandBus.push(any())).thenReturn(DomainCommandResponse.failResponse("error"));

        String requestBody = """
                {
                  "id": "%s",
                  "clubId": "%s",
                  "practicionerId": "%s",
                  "yearRanges": ["2025-2026"]
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        mockMvc.perform(put("/api/v1/club_member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError());

        verify(commandBus).push(any());
    }

    @Test
    void deleteReturnsNoContentWhenCommandSucceeds() throws Exception {
        UUID id = UUID.randomUUID();
        when(commandBus.push(any())).thenReturn(DomainCommandResponse.successResponse(new Object()));

        mockMvc.perform(delete("/api/v1/club_member/{id}", id))
                .andExpect(status().isNoContent());

        verify(commandBus).push(any());
    }
}

