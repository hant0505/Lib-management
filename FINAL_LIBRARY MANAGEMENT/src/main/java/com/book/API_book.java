package com.book;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("CallToPrintStackTrace")
public class API_book {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String apiKey = "AIzaSyCOXgIRt1bEFAELVodG6PuJ3RzLcTaGPA8";

    public List<Book> getBooksByISBN(String isbn) {
        List<Book> bookList = new ArrayList<>();
        try {
            if (isbn == null || isbn.trim().isEmpty()) {
                return bookList;
            }

            String encodedISBN = URLEncoder.encode("isbn:" + isbn, StandardCharsets.UTF_8);
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
                        String category = volumeInfo.has("categories") ? String.join(", ", getCategories(volumeInfo.get("categories"))) : "No Categories";
                        int quantity = 1;

                        String imagePath = volumeInfo.path("imageLinks").path("thumbnail").asText();
                        String publishYear = volumeInfo.has("publishedDate")
                                ? volumeInfo.get("publishedDate").asText().substring(0, 4)
                                : "Unknown Year";

                        Book book = new Book(isbn, title, author, Integer.parseInt(publishYear), quantity, description, category, imagePath);
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

    private List<String> getCategories(JsonNode categoriesNode) {
        List<String> categories = new ArrayList<>();
        categoriesNode.forEach(category -> categories.add(category.asText()));
        return categories;
    }
}
