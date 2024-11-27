package com.book;

import com.DatabaseConnection;
import com.effect.AlertUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static com.book.DBUtils.extendTransactions;

@SuppressWarnings("CallToPrintStackTrace")
public class ExtendTransController implements Initializable {
    @FXML
    private Label label_coin;
    @FXML
    private Label label_newDueDate;

    @FXML
    private ChoiceBox<Integer> cb_days;

    @FXML
    private Button button_submit;

    private Stage stage; // Tham chiếu đến Stage để đóng cửa sổ
    private int transactionID;
    private StudentTransactionsController parentController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        label_coin.setText(String.valueOf(com.user.DBUtils.getCoins(com.user.DBUtils.loggedInUser)));
        cb_days.getItems().addAll(1, 2, 3, 4, 5, 6, 7);
        cb_days.setValue(null);
        button_submit.setOnAction(_ -> handleSubmit());
    }

    public void setTransactionDueDate(Transaction transaction) {
        cb_days.setOnAction(_ -> {
            int selectedDays = cb_days.getValue();
            LocalDate newDueDate = transaction.getDueDate().plusDays(selectedDays);
            transactionID = transaction.getTransactionID();
            label_newDueDate.setText("New Due Date:\n" + newDueDate);
        });
    }

    @FXML
    private void handleSubmit() {
        int selectedDays = cb_days.getValue();
        int maxDays = 0;
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("select maxExtendDays from transactions where transactionID = ?");
            ps.setInt(1, transactionID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                maxDays = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (maxDays - selectedDays < 0) {
            AlertUtils.errorAlert("Cannot extend due date!");
            return;
        } else {
            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("update transactions set maxExtendDays = ? where transactionID = ?");
                ps.setInt(1, maxDays - selectedDays);
                ps.setInt(2, transactionID);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        int currentCoins = com.user.DBUtils.getCoins(com.user.DBUtils.loggedInUser);
        // Kiểm tra nếu người dùng đủ coin
        int requiredCoins = selectedDays * 50; // Mỗi ngày gia hạn mất 50 coin
        if (currentCoins < requiredCoins) {
            AlertUtils.errorAlert("Not enough coins!");
            return;
        }
        try {
            extendTransactions(transactionID, cb_days.getValue());
            if (parentController != null) {
                parentController.updateTable();
                parentController.updateCoinLabel();
            }
            AlertUtils.infoAlert("Transaction extends successfully!");
            handleClose();
        } catch (Exception e) {
            label_newDueDate.setText("Có lỗi xảy ra khi gia hạn giao dịch!");
            e.printStackTrace();
        }
    }


    @FXML
    private void handleClose() {
        if (stage != null) {
            stage.close();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setParentController(StudentTransactionsController parentController) {
        this.parentController = parentController;
    }

}
