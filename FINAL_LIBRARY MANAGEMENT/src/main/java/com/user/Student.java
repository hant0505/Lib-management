package com.user;

public class Student extends User {
    public Student(String username, String name, String phone, String email) {
        super(username, name, phone, email);
        this.setRole("STUDENT");
    }

}
