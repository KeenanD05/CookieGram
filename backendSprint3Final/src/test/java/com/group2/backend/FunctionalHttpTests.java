package com.group2.backend;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionalHttpTests {

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private static final String BASE = "http://localhost:8080";

    private static int getStatus(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        return client.send(req, HttpResponse.BodyHandlers.discarding()).statusCode();
    }

    private static HttpResponse<String> postJson(String url, String json) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void backendIsReachable() throws Exception {
        int status = getStatus(BASE + "/");
        assertTrue(status >= 200 && status < 500, "Unexpected status: " + status);
    }

    @Test
    void apiEndpointResponds() throws Exception {
        int status = getStatus(BASE + "/api/posts"); 
        assertTrue(
                status == 200 || status == 401 || status == 403,
                "Unexpected status: " + status
        );
    }

    @Test
    void loginMissingFieldsShouldFail() throws Exception {
        HttpResponse<String> res = postJson(BASE + "/api/login", "{}"); //
        assertTrue(res.statusCode() >= 400 && res.statusCode() < 500);
    }
}
