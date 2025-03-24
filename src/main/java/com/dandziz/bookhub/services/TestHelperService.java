package com.dandziz.bookhub.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class TestHelperService {

    private final QueueManager queueManager;
    private final RestTemplate restTemplate;
    private final List<String> urls = new ArrayList<>(List.of(
        "https://www.google.com/",
        "https://www.samsung.com/vn/",
        "https://www.baeldung.com/"
    ));

    @Async
    public void testOrderAsync() throws InterruptedException {
        queueManager.submitOrderTask(() -> {
            try {
                Thread currentThread = Thread.currentThread();
                UUID uuid = UUID.randomUUID();
                String time = time();
                System.out.format("[%s][%s] Order Task started, sleeping for %d ms, thread %d\n", time, uuid, 5000, currentThread.threadId());

                Thread.sleep(5000);
                System.out.format("[%s][%s] Order Task finished, thread %d\n", time, uuid, currentThread.threadId());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    @Async
    public void testNotificationAsync() throws InterruptedException {
        queueManager.submitNotificationTask(() -> {
            try {
                Thread currentThread = Thread.currentThread();
                String time = time();
                UUID uuid = UUID.randomUUID();
                System.out.format("[%s][%s] Notification Task started, sleeping for %d ms, thread %d\n", time, uuid, 30000, currentThread.threadId());

                Thread.sleep(30000);
                System.out.format("[%s][%s] Notification Task finished, thread %d\n", time, uuid, currentThread.threadId());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    @Async
    public void testAsync() throws InterruptedException {
        queueManager.submitEmailTask(() -> {
            try {
                Thread currentThread = Thread.currentThread();
                String time = time();
                int sleepTime = ThreadLocalRandom.current().nextInt(12000, 15000);
                System.out.format("[%s] Task started, sleeping for %d ms, thread %d\n", time, sleepTime, currentThread.threadId());

                Thread.sleep(sleepTime);
                getData();

                System.out.format("[%s] Task finished, thread %d\n", time, currentThread.threadId());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private String time() {
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS");
        return now.format(formatter);
    }

    private void getData() {
        Set<String> fetchedUrls = new HashSet<>();
        for (String url : urls) {
            if (!fetchedUrls.contains(url)) {
                try {
                    String response = restTemplate.getForObject(url, String.class);
                    System.out.println("Fetched from: " + url);
                    System.out.println(response);
                    fetchedUrls.add(url);
                } catch (Exception e) {
                    System.err.println("Failed to fetch: " + url);
                }
            }
        }
    }
}
