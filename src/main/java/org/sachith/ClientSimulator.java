package org.sachith;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientSimulator {

    private static final String URL_STRING =
            "http://localhost:8080/reserve";

    public static void main(String[] args) {

        int users = 50;

        ExecutorService executor =
                Executors.newFixedThreadPool(10);

        for (int i = 1; i <= users; i++) {

            int userId = i;

            executor.submit(() -> {

                try {

                    sendReservation(userId);

                } catch (Exception e) {

                    System.out.println(
                            "User " + userId + " failed: " +
                                    e.getMessage());
                }
            });
        }

        executor.shutdown();
    }

    private static void sendReservation(int userId)
            throws Exception {

        URL url = new URL(URL_STRING);

        HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");

        conn.setRequestProperty(
                "Content-Type",
                "application/json");

        conn.setDoOutput(true);

        String json =
                """
                {
                  "origin":"A",
                  "destination":"D",
                  "passengers":1,
                  "paymentAmount":150,
                  "travelDate":"2026-02-20"
                }
                """;

        OutputStream os =
                conn.getOutputStream();

        os.write(json.getBytes());

        os.flush();

        int responseCode = conn.getResponseCode();

        System.out.println(
                "User " + userId +
                        " response: " + responseCode);
    }
}