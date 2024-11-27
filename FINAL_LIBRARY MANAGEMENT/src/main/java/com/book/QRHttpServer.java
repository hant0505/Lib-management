package com.book;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class QRHttpServer {
    private static HttpServer server;

    public static void main(String[] ignoredArgs) throws IOException {
        // Tạo server HTTP lắng nghe trên cổng 8080
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/qrs", new QRHandler());  // Định nghĩa đường dẫn /qrs
        server.setExecutor(null); // Sử dụng executor mặc định
        server.start();
        System.out.println("Server started on port 8080");
    }

    // Phương thức để dừng server
    public static void stopServer() {
        if (server != null) {
            server.stop(0); // Dừng server ngay lập tức
            System.out.println("Server stopped.");
        }
    }
}

class QRHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Lấy dữ liệu từ URL (dưới dạng query string)
        String query = exchange.getRequestURI().getQuery();
        String data = query.split("=")[1];  // Lấy giá trị sau dấu "="

        // Gọi phương thức scan để xử lý và định dạng dữ liệu
        QR_SCAN.scan(data);
        // Trả lời cho client (có thể là điện thoại)
        String response = "QR Data Received";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
