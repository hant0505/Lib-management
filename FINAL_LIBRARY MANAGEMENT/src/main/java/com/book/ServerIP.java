package com.book;

import java.net.InetAddress;

@SuppressWarnings("CallToPrintStackTrace")
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
