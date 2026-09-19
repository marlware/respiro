package com.respiro.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "app.api-key=test-secret")
class ApiKeyFilterTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void rejectsApiRequestsMissingTheKey() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/vitals/latest", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void acceptsApiRequestsWithTheCorrectKey() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-Key", "test-secret");

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/vitals/history?limit=1", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void doesNotGuardTheHealthEndpoint() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
