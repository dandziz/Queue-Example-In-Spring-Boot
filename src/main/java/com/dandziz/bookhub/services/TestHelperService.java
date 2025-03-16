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
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class TestHelperService {

    private final TaskQueueService taskQueueService;
    private final RestTemplate restTemplate;
    private final List<String> urls = new ArrayList<>(List.of(
        "https://www.google.com/",
        "https://www.samsung.com/vn/",
        "https://www.baeldung.com/"
    ));

    @Async
    public void testAsync() throws InterruptedException {
        taskQueueService.addTask(() -> {
            try {
                Thread currentThread = Thread.currentThread();
                String time = time();
                int sleepTime = ThreadLocalRandom.current().nextInt(2000, 5001);
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
