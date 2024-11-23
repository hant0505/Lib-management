package Book;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class UpdateBookController implements Initializable {
    @FXML
    private TextField tf_bookTitle_update;

    @FXML
    private TextField tf_bookAuthor_update;

    @FXML
    private TextField tf_bookPublishYear_update;

    @FXML
    private Button button_updateBook;

    private Book currentBook;

    public void setBook(Book book) {
        this.currentBook = book;
        tf_bookTitle_update.setText(book.getTitle());
        tf_bookAuthor_update.setText(book.getAuthor());
        tf_bookPublishYear_update.setText(Integer.toString(book.getPublishYear()));
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_updateBook.setOnAction(e -> {
            String title = tf_bookTitle_update.getText();
            String author = tf_bookAuthor_update.getText();
            int publishYear = Integer.parseInt(tf_bookPublishYear_update.getText());
            currentBook.setTitle(title);
            currentBook.setAuthor(author);
            currentBook.setPublishYear(publishYear);

            DBUtils.updateBookFromDB(currentBook,e);

            Stage stage = (Stage) button_updateBook.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        });

    }
}

