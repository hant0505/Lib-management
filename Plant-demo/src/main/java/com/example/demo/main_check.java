package com.example.demo;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.stage.Stage;

public class main_check extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Khởi động Service trong phương thức start của JavaFX Application
        HeavyService service = new HeavyService();
        service.start(); // Khởi động service
    }

    public static void main(String[] args) {
        launch(args); // Khởi động JavaFX Application
    }
}

class HeavyService extends Service<Void> {
    @Override
    protected Task<Void> createTask() {
        return new MyTask();
    }

    public static class MyTask extends Task<Void> {
        @Override
        protected Void call() throws Exception {
            // Tác vụ nặng tại đây
            System.out.println("Đang thực hiện tác vụ nặng...");
            // Bạn có thể thực hiện các tác vụ của mình tại đây
            Thread.sleep(50); // Giả sử là tác vụ nặng kéo dài 2 giây
            return null;
        }
    }
}
