package com.attendance;

import java.time.LocalDateTime;
import java.util.Date;

public class Attendance {
    private int id;
    private String username;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Date date;
    private String status;

    public Attendance(int id, String username, LocalDateTime checkIn, LocalDateTime checkOut, Date date, String status) {
        this.id = id;
        this.username = username;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.date = date;
        this.status = status;
    }

    public Attendance(String username, LocalDateTime checkIn, LocalDateTime checkOut, Date date, String status) {
        this.username = username;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.date = date;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDateTime checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDateTime getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDateTime checkOut) {
        this.checkOut = checkOut;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

