package atm.controller;
import atm.App;
import atm.model.*;
import atm.ui.*;
import atm.util.*;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainMenuController {

    @FXML private Label welcomeLabel;
    @FXML private Label userIdLabel;
    @FXML private ListView<String> accountsListView;
    
    // Кнопки
    @FXML private Button withdrawButton;
    @FXML private Button depositButton;
    @FXML private Button transferButton;
    @FXML private Button historyButton;
    @FXML private Button paymentsButton;
    @FXML private Button changePinButton;
    @FXML private Button logoutButton;
    
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        setActionButtonsDisabled(true);
        accountsListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    setActionButtonsDisabled(false);
                    setStatusText("> СЧЕТ ВЫБРАН. ВЫБЕРИТЕ ДЕЙСТВИЕ");
                }
            }
        );
        refreshAccountsList(-1);
    }

    private void setActionButtonsDisabled(boolean disabled) {
        withdrawButton.setDisable(disabled);
        depositButton.setDisable(disabled);
        transferButton.setDisable(disabled);
        historyButton.setDisable(disabled);
        paymentsButton.setDisable(disabled);
        changePinButton.setDisable(disabled);
    }

    private void setStatusText(String text) {
        UIUtils.typewriterAnimation(statusLabel, text, this::setupBlinkingCursor);
    }

    private void setupBlinkingCursor() {
        final Text cursor = new Text("_");
        cursor.getStyleClass().add("cursor");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), evt -> {
            if(statusLabel.getGraphic() == null) {
                statusLabel.setGraphic(cursor);
            } else {
                statusLabel.setGraphic(null);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void refreshAccountsList(int selectedIndex) {
        User user = App.loggedInUser;
        if (user != null) {
            welcomeLabel.setText("> ДОБРО ПОЖАЛОВАТЬ, " + user.getFirstName().toUpperCase());
            userIdLabel.setText("КАРТА: " + user.getUUID().replaceAll("(.{4})", "$1 ").trim());
            ObservableList<String> accountSummaries = FXCollections.observableArrayList();
            for (int i = 0; i < user.numAccounts(); i++) {
                accountSummaries.add(user.getAccountSummaryLine(i));
            }
            accountsListView.setItems(accountSummaries);
            if (selectedIndex >= 0) {
                accountsListView.getSelectionModel().select(selectedIndex);
            }
        }
    }

    private int getSelectedAccountIndex() {
        return accountsListView.getSelectionModel().getSelectedIndex();
    }

    @FXML
    private void handleWithdrawAction() {
        int accIdx = getSelectedAccountIndex();
        if (accIdx == -1) { return; }
        TransactionDialog dialog = new TransactionDialog("СНЯТИE НАЛИЧНЫХ", "ВЫБРАН СЧЕТ: " + App.loggedInUser.getAcctUUID(accIdx));
        Optional<Double> result = dialog.showAndWait();
        result.ifPresent(amount -> {
            if (amount <= 0 || amount > App.loggedInUser.getAcctBalance(accIdx)) {
                App.showAlert("ОШИБКА", amount <= 0 ? "СУММА ДОЛЖНА БЫТЬ ПОЛОЖИТЕЛЬНОЙ." : "НЕДОСТАТОЧНО СРЕДСТВ.");
                return;
            }
            Map<Integer, Integer> bills = App.dispenser.calculateWithdrawal(amount.intValue());
            if (bills == null) {
                App.showAlert("ОШИБКА СИСТЕМЫ", "ТЕРМИНАЛ НЕ МОЖЕТ ВЫДАТЬ ДАННУЮ СУММУ.");
                return;
            }
            App.loggedInUser.addAcctTransaction(accIdx, -amount, "Снятие наличных");
            App.dispenser.dispense(bills);
            String dispensedMessage = bills.entrySet().stream()
                .map(entry -> String.format("%d x %d руб.", entry.getValue(), entry.getKey()))
                .collect(Collectors.joining("\n"));
            App.showAlert("УСПЕХ", "ОПЕРАЦИЯ ВЫПОЛНЕНА.\nВОЗЬМИТЕ:\n" + dispensedMessage);
            refreshAccountsList(accIdx);
        });
    }

    @FXML
    private void handleDepositAction() {
        int accIdx = getSelectedAccountIndex();
        if (accIdx == -1) { return; }
        BillDepositDialog dialog = new BillDepositDialog(App.loggedInUser.getAcctUUID(accIdx));
        Optional<Double> result = dialog.showAndWait();
        result.ifPresent(amount -> {
            if (amount <= 0) {
                App.showAlert("ИНФО", "ОПЕРАЦИЯ ОТМЕНЕНА.");
                return;
            }
            App.loggedInUser.addAcctTransaction(accIdx, amount, "Внесение наличных (ATM)");
            App.showAlert("УСПЕХ", String.format("СУММА %.2f руб. ЗАЧИСЛЕНА.", amount));
            refreshAccountsList(accIdx);
        });
    }

    @FXML
    private void handleTransferAction() throws IOException {
        int accIdx = getSelectedAccountIndex();
        if (accIdx == -1) { return; }
        App.changeScene("transfer-view.fxml", logoutButton.getScene());
    }

    // НОВОЕ: Платежи
    @FXML
    private void handlePaymentsAction() throws IOException {
        int accIdx = getSelectedAccountIndex();
        if (accIdx == -1) { return; }
        App.changeScene("payments-view.fxml", logoutButton.getScene());
    }

    // НОВОЕ: Смена ПИН
    @FXML
    private void handleChangePinAction() {
        // Вызываем диалог смены пина
        ChangePinDialog dialog = new ChangePinDialog();
        Optional<Boolean> result = dialog.showAndWait();
        
        result.ifPresent(success -> {
            if (success) {
                App.showAlert("УСПЕХ", "PIN-КОД УСПЕШНО ИЗМЕНЕН.\nПОЖАЛУЙСТА, ЗАПОМНИТЕ НОВЫЙ КОД.");
            }
        });
    }

    @FXML
    private void handleHistoryAction() throws IOException {
        int accIdx = getSelectedAccountIndex();
        if (accIdx == -1) { return; }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/atm/history-view.fxml"));
        Parent root = loader.load();
        HistoryController controller = loader.getController();
        controller.initData(App.loggedInUser.getAccount(accIdx));
        logoutButton.getScene().setRoot(root);
    }

    @FXML
    private void handleLogoutAction() throws IOException {
        App.loggedInUser = null;
        App.changeScene("login.fxml", logoutButton.getScene());
    }
}