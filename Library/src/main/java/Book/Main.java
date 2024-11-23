package Book;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

@SuppressWarnings("CallToPrintStackTrace")
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Stage splashStage = createSplashStage();
        splashStage.show();

        // Tạo worker để tải dữ liệu sách
        Task<Void> loadBookDataTask = createWorker();

        // check xem chạy được real không
        loadBookDataTask.messageProperty().addListener((_, _, newValue) -> System.out.println(newValue));
        loadBookDataTask.progressProperty().addListener((_, _, newValue) -> {
            System.out.printf("Progress: %.2f%%\n", newValue.doubleValue() * 100); // In tiến trình
        });

        // chỉ DO khi tải xong dữ liệu
        loadBookDataTask.setOnSucceeded(_ -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Book/bookInfo.fxml"));
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/Book/Style.css").toExternalForm());
                splashStage.setScene(scene); // thay thế scene của splash thành của home
                splashStage.setTitle("Book Info");
                splashStage.setResizable(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        new Thread(loadBookDataTask).start();
    }

    private Stage createSplashStage() {
        Stage splashStage = new Stage();
        splashStage.setTitle("Loading Screen");

        try {
            FXMLLoader splashLoader = new FXMLLoader(getClass().getResource("/Animation/SplashScreen.fxml"));
            Parent splashRoot = splashLoader.load();
            Scene splashScene = new Scene(splashRoot);

            splashStage.setScene(splashScene);
            splashStage.setResizable(false);
            splashStage.setWidth(1300);
            splashStage.setHeight(700);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return splashStage;
    }

    private Task<Void> createWorker() {
        return new Task<>() {
            @Override
            protected Void call() {
                // check
                updateMessage("Initializing...");
                updateProgress(0, 100);

                try {
                    for (int i = 1; i <= 100; i++) {
                        Thread.sleep(50); // coi như tải dữ liệu trong 5s
                        updateProgress(i, 100);
                        updateMessage("Loading... " + i + "%");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                updateMessage("Tải xong sách r ");
                return null;
            }
        };
    }

    public static void main(String[] args) {
        launch();
    }
}

