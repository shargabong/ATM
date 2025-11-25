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

public class PaymentsController {

    @FXML private ChoiceBox<String> accountBox;
    @FXML private ChoiceBox<String> categoryBox;
    @FXML private TextField identifierField;
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
            accountBox.setItems(accounts);
            
            categoryBox.setItems(FXCollections.observableArrayList(
                "МОБИЛЬНАЯ СВЯЗЬ", 
                "ИНТЕРНЕТ И ТВ", 
                "ЖКХ / КОММУНАЛЬНЫЕ",
                "ШТРАФЫ ГИБДД"
            ));
            
            categoryBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    switch (newVal) {
                        case "МОБИЛЬНАЯ СВЯЗЬ" -> identifierField.setPromptText("+7 (XXX) XXX-XX-XX");
                        case "ИНТЕРНЕТ И ТВ" -> identifierField.setPromptText("НОМЕР ДОГОВОРА");
                        case "ЖКХ / КОММУНАЛЬНЫЕ" -> identifierField.setPromptText("КОД ПЛАТЕЛЬЩИКА");
                        default -> identifierField.setPromptText("ИДЕНТИФИКАТОР");
                    }
                }
            });
        }
    }

    @FXML
    private void handlePayAction() {
        int accIdx = accountBox.getSelectionModel().getSelectedIndex();
        String category = categoryBox.getValue();
        String identifier = identifierField.getText();
        String amountStr = amountField.getText();

        if (accIdx == -1 || category == null || identifier.isEmpty() || amountStr.isEmpty()) {
            statusLabel.setText("> ОШИБКА: ЗАПОЛНИТЕ ВСЕ ПОЛЯ");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            statusLabel.setText("> ОШИБКА: НЕКОРРЕКТНАЯ СУММА");
            return;
        }

        if (amount <= 0) {
            statusLabel.setText("> ОШИБКА: СУММА ДОЛЖНА БЫТЬ > 0");
            return;
        }

        if (amount > currentUser.getAcctBalance(accIdx)) {
            statusLabel.setText("> ОШИБКА: НЕДОСТАТОЧНО СРЕДСТВ");
            return;
        }

        String memo = "Оплата: " + category + " (" + identifier + ")";
        currentUser.addAcctTransaction(accIdx, -amount, memo);

        App.showAlert("УСПЕХ", String.format("ОПЛАТА '%.2f руб.'\nВ ПОЛЬЗУ '%s'\nВЫПОЛНЕНА УСПЕШНО.", amount, category));
        
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