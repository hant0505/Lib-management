package com.book;

import com.DatabaseConnection;
import com.effect.AlertUtils;
import com.user.DBUtils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

@SuppressWarnings("CallToPrintStackTrace")
public class StudentTransactionsController implements Initializable {
    static ObservableList<Transaction> transactionsList;
    @FXML
    private Button button_home;
    @FXML
    private Button button_booksForStu;
    @FXML
    private Button button_game;
    @FXML
    private Button button_account;
    @FXML
    private TextField tf_search;
    @FXML
    private Label label_coin;
    @FXML
    private TableView<Transaction> transTableViewForStu;
    @FXML
    private TableColumn<Transaction, Integer> table_transID;
    @FXML
    private TableColumn<Transaction, String> table_bookTitle;
    @FXML
    private TableColumn<Transaction, LocalDate> table_borrowedDate;
    @FXML
    private TableColumn<Transaction, LocalDate> table_dueDate;
    @FXML
    private TableColumn<Transaction, Integer> table_lateFee;
    @FXML
    private TableColumn<Transaction, ImageView> table_qr;
    @FXML
    private TableColumn<Transaction, Button> table_extendDueDate;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        table_transID.setCellValueFactory(new PropertyValueFactory<>("transactionID"));
        table_bookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        table_borrowedDate.setCellValueFactory(new PropertyValueFactory<>("borrowedDate"));
        table_dueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        table_qr.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(ImageView item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(item);
                }
            }
        });
        table_qr.setCellValueFactory(data -> {
            String qrCodePath = data.getValue().getQrCodePath();
            ImageView qrImageView = null;
            if (qrCodePath != null && !qrCodePath.isEmpty()) {
                try {
                    // Dùng đường dẫn tuyệt đối
                    String imagePath = System.getProperty("user.dir") + "/" + qrCodePath;
                    Image qrImage = new Image("file:" + imagePath);
                    qrImageView = new ImageView(qrImage);
                    qrImageView.setFitWidth(50); // Điều chỉnh kích thước ảnh
                    qrImageView.setFitHeight(50);
                } catch (Exception e) {
                    System.err.println("Không thể tải ảnh: " + qrCodePath);
                    e.printStackTrace();
                }
            }
            return new SimpleObjectProperty<>(qrImageView);
        });


        // chậm 1 ngày tính thêm 10k
        table_lateFee.setCellValueFactory(data -> {
            LocalDate dueDate = data.getValue().getDueDate();
            if (dueDate != null && LocalDate.now().isAfter(dueDate)) {
                // Tính số ngày bị trễ
                long daysPast = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
                return new SimpleObjectProperty<>((int) daysPast * 10000);
            }
            return new SimpleObjectProperty<>(0);
        });

        table_extendDueDate.setCellFactory(_ -> new TableCell<>() {
            private final Button button = new Button("Extend");

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null); // Nếu cell rỗng thì không hiển thị gì
                } else {
                    // Gán hành động cho nút
                    button.setOnAction(_ -> {
                        Transaction transaction = getTableView().getItems().get(getIndex());
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/book/extend_trans.fxml"));
                            Parent root = loader.load();

                            ExtendTransController controller = loader.getController();
                            controller.setTransactionDueDate(transaction);

                            controller.setParentController(StudentTransactionsController.this);


                            Stage stage = new Stage();
                            stage.setTitle("Extend Transaction");
                            stage.setScene(new Scene(root));
                            stage.setResizable(false);
                            //update coin ngay sau khi sub
                            stage.setOnHidden(_ -> {
                                updateCoinLabel();
                                updateTable();
                            });

                            controller.setStage(stage);
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    // Đặt nút vào trong cell
                    setGraphic(button);
                }
            }
        });


        transactionsList = FXCollections.observableArrayList();
        transTableViewForStu.setItems(transactionsList);

        loadData();
        searchTransaction();
        label_coin.setText(String.valueOf(DBUtils.getCoins(DBUtils.loggedInUser)));

        transTableViewForStu.setRowFactory(_ -> {
            TableRow<Transaction> row = new TableRow<>();
            // Tạo Tooltip với nội dung hướng dẫn
            Tooltip tooltip = new Tooltip("Double-click to download the QR code");
            row.setOnMouseEntered(_ -> {
                // Chỉ hiển thị Tooltip nếu dòng không trống
                if (!row.isEmpty()) {
                    Tooltip.install(row, tooltip);
                } else Tooltip.uninstall(row, tooltip);
            });
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) { // Bấm đúp chuột
                    Transaction clickedTransaction = row.getItem();  // Lấy item được chọn
                    downloadQRCode(clickedTransaction.getQrCodePath()); // Gọi hàm tải ảnh
                }
            });
            return row;
        });


        button_home.setOnAction(event -> com.user.DBUtils.changeScene(event, "/com/home_student.fxml", "Library"));
        button_game.setOnAction(event -> com.user.DBUtils.changeScene(event, "/com/game/game.fxml", "Game"));
        button_booksForStu.setOnAction(event -> DBUtils.changeScene(event, "/com/book/bookInfo_stu.fxml", "Books"));
        button_account.setOnAction(event -> com.user.DBUtils.changeScene(event, "/com/user/account_info_stu.fxml", "Account"));
    }

    private void loadData() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("select transactionID, bookTitle, borrowedDate, dueDate, qrCodePath from transactions where username = ? and returnedDate is null");
            ps.setString(1, DBUtils.loggedInUser);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                transactionsList.add(new Transaction(rs.getInt(1), rs.getString(2), rs.getDate(3).toLocalDate(), rs.getDate(4).toLocalDate(), rs.getString(5)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadQRCode(String qrCodePath) {
        if (qrCodePath == null || qrCodePath.isEmpty()) {
            System.err.println("QR Code path is empty or null");
            return;
        }

        try {
            // Đường dẫn file gốc
            String sourcePath = System.getProperty("user.dir") + "/" + qrCodePath;

            // Kiểm tra file QR Code có tồn tại không
            Path path = Paths.get(sourcePath);
            if (!Files.exists(path)) {
                AlertUtils.errorAlert("QR Code not existed: " + qrCodePath);
                return;
            }

            // Sử dụng FileChooser để chọn nơi lưu file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose file to save QR Code");
            fileChooser.setInitialFileName(Paths.get(qrCodePath).getFileName().toString()); // Tên mặc định của file

            // Đặt bộ lọc file để chỉ hiển thị các file ảnh
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );
            // Hiển thị hộp thoại lưu file
            File selectedFile = fileChooser.showSaveDialog(null);
            if (selectedFile != null) {
                // Sao chép file đến vị trí đã chọn
                Files.copy(path, selectedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                // Thông báo thành công
                AlertUtils.infoAlert("QR Code saved at: " + selectedFile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();

            AlertUtils.errorAlert("Cannot download QR Code. Please check log!");
        }
    }

    public void searchTransaction() {
        FilteredList<Transaction> filteredData = new FilteredList<>(transactionsList, _ -> true);
        tf_search.textProperty().addListener((_, _, newValue) -> filteredData.setPredicate(b -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();
            if (b.getBookTitle().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else return String.valueOf(b.getTransactionID()).contains(lowerCaseFilter);
        }));
        SortedList<Transaction> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(transTableViewForStu.comparatorProperty());
        transTableViewForStu.setItems(sortedData);
    }

    public void updateTable() {
        // Làm mới danh sách giao dịch và cập nhật lại bảng
        transactionsList.clear();
        loadData();
        transTableViewForStu.refresh();
    }


    public void updateCoinLabel() {
        label_coin.setText(String.valueOf(DBUtils.getCoins(DBUtils.loggedInUser)));
    }


}
