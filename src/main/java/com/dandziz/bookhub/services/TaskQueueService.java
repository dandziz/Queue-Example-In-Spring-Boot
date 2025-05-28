package com.dandziz.bookhub.services;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TaskQueueService {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void submitTask(Runnable task) {
        executorService.submit(task);
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }
}
