package com.dandziz.bookhub.services;

import com.dandziz.bookhub.constants.QueueType;
import org.springframework.stereotype.Component;

@Component
public class QueueManager {
    private final DynamicTaskQueue emailQueue = new DynamicTaskQueue(QueueType.SINGLE_THREAD, 1);
    private final DynamicTaskQueue orderQueue = new DynamicTaskQueue(QueueType.MULTI_THREAD, 2);
    private final DynamicTaskQueue notificationQueue = new DynamicTaskQueue(QueueType.MULTI_THREAD, 2);

    public void submitEmailTask(Runnable task) {
        emailQueue.submitTask(task);
    }

    public void submitOrderTask(Runnable task) {
        orderQueue.submitTask(task);
    }

    public void submitNotificationTask(Runnable task) {
        notificationQueue.submitTask(task);
    }
}
