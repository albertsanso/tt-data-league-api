package org.cttelsamicsterrassa.data.api.core.practicioner.find;

import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.core.domain.model.Practicioner;
import org.cttelsamicsterrassa.data.core.domain.repository.PracticionerRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FindPracticionerByNameQueryHandlerTest {

    private final PracticionerRepository practicionerRepository = mock(PracticionerRepository.class);
    private final FindPracticionerByNameQueryHandler handler = new FindPracticionerByNameQueryHandler(practicionerRepository);

    @Test
    void handleReturnsRepositoryResults() {
        Practicioner first = mock(Practicioner.class);
        Practicioner second = mock(Practicioner.class);
        Practicioner other = mock(Practicioner.class);
        when(first.getFullName()).thenReturn("John Smith");
        when(second.getFullName()).thenReturn("Anna Smithers");
        when(other.getFullName()).thenReturn("Maria Garcia");
        when(practicionerRepository.searchBySimilarName("smith")).thenReturn(List.of(first, second, other));

        DomainQueryResponse<List<Practicioner>> response = handler.handle(new FindPracticionerByNameQuery("smith"));

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getResponse()).containsExactly(first, second, other);
        verify(practicionerRepository).searchBySimilarName("smith");
    }

    @Test
    void handleReturnsEmptyListWhenRepositoryReturnsNull() {
        when(practicionerRepository.searchBySimilarName("unknown")).thenReturn(null);

        DomainQueryResponse<List<Practicioner>> response = handler.handle(new FindPracticionerByNameQuery("unknown"));

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getResponse()).isNull();
    }
}
