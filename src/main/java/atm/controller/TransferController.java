package atm.controller;
import atm.App;
import atm.model.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;

public class TransferController {

    @FXML private ChoiceBox<String> fromAccountBox;
    @FXML private ChoiceBox<String> toAccountBox;
    @FXML private TextField amountField;
    @FXML private Button backButton;
    @FXML private Label statusLabel;

    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = App.loggedInUser;
        if (currentUser != null) {
            ObservableList<String> accounts = FXCollections.observableArrayList();
            for (int i = 0; i < currentUser.numAccounts(); i++) {
                accounts.add(currentUser.getAccountSummaryLine(i));
            }
            fromAccountBox.setItems(accounts);
            toAccountBox.setItems(accounts);
        }
    }

    @FXML
    private void handleTransferAction() {
        int fromIdx = fromAccountBox.getSelectionModel().getSelectedIndex();
        int toIdx = toAccountBox.getSelectionModel().getSelectedIndex();

        if (fromIdx == -1 || toIdx == -1 || amountField.getText().isEmpty()) {
            statusLabel.setText("> ОШИБКА: ВСЕ ПОЛЯ ДОЛЖНЫ БЫТЬ ЗАПОЛНЕНЫ");
            return;
        }

        if (fromIdx == toIdx) {
            statusLabel.setText("> ОШИБКА: НЕЛЬЗЯ ПЕРЕВЕСТИ НА ТОТ ЖЕ СЧЕТ");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            statusLabel.setText("> ОШИБКА: СУММА ДОЛЖНА БЫТЬ ЧИСЛОМ");
            return;
        }

        if (amount <= 0) {
            statusLabel.setText("> ОШИБКА: СУММА ДОЛЖНА БЫТЬ ПОЛОЖИТЕЛЬНОЙ");
            return;
        }

        if (amount > currentUser.getAcctBalance(fromIdx)) {
            statusLabel.setText("> ОШИБКА: НЕДОСТАТОЧНО СРЕДСТВ");
            return;
        }

        currentUser.addAcctTransaction(fromIdx, -amount, "Перевод на счет " + currentUser.getAcctUUID(toIdx));
        currentUser.addAcctTransaction(toIdx, amount, "Перевод со счета " + currentUser.getAcctUUID(fromIdx));

        App.showAlert("УСПЕХ", String.format("СУММА %.2f руб. УСПЕШНО ПЕРЕВЕДЕНА.", amount));
        
        try {
            handleBackAction();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackAction() throws IOException {
        App.changeScene("main-menu.fxml", backButton.getScene());
    }
}