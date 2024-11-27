package com.example.lib;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static javafx.application.Application.launch;

public class APIclient_ggbook extends Application {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String apiKey = "AIzaSyCOXgIRt1bEFAELVodG6PuJ3RzLcTaGPA8"; // Thay API Key

    // lấy sách từ API Google Books
    public List<Book> getBooksByISBN(String isbn) {
        List<Book> bookList = new ArrayList<>();
        try {
            // Kiểm tra nếu ISBN rỗng
            if (isbn == null || isbn.trim().isEmpty()) {
                System.out.println("Vui lòng nhập ISBN!");
                return bookList;
            }

            String encodedISBN = URLEncoder.encode("isbn:" + isbn, StandardCharsets.UTF_8.toString());
            String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=" + encodedISBN + "&key=" + apiKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode == 200) {
                String responseBody = response.body();
                ///CHECK
                // System.out.println("Nội dung phản hồi: " + responseBody);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);

                if (jsonNode.has("items")) {
                    jsonNode.get("items").forEach(item -> {
                        JsonNode volumeInfo = item.get("volumeInfo");
                        String title = volumeInfo.has("title") ? volumeInfo.get("title").asText() : "Unknown Title";
                        String author = volumeInfo.has("authors") ? volumeInfo.get("authors").get(0).asText() : "Unknown Author";
                        String description = volumeInfo.has("description") ? volumeInfo.get("description").asText() : "No Description";
                        String category = volumeInfo.has("categories") ? String.join(", ", getCategories(volumeInfo.get("categories"))) : "No Categories";
                        int quantity = 1;

                        String imagePath = volumeInfo.path("imageLinks").path("thumbnail").asText();
                        //System.out.println(imagePath); ///DEBUG
//                        if (imagePath == null) {
//                            imagePath = "com/example/lib/bia-sach-harry-potter-va-cai-dit-con-me-may.jpg"; // Sử dụng ảnh mặc định
//                        }

//                        ImageView coverImageView = new ImageView(new Image(imagePath));
//                        coverImageView.setFitWidth(100);
//                        coverImageView.setPreserveRatio(true);

                        // Lấy năm xuất bản - 4 ký tự
                        String publishYear = volumeInfo.has("publishedDate")
                                ? volumeInfo.get("publishedDate").asText().substring(0, 4)
                                : "Unknown Year";

                        //System.out.println(volumeInfo.toString());

                        // Tạo đối tượng Book
                        Book book = new Book(isbn, title, author, Integer.parseInt(publishYear), quantity, description, category,imagePath);
                        book.setDescription(description);
                        bookList.add(book);

                        String selfLink = item.has("selfLink") ? item.get("selfLink").asText() : "No SelfLink";
                        System.out.println("Chạy API-ing"); ///DEBUG
                        System.out.println("link: " + selfLink);
                        System.out.println("image: " + imagePath);

                    });


                } else {
                    System.out.println("Không tìm thấy sách nào với ISBN: " + isbn);
                }
            } else {
                System.out.println("Yêu cầu không thành công. Mã trạng thái: " + statusCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookList;
    }

    // lấy danh sách thể loại
    private List<String> getCategories(JsonNode categoriesNode) {
        List<String> categories = new ArrayList<>();
        categoriesNode.forEach(category -> categories.add(category.asText()));
        return categories;
    }

    /**
     * In ra thử.
     * @param primaryStage pr
     */
    @Override
    public void start(Stage primaryStage) {
        // Chạy ứng dụng JavaFX
        String isbn = "9780747542155"; // Ví dụ ISBN của một sách
        // 9786041219212 9786041198845 9780439139595
        List<Book> books = getBooksByISBN(isbn);
        books.forEach(book -> {
            System.out.println("Tiêu đề: " + book.getTitle());
            System.out.println("Tác giả: " + book.getAuthor());
            System.out.println("Năm xuất bản:" + book.getPublishYear());
            System.out.println("Thể loại: " + book.getCategory());  // In ra thể loại
            System.out.println("Mô tả: " + book.getDescription()); // In ra mô tả
            System.out.println("Ảnh bìa: " + book.getImagePath()); // In ra mô tả
            System.out.println("-----------");
        });
    }

    // Khởi tạo JavaFX ứng dụng
    public static void main(String[] args) {
        launch(args);
    }

}
