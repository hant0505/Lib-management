package com.book;

import javafx.scene.control.CheckBox;


public class Book {

    private String isbn;
    private String title;
    private String author;
    private int quantity;
    private String category;
    private String description;
    private int publishYear;
    private String imagePath;
    private byte[] image;
    private Boolean isAvailable;
    private CheckBox checkBox;


    public Book(String isbn, String title, String author, int publishYear, int quantity, String description, String category, Boolean isAvailable, String imagePath) {
        this.title = title;
        this.author = author;
        this.publishYear = publishYear;
        this.quantity = quantity;
        this.isbn = isbn;
        this.description = description;
        this.category = category;
        this.checkBox = new CheckBox();
        this.isAvailable = isAvailable;
        this.imagePath = imagePath;
    }

    /// use in BookInfo
    public Book(String isbn, String title, String author, int publishYear, String category, int quantity) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publishYear = publishYear;
        this.category = category;
        this.quantity = quantity;
        this.checkBox = new CheckBox();
    }

    /// use in API_BOOK
    public Book(String isbn, String title, String author, int publishYear, int quantity, String description, String category) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publishYear = publishYear;
        this.quantity = quantity;
        this.category = category;
        this.description = description;
        this.checkBox = new CheckBox();
    }

    /// use in API_BOOK with IMAGE PATH
    public Book(String isbn, String title, String author, int publishYear, int quantity, String description, String category, String imagePath) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publishYear = publishYear;
        this.quantity = quantity;
        this.category = category;
        this.description = description;
        this.imagePath = imagePath;
        this.checkBox = new CheckBox();
    }

    public Book(String isbn, String title, String author, String category, int quantity, boolean isAvailable, String description) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.quantity = quantity;
        this.isAvailable = isAvailable;
        this.description = description;
        this.checkBox = new CheckBox();
    }

    /// DELETE WITH ISBN
    public Book(String isbn) {
        this.isbn = isbn;
    }

    /// getBooks DBUtils
    public Book(String isbn, String title, String author, int publishYear, String category, int quantity, String imagePath) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publishYear = publishYear;
        this.category = category;
        this.quantity = quantity;
        this.imagePath = imagePath;
        this.checkBox = new CheckBox();
    }

    public Book(String isbn, String title, String author, int publishYear, int quantity, String description, String category, byte[] image) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publishYear = publishYear;
        this.quantity = quantity;
        this.description = description;
        this.category = category;
        this.image = image;
        this.checkBox = new CheckBox();
    }

    public Book(String title, String imagePath) {
        this.title = title;
        this.imagePath = imagePath;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        updateAvailability();// Cập nhật trạng thái 'available' khi quantity thay đổi
    }

    private void updateAvailability() {
        this.setAvailable(this.getQuantity() > 0); // Đồng bộ với Boolean isAvailable
    }

    public Boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        this.isAvailable = available;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(int publishYear) {
        this.publishYear = publishYear;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
