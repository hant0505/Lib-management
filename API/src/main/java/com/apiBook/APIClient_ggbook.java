package com.apiBook;

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

    public List<API_Book> getBooks(String query) {
        List<API_Book> bookList = new ArrayList<>();
        try {
            // Kiểm tra nếu query rỗng
            if (query == null || query.trim().isEmpty()) {
                System.out.println("Vui lòng nhập từ khóa tìm kiếm!");
                return bookList; // Trả về danh sách rỗng nếu không có từ khóa
            }

            String apiKey = "AIzaSyCOXgIRt1bEFAELVodG6PuJ3RzLcTaGPA8"; // API Key của bạn
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString()); // Mã hóa chuỗi truy vấn
            String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=" + encodedQuery + "&key=" + apiKey + "&maxResults=30";

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
                        String id = item.get("id").asText();
                        String title = volumeInfo.get("title").asText();
                        String author = volumeInfo.has("authors") ? volumeInfo.get("authors").get(0).asText() : "Unknown";
                        String publishedDate = volumeInfo.has("publishedDate") ? volumeInfo.get("publishedDate").asText() : "Unknown";
                        String isbn = ""; // Default value, change based on actual ISBN format or lookup
                        String description = volumeInfo.has("description") ? volumeInfo.get("description").asText() : "No description available";


                        // Extract ISBN
                        JsonNode industryIdentifiers = volumeInfo.get("industryIdentifiers");
                        if (industryIdentifiers != null && industryIdentifiers.isArray()) {
                            for (JsonNode identifier : industryIdentifiers) {
                                String type = identifier.get("type").asText();
                                if ("ISBN_13".equals(type)) {
                                    isbn = identifier.get("identifier").asText();
                                }
                            }
                        }

                        // Extract categories
                        String categories = "No categories available";
                        if (volumeInfo.has("categories")) {
                            JsonNode categoriesArray = volumeInfo.get("categories");
                            List<String> categoriesList = new ArrayList<>();
                            categoriesArray.forEach(categoryNode -> categoriesList.add(categoryNode.asText()));
                            categories = String.join(", ", categoriesList); // Nối các category thành chuỗi
                        }

                        int quantity = 1; // Giả sử mỗi sách có 1 bản; chỉnh sửa theo logic của bạn
                        String available = "Có sẵn"; // Giả sử mặc định sách có sẵn; chỉnh sửa theo logic của bạn

                        // Tạo đối tượng Book với tiêu đề và tác giả
                        //API_Book book = new API_Book(id, title, author, publishedDate, isbn, description);
                        //API_Book book = new API_Book(isbn, title, author,categories, quantity, available);
                        API_Book book = new API_Book(isbn, title, author,categories, quantity, available);

                        bookList.add(book); // Thêm vào danh sách sách

                        // In thông tin sách ra console
//                        System.out.println("Book ID: " + id);
//                        System.out.println("Title: " + title);
//                        System.out.println("Author(s): " + author);
//                        System.out.println("Published Date: " + publishedDate);
//                        System.out.println("ISBN: " + isbn);
//                        System.out.println("Description: " + description);
//                        System.out.println("---------");

                        System.out.println("ISBN: " + isbn);
                        System.out.println("Title: " + title);
                        System.out.println("Author(s): " + author);
                        System.out.println("Thể loại: " + categories);
                        System.out.println("Quantity: " + quantity);
                        System.out.println("Available: " + available);
                        System.out.println("---------");
                    });
                } else {
                    System.out.println("Không tìm thấy sách nào với từ khóa: " + query);
                }
            } else {
                System.out.println("Yêu cầu không thành công, mã trạng thái: " + statusCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookList;
    }

    public static void main(String[] args) {
        APIClient_ggbook apiClient = new APIClient_ggbook();
        String query = "harry potter"; // Từ khóa tìm kiếm ví dụ
        List<API_Book> books = apiClient.getBooks(query);
        System.out.println("Số sách tìm thấy: " + books.size());
        // Hiển thị kết quả
        books.forEach(book -> System.out.println("Title: " + book.getTitle() + ", Author: " + book.getAuthor()));
    }
}
