package com.example.lib;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class QRHttpServer {
    public static void main(String[] args) throws IOException {
        // Tạo server HTTP lắng nghe trên cổng 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        //HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8080), 0);
        server.createContext("/qrs", new QRHandler());  // Định nghĩa đường dẫn /qrs
        server.setExecutor(null); // Sử dụng executor mặc định
        server.start();
        System.out.println("Server started on port 8080");
    }
}

class QRHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Lấy dữ liệu từ URL (dưới dạng query string)
        String query = exchange.getRequestURI().getQuery();
        String data = query.split("=")[1];  // Lấy giá trị sau dấu "="
        //Mã hóa lại
        //String decodedData = URLDecoder.decode(query, StandardCharsets.UTF_8);
        // In ra dữ liệu QR và kiểu dữ liệu của nó
        System.out.println("QR Data: " + data);
        System.out.println("Type of QR Data: " + data.getClass().getName());


        // Gọi phương thức scan để xử lý và định dạng dữ liệu
        Qr_app.scan(data);
        // Gọi phương thức showQRCodePopup() để hiển thị pop-up trong JavaFX
        //Qr_app.showQRCodePopup(formattedData);

        // Trả lời cho client (có thể là điện thoại)
        String response = "QR Data Received";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
