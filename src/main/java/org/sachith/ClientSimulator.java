package org.sachith;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientSimulator {

    private static final String BASE_URL =
            "http://localhost:8080/bus-ticket-server";

    private static final int USERS = 100;

    public static void main(String[] args) throws InterruptedException {

        ExecutorService executor =
                Executors.newFixedThreadPool(USERS);

        CountDownLatch readyLatch =
                new CountDownLatch(USERS);

        CountDownLatch startLatch =
                new CountDownLatch(1);

        HttpClient client =
                HttpClient.newHttpClient();

        for (int i = 1; i <= USERS; i++) {

            int userId = i+1;

            executor.submit(() -> {

                try {

                    readyLatch.countDown();
                    startLatch.await();

                    String requestBody = """
                            {
                              "origin":"A",
                              "destination":"D",
                              "passengers":1,
                              "paymentAmount":150,
                              "travelDate":"2026-03-10"
                            }
                            """;

                    HttpRequest request =
                            HttpRequest.newBuilder()
                                    .uri(URI.create(BASE_URL + "/reserve"))
                                    .header("Content-Type", "application/json")
                                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                                    .build();

                    HttpResponse<String> response =
                            client.send(request,
                                    HttpResponse.BodyHandlers.ofString());

                    System.out.println(
                            "User " + userId + ": " +
                                    response.statusCode() + " -> " +
                                    response.body()
                    );

                } catch (Exception e) {

                    System.out.println(
                            "User " + userId + " failed: " +
                                    e.getMessage());
                }
            });
        }

        // Wait until all threads are ready
        readyLatch.await();

        System.out.println("Starting simulation for " + USERS + " users...");

        // Start all at same time
        startLatch.countDown();

        executor.shutdown();
    }
}