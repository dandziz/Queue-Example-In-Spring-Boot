package com.dandziz.bookhub.services;

import com.dandziz.bookhub.constants.QueueType;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class DynamicTaskQueue {
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final ExecutorService executorService;

    public DynamicTaskQueue(QueueType queueType, int maxThreads) {
        if (queueType == QueueType.SINGLE_THREAD) {
            this.executorService = Executors.newSingleThreadExecutor();
        } else {
            this.executorService = Executors.newFixedThreadPool(maxThreads);
        }

        startWorker();
    }

    public void submitTask(Runnable task) {
        queue.offer(task);
    }

    private void startWorker() {
        executorService.submit(() -> {
            while (true) {
                try {
                    Runnable task = queue.take();
                    executorService.submit(task);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }
}
