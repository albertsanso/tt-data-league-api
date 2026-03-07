package org.cttelsamicsterrassa.data.api.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OpenApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void apiDocsAvailable() {
        String url = "http://localhost:" + port + "/v3/api-docs";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("openapi");
    }

    @Test
    void validationErrorReturnsErrorResponse() {
        String url = "http://localhost:" + port + "/api/v1/club";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>("{}", headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        assertThat(response.getStatusCode()).isIn(HttpStatus.BAD_REQUEST, HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("code").contains("message");
    }
}

