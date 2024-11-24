package com.example.lib;
import java.net.InetAddress;

public class ServerIP {
    public static String getServerIP() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress(); // Lấy IP của máy
        } catch (Exception e) {
            e.printStackTrace();
            return "localhost"; // Fallback nếu lỗi
        }
    }
}
