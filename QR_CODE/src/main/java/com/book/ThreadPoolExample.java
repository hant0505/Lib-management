package com.book;

import java.util.concurrent.*;

public class ThreadPoolExample {

    public static void main(String[] args) {
        // Tạo một thread pool với số lượng tối đa là 10 thread
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // Gửi các công việc vào thread pool
        for (int i = 0; i < 20; i++) {
            int taskId = i;
            executorService.submit(() -> {
                try {
                    // Mô phỏng công việc của thread
                    System.out.println("Task " + taskId + " đang thực hiện...");
                    Thread.sleep(1000); // Giả lập công việc
                    System.out.println("Task " + taskId + " hoàn thành.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        // Đảm bảo không thêm thread mới và cho phép các task còn lại hoàn thành
        executorService.shutdown();

        // Chờ tất cả các task hoàn thành trước khi kết thúc chương trình
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow(); // Nếu có task chưa hoàn thành, ép dừng
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow(); // Nếu có ngoại lệ, dừng tất cả thread ngay lập tức
        }

        System.out.println("Tất cả các task đã hoàn thành.");
    }
}
