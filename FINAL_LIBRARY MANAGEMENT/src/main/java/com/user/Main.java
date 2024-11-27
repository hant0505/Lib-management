package com.user;

import com.book.QRHttpServer;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

@SuppressWarnings("CallToPrintStackTrace")
public class Main extends Application {
    private Thread serverThread;

    public static void main(String[] ignoredArgs) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Load FXML
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/user/sign_in.fxml")));
        // Set stage properties
        stage.setTitle("Library");
        stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/com/Image/logo.png"))));
        stage.setResizable(false);

        // Set scene with stylesheet
        Scene scene = new Scene(root);
        stage.setScene(scene);

        // Show stage
        stage.show();

        // In thông báo rằng ứng dụng đã bắt đầu
        System.out.println("Application started.");
    }

    // Sau khi đăng nhập thành công, chuyển đến màn hình splash
    public void showSplashScreen(Stage stage) {
        stage.setScene(createSplashStage().getScene());
        stage.show();

        // Tạo worker để tải dữ liệu sách
        Task<Void> loadBookDataTask = createWorker();
        // Khi tải xong, chuyển đến trang home
        loadBookDataTask.setOnSucceeded(_ -> {
            try {
                if (DBUtils.loggedInRole.equals("STUDENT")) {
                    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/home_student.fxml")));
                    Scene scene = new Scene(root);
                    stage.setScene(scene); // Thay thế scene của splash thành của home
                    stage.setTitle("Home");
                    stage.setResizable(false);
                }
                if (DBUtils.loggedInRole.equals("LIBRARIAN")) {
                    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/home_librarian.fxml")));
                    Scene scene = new Scene(root);
                    stage.setScene(scene); // Thay thế scene của splash thành của home
                    stage.setTitle("Home");
                    stage.setResizable(false);

                    // Khởi động server HTTP sau khi chuyển đến màn hình LIBRARIAN
                    startServer();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Bắt đầu worker
        new Thread(loadBookDataTask).start();
    }

    // Tạo màn hình splash
    private Stage createSplashStage() {
        Stage splashStage = new Stage();
        splashStage.setTitle("Loading Screen");

        try {
            FXMLLoader splashLoader = new FXMLLoader(getClass().getResource("/com/loading.fxml"));
            Parent splashRoot = splashLoader.load();
            Scene splashScene = new Scene(splashRoot);

            splashStage.setScene(splashScene);
            splashStage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return splashStage;
    }

    // Worker để tải dữ liệu sách
    private Task<Void> createWorker() {
        return new Task<>() {
            @Override
            protected Void call() {
                updateMessage("Initializing...");
                updateProgress(0, 100);

                try {
                    for (int i = 1; i <= 100; i++) {
                        Thread.sleep(50); // Giả lập việc tải dữ liệu trong 5 giây
                        updateProgress(i, 100);
                        updateMessage("Loading... " + i + "%");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                updateMessage("Tải xong sách");
                return null;
            }
        };
    }

    // Phương thức để khởi động server
    private void startServer() {
        if (serverThread != null && serverThread.isAlive()) {
            System.out.println("Server is already running.");
            return;
        }

        serverThread = new Thread(() -> {
            try {
                QRHttpServer.main(new String[]{});  // Khởi chạy server ở cổng 8080
                System.out.println("HTTP Server started.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.setDaemon(true); // Đảm bảo thread không ngăn ứng dụng đóng
        serverThread.start();
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        // Dừng server HTTP
        QRHttpServer.stopServer(); // Dừng server nếu có triển khai hàm này
        // Dừng thread server nếu cần
        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
            System.out.println("Server thread interrupted.");
        }
    }
}
