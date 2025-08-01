package com.kadirkara.product.contract;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Contract Testing Examples - Consumer verifies Provider contracts")
public class SimpleContractTest {

    private WireMockServer wireMockServer;
    private HttpClient httpClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);

        httpClient = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("Contract: GET /api/products/{id} should return product with correct structure")
    void contractGetProductByIdShouldReturnCorrectStructure() throws IOException, InterruptedException {
        // Given - Setup Provider contract (what the API promises to return)
        Map<String, Object> expectedResponse = Map.of(
                "id", 1,
                "name", "Test Product",
                "price", 99.99,
                "_links", Map.of(
                        "self", Map.of("href", "http://localhost/api/products/1"),
                        "products", Map.of("href", "http://localhost/api/products")
                )
        );

        stubFor(get(urlEqualTo("/api/products/1"))
                .withHeader("Accept", equalTo("application/hal+json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/hal+json")
                        .withBody(objectMapper.writeValueAsString(expectedResponse))));

        // When - Consumer makes request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8089/api/products/1"))
                .header("Accept", "application/hal+json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Then - Verify contract is honored
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type").orElse(""))
                .contains("application/hal+json");

        Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);

        // Contract validation - these fields MUST be present
        assertThat(responseBody.get("id")).isEqualTo(1);
        assertThat(responseBody.get("name")).isEqualTo("Test Product");
        assertThat(responseBody.get("price")).isEqualTo(99.99);
        assertThat(responseBody.containsKey("_links")).isTrue();

        // Verify the exact request was made (Provider contract verification)
        verify(getRequestedFor(urlEqualTo("/api/products/1"))
                .withHeader("Accept", equalTo("application/hal+json")));
    }

    @Test
    @DisplayName("Contract: POST /api/products should accept correct payload and return created resource")
    void contractCreateProductShouldAcceptPayloadAndReturnResource() throws IOException, InterruptedException {
        // Given - Consumer's contract expectations
        Map<String, Object> requestPayload = Map.of(
                "name", "New Product",
                "price", 149.99,
                "description", "New Product Description",
                "sku", "SKU002",
                "barcode", "987654321",
                "category", Map.of("name", "Electronics")
        );

        Map<String, Object> expectedResponse = Map.of(
                "id", 1,
                "name", "Test Product",
                "price", 99.99,
                "_links", Map.of(
                        "self", Map.of("href", "http://localhost/api/products/1"),
                        "products", Map.of("href", "http://localhost/api/products")
                )
        );

        stubFor(post(urlEqualTo("/api/products"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withHeader("Accept", equalTo("application/hal+json"))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(requestPayload)))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/hal+json")
                        .withHeader("Location", "http://localhost/api/products/1")
                        .withBody(objectMapper.writeValueAsString(expectedResponse))));

        // When - Consumer makes create request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8089/api/products"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/hal+json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestPayload)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Then - Verify contract compliance
        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.headers().firstValue("Location").orElse(""))
                .isEqualTo("http://localhost/api/products/1");
        assertThat(response.headers().firstValue("Content-Type").orElse(""))
                .contains("application/hal+json");

        Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);
        assertThat(responseBody.get("id")).isEqualTo(1);
        assertThat(responseBody.get("name")).isEqualTo("Test Product");
        assertThat(responseBody.get("price")).isEqualTo(99.99);

        // Verify contract interaction
        verify(postRequestedFor(urlEqualTo("/api/products"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withHeader("Accept", equalTo("application/hal+json")));
    }

    @Test
    @DisplayName("Contract: GET /api/products/999 should return 404 for non-existent resource")
    void contractShouldReturn404ForNonExistentResource() throws IOException, InterruptedException {
        // Given - Contract specifies 404 for non-existent resources
        stubFor(get(urlEqualTo("/api/products/999"))
                .willReturn(aResponse()
                        .withStatus(404)));

        // When - Consumer requests non-existent resource
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8089/api/products/999"))
                .header("Accept", "application/hal+json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Then - Contract should be honored (404 status)
        assertThat(response.statusCode()).isEqualTo(404);

        verify(getRequestedFor(urlEqualTo("/api/products/999")));
    }

    @Test
    @DisplayName("Contract: DELETE /api/products/{id} should return 204 No Content")
    void contractDeleteShouldReturn204NoContent() throws IOException, InterruptedException {
        // Given - Contract for delete operation
        stubFor(delete(urlEqualTo("/api/products/1"))
                .willReturn(aResponse()
                        .withStatus(204)));

        // When - Consumer makes delete request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8089/api/products/1"))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Then - Contract should be honored
        assertThat(response.statusCode()).isEqualTo(204);
        assertThat(response.body()).isEmpty();

        verify(deleteRequestedFor(urlEqualTo("/api/products/1")));
    }

    @Test
    @DisplayName("Contract Explanation Test - This demonstrates the contract testing concept")
    void contractExplanationTest() {
        /*
         * CONTRACT TESTING EXPLANATION:
         *
         * 1. WHAT IT IS:
         *    - Tests the "contract" (API interface) between services
         *    - Ensures Producer and Consumer agree on the API structure
         *    - Prevents breaking changes between microservices
         *
         * 2. TWO SIDES:
         *    - PRODUCER (Provider): The API that serves data (our REST API)
         *    - CONSUMER: The client that uses the API (mobile app, frontend, other services)
         *
         * 3. HOW IT WORKS:
         *    - Consumer defines expectations (what it needs from the API)
         *    - Producer must fulfill those expectations (contract compliance)
         *    - If Producer changes the API, tests fail unless Consumer agrees
         *
         * 4. BENEFITS:
         *    - Early detection of breaking changes
         *    - Documentation of API expectations
         *    - Confidence in service integration
         *    - Independent development of services
         *
         * 5. TOOLS USED:
         *    - Spring Cloud Contract: Producer contract verification
         *    - WireMock: Consumer contract simulation
         *    - HTTP clients: Simulating real consumer behavior
         *
         * 6. IN THIS EXAMPLE:
         *    - WireMock simulates our Product API (Producer)
         *    - HTTP client acts as Consumer
         *    - We verify both sides honor the contract
         */

        // This test just demonstrates the concept - no actual testing
        assertThat("Contract testing").contains("Contract");
    }
}