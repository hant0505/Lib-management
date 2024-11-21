package com.example.lib;
import javafx.scene.control.CheckBox;


public class Book {
    private String title;
    private String author;
    private int publishYear;
    private int quantity;
    private String isbn;
    private String description;
    private String category;
    private CheckBox checkBox;
    private Boolean isAvailable;
    private String imagePath;

    public Book(String isbn, String title, String author, int publishYear, int quantity, String description, String category, CheckBox checkBox, Boolean isAvailable, String imagePath) {
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

    public Book(String isbn, String title, String author, int publishYear, int quantity, String category) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publishYear = publishYear;
        this.quantity = quantity;
        this.category = category;
        this.checkBox = new CheckBox();
    }

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        updateAvailability(); // Cập nhật trạng thái 'available' khi quantity thay đổi
    }

    private void updateAvailability() {
        this.isAvailable = this.quantity > 0; // Đồng bộ với Boolean isAvailable
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

}
