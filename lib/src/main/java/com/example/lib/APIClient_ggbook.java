package com.example.lib;

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

public class APIClient_ggbook {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public List<Book> getBooks(String query) {
        List<Book> bookList = new ArrayList<>();
        try {
            String apiKey = "AIzaSyCOXgIRt1bEFAELVodG6PuJ3RzLcTaGPA8"; // API Key của bạn
            //String query = "java programming"; // Từ khóa tìm kiếm sách
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString()); // Mã hóa chuỗi truy vấn
            String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=" + encodedQuery + "&key=" + apiKey + "&maxResults=5";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            System.out.println("Mã trạng thái: " + statusCode);

            if (statusCode == 200) {
                String responseBody = response.body();
                System.out.println("Nội dung phản hồi: " + responseBody);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);

                // Kiểm tra nếu có mục (items) hay không
                if (jsonNode.has("items")) {
                    jsonNode.get("items").forEach(item -> {
                        JsonNode volumeInfo = item.get("volumeInfo");
                        String title = volumeInfo.get("title").asText();
                        String author = volumeInfo.has("authors") ? volumeInfo.get("authors").get(0).asText() : "Unknown";

                        Book book = new Book(title, author); // Tạo đối tượng Book với tiêu đề và tác giả
                        bookList.add(book); // Thêm vào danh sách sách

                        //System.out.println("Title: " + title);
                        //System.out.println("Author: " + author);
                        //System.out.println("---------");
                    });
                } else {
                    //System.out.println("Không tìm thấy sách nào với từ khóa: " + query);
                }
            } else {
                //.out.println("Yêu cầu không thành công, mã trạng thái: " + statusCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookList;
    }

    public static void main(String[] args) {
        APIClient_ggbook apiClient = new APIClient_ggbook();
        String query = "Java"; // Bạn có thể thay đổi từ khóa tìm kiếm
        List<Book> books = apiClient.getBooks(query);

        // Hiển thị kết quả
        books.forEach(book -> System.out.println("Title: " + book.getTitle() + ", Author: " + book.getAuthor()));
    }
}
