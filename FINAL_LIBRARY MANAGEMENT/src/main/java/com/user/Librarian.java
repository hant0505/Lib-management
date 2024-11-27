package com.user;

public class Librarian extends User {

    public Librarian(String username, String name, String phone, String email) {
        super(username, name, phone, email);
        this.setRole("LIBRARIAN");
    }
}
