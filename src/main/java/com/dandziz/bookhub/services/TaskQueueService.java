package com.dandziz.bookhub.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class TaskQueueService {

    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void initWorker() {
        executorService.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    System.out.format("Queue size: %d\n", taskQueue.size());
                    Runnable task = taskQueue.take();
                    task.run();
                } catch (InterruptedException e) {
                    System.out.println("Break");
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void addTask(Runnable task) {
        taskQueue.offer(task);
    }
}
