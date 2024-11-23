package com.example.demo;

import java.util.concurrent.*;

public class ThreadPoolExample {
    public static void main(String[] args) {
        // Tạo một thread pool với 4 luồng
        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 10; i++) {
            executor.submit(new RunnableTask(i));
        }

        // Dừng thread pool khi hoàn thành
        executor.shutdown();
    }
}

class RunnableTask implements Runnable {
    private int taskId;

    public RunnableTask(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public void run() {
        System.out.println("Task " + taskId + " is running on " + Thread.currentThread().getName());
    }
}
