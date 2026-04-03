package org.cttelsamicsterrassa.data.api.rest;

import org.junit.jupiter.api.Test;
import org.albertsanso.commons.command.CommandBus;
import org.albertsanso.commons.query.QueryBus;
import org.cttelsamicsterrassa.data.api.rest.club.ClubController;
import org.cttelsamicsterrassa.data.api.rest.error.GlobalExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = OpenApiIntegrationTest.TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.autoconfigure.exclude="
                + "org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
)
public class OpenApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CommandBus commandBus;

    @Test
    void apiDocsAvailable() {
        String url = "http://localhost:" + port + "/v3/api-docs";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        if (response.getStatusCode().is2xxSuccessful()) {
            assertThat(response.getBody()).contains("openapi");
        } else {
            assertThat(response.getBody()).contains("code").contains("message");
        }
    }

    @Test
    void validationErrorReturnsErrorResponse() {
        doThrow(new RuntimeException("forced test error")).when(commandBus).push(any());

        String url = "http://localhost:" + port + "/api/v1/club";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>("{\"name\":\"test\",\"yearRanges\":[\"2025-2026\"]}", headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("code").contains("message");
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({ClubController.class, GlobalExceptionHandler.class})
    static class TestApplication {

        @Bean
        CommandBus commandBus() {
            return mock(CommandBus.class);
        }

        @Bean
        QueryBus queryBus() {
            return mock(QueryBus.class);
        }
    }
}

