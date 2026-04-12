package org.cttelsamicsterrassa.data.api.rest.match;

import org.albertsanso.commons.command.CommandBus;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.albertsanso.commons.query.QueryBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MatchControllerTest {

    @Mock
    private QueryBus queryBus;

    @Mock
    private CommandBus commandBus;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MatchController controller = new MatchController();
        ReflectionTestUtils.setField(controller, "queryBus", queryBus);
        ReflectionTestUtils.setField(controller, "commandBus", commandBus);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getMatchReturnsInternalServerErrorWhenQueryFails() throws Exception {
        when(queryBus.push(any())).thenReturn(DomainQueryResponse.failResponse("not found"));

        mockMvc.perform(get("/api/v1/match/{id}", UUID.randomUUID()))
                .andExpect(status().isInternalServerError());

        verify(queryBus).push(any());
    }

    @Test
    void deleteMatchReturnsNoContentWhenCommandSucceeds() throws Exception {
        when(commandBus.push(any())).thenReturn(DomainCommandResponse.successResponse(new Object()));

        mockMvc.perform(delete("/api/v1/match/{id}", UUID.randomUUID()))
                .andExpect(status().isNoContent());

        verify(commandBus).push(any());
    }
}

