package org.cttelsamicsterrassa.data.api.rest.practicioner;

import org.albertsanso.commons.query.DomainQueryResponse;
import org.albertsanso.commons.query.QueryBus;
import org.cttelsamicsterrassa.data.api.core.practicioner.find.FindPracticionerByNameQuery;
import org.cttelsamicsterrassa.data.core.domain.model.Practicioner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PracticionerControllerTest {

    @Mock
    private QueryBus queryBus;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        PracticionerController controller = new PracticionerController();
        ReflectionTestUtils.setField(controller, "queryBus", queryBus);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void findBySimilarNameReturnsMatchingPracticioners() throws Exception {
        Practicioner practicioner = mock(Practicioner.class);
        UUID id = UUID.randomUUID();
        when(practicioner.getId()).thenReturn(id);
        when(practicioner.getFirstName()).thenReturn("John");
        when(practicioner.getSecondName()).thenReturn("Smith");
        when(practicioner.getFullName()).thenReturn("John Smith");
        when(practicioner.getBirthDate()).thenReturn(new Date(0));

        when(queryBus.push(any())).thenReturn(DomainQueryResponse.sucessResponse(List.of(practicioner)));

        mockMvc.perform(get("/api/v1/practicioner/find_by_similar_name").param("name", "sMiTh"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].fullName").value("John Smith"));

        ArgumentCaptor<FindPracticionerByNameQuery> captor = ArgumentCaptor.forClass(FindPracticionerByNameQuery.class);
        verify(queryBus).push(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("sMiTh");
    }

    @Test
    void findBySimilarNameReturnsBadRequestWhenNameMissing() throws Exception {
        mockMvc.perform(get("/api/v1/practicioner/find_by_similar_name"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findBySimilarNameReturnsBadRequestWhenNameBlank() throws Exception {
        mockMvc.perform(get("/api/v1/practicioner/find_by_similar_name").param("name", "   "))
                .andExpect(status().isBadRequest());
    }
}



