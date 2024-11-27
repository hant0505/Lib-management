package com.book;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class APIclient_ggbook {
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
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);

                if (jsonNode.has("items")) {
                    jsonNode.get("items").forEach(item -> {
                        JsonNode volumeInfo = item.get("volumeInfo");
                        String title = volumeInfo.has("title") ? volumeInfo.get("title").asText() : "Unknown Title";
                        String author = volumeInfo.has("authors") ? volumeInfo.get("authors").get(0).asText() : "Unknown Author";
                        String description = volumeInfo.has("description") ? volumeInfo.get("description").asText() : "No Description";
                        String categories = volumeInfo.has("categories") ? String.join(", ", getCategories(volumeInfo.get("categories"))) : "No Categories";
                        int quantity = 1;

                        // Tạo đối tượng API_Book
                        Book book = new Book(isbn, title, author, categories, quantity, "Available");
                        book.setDescription(description);
                        bookList.add(book);
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

    // lấy danh sách thể loại từ JSON
    private List<String> getCategories(JsonNode categoriesNode) {
        List<String> categories = new ArrayList<>();
        categoriesNode.forEach(category -> categories.add(category.asText()));
        return categories;
    }

    public static void main(String[] args) {
        APIclient_ggbook apiClient = new APIclient_ggbook();
        String isbn = "9780439139595"; // Ví dụ ISBN của một sách
        List<Book> books = apiClient.getBooksByISBN(isbn);
        books.forEach(book -> {
            System.out.println("Tiêu đề: " + book.getTitle());
            System.out.println("Tác giả: " + book.getAuthor());
            System.out.println("Thể loại: " + book.getCategories());
            System.out.println("Mô tả: " + book.getDescription());
            System.out.println("-----------");
        });
    }
}
