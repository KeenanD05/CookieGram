package com.group2.backend;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class NonFunctionalHttpTests {

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private static final String BASE = "http://localhost:8080";

    private static long timedGetMs(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        long start = System.nanoTime();
        HttpResponse<Void> res = client.send(req, HttpResponse.BodyHandlers.discarding());
        long end = System.nanoTime();

        int status = res.statusCode();
        assertTrue(status >= 200 && status < 500, "Unexpected status: " + status);

        return (end - start) / 1_000_000; // ms
    }

    @Test
    void rootResponseTimeUnder500ms() throws Exception {
        long ms = timedGetMs(BASE + "/");
        assertTrue(ms < 500, "Response too slow: " + ms + "ms");
    }

    @Test
    void apiResponseTimeUnder800ms() throws Exception {
        long ms = timedGetMs(BASE + "/api/posts"); 
        assertTrue(ms < 800, "Response too slow: " + ms + "ms");
    }
}
