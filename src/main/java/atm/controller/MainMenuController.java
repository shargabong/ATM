package atm.controller;

import atm.App;
import atm.model.User;
import atm.model.Account;
import atm.model.Bank;
import atm.model.CashDispenser;
import atm.ui.BillDepositDialog;
import atm.ui.ChangePinDialog;
import atm.ui.TransactionDialog;
import atm.util.UIUtils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainMenuController {

    @FXML private Label welcomeLabel;
    @FXML private Label userIdLabel;
    
    // Новые элементы UI
    @FXML private ChoiceBox<String> accountSelector;
    @FXML private Label selectedAccountName;
    @FXML private Label selectedAccountBalance;
    @FXML private Label selectedAccountType;

    @FXML private Button withdrawButton;
    @FXML private Button depositButton;
    @FXML private Button transferButton;
    @FXML private Button historyButton;
    @FXML private Button paymentsButton;
    @FXML private Button changePinButton;
    @FXML private Button logoutButton;
    
    @FXML private Label statusLabel;

    private int currentAccountIndex = -1;

    @FXML
    public void initialize() {
        accountSelector.getSelectionModel().selectedIndexProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null && newVal.intValue() >= 0) {
                    updateActiveAccount(newVal.intValue());
                }
            }
        );

        refreshAccountsData(0);
        setupBlinkingCursor();
    }

    private void refreshAccountsData(int indexToSelect) {
        User user = App.loggedInUser;
        if (user != null) {
            welcomeLabel.setText("> ДОБРО ПОЖАЛОВАТЬ, " + user.getFirstName().toUpperCase());
            userIdLabel.setText("КАРТА: " + user.getUUID().replaceAll("(.{4})", "$1 ").trim());
            
            ObservableList<String> accountNames = FXCollections.observableArrayList();
            for (int i = 0; i < user.numAccounts(); i++) {
                accountNames.add(user.getAccount(i).getSelectorLabel());
            }
            accountSelector.setItems(accountNames);

            if (!accountNames.isEmpty()) {
                if (indexToSelect >= 0 && indexToSelect < accountNames.size()) {
                    accountSelector.getSelectionModel().select(indexToSelect);
                } else {
                    accountSelector.getSelectionModel().select(0);
                }
            } else {
                disableAllButtons();
            }
        }
    }

    private void updateActiveAccount(int index) {
        this.currentAccountIndex = index;
        User user = App.loggedInUser;
        Account acc = user.getAccount(index);

        selectedAccountName.setText(acc.getName());
        selectedAccountBalance.setText(String.format("%.2f руб.", acc.getBalance()));
        
        String typeRu = acc.getType().equals("Savings") ? "СБЕРЕГАТЕЛЬНЫЙ" : "ТЕКУЩИЙ";
        selectedAccountType.setText("ТИП: " + typeRu);

        boolean isSavings = acc.getType().equals("Savings");
        
        withdrawButton.setDisable(false);
        depositButton.setDisable(false);
        historyButton.setDisable(false);
        paymentsButton.setDisable(false);
        changePinButton.setDisable(false);
        
        // БЛОКИРОВКА ПЕРЕВОДА ДЛЯ СБЕРЕГАТЕЛЬНОГО СЧЕТА
        if (isSavings) {
            transferButton.setDisable(true);
            setStatusText("> ВНИМАНИЕ: СБЕРЕГАТЕЛЬНЫЙ СЧЕТ (ПЕРЕВОДЫ ЗАПРЕЩЕНЫ)");
        } else {
            transferButton.setDisable(false);
            setStatusText("> СЧЕТ АКТИВЕН. ВЫБЕРИТЕ ДЕЙСТВИЕ");
        }
    }

    private void disableAllButtons() {
        withdrawButton.setDisable(true);
        depositButton.setDisable(true);
        transferButton.setDisable(true);
        historyButton.setDisable(true);
        paymentsButton.setDisable(true);
    }

    @FXML
    private void handleNewAccountAction() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Сберегательный", "Сберегательный", "Текущий");
        dialog.setTitle("ОТКРЫТИЕ СЧЕТА");
        dialog.setHeaderText("ВЫБЕРИТЕ ТИП НОВОГО СЧЕТА");
        dialog.setContentText("Тип:");
        
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/atm/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("root");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(typeRu -> {
            String typeCode = typeRu.equals("Сберегательный") ? "Savings" : "Checking";
            String name = typeRu + " счет";
            
            Bank bank = App.getBank();
            User user = App.loggedInUser;
            
            Account newAcc = new Account(name, typeCode, user, bank);
            user.addAccount(newAcc);
            bank.addAccount(newAcc);
            
            App.showAlert("УСПЕХ", "НОВЫЙ СЧЕТ УСПЕШНО ОТКРЫТ.");
            refreshAccountsData(user.numAccounts() - 1);
        });
    }

    @FXML
    private void handleWithdrawAction() {
        if (currentAccountIndex == -1) return;
        TransactionDialog dialog = new TransactionDialog("СНЯТИE НАЛИЧНЫХ", "ВЫБРАН СЧЕТ: " + App.loggedInUser.getAcctUUID(currentAccountIndex));
        Optional<Double> result = dialog.showAndWait();
        result.ifPresent(amount -> {
            if (amount <= 0 || amount > App.loggedInUser.getAcctBalance(currentAccountIndex)) {
                 App.showAlert("ОШИБКА", "НЕДОСТАТОЧНО СРЕДСТВ ИЛИ НЕВЕРНАЯ СУММА."); return;
            }
            Map<Integer, Integer> bills = App.dispenser.calculateWithdrawal(amount.intValue());
            if (bills == null) {
                App.showAlert("ОШИБКА", "НЕТ КУПЮР ДЛЯ ВЫДАЧИ."); return;
            }
            App.loggedInUser.addAcctTransaction(currentAccountIndex, -amount, "Снятие наличных");
            App.dispenser.dispense(bills);
            String dispensedMessage = bills.entrySet().stream()
                .map(entry -> String.format("%d x %d руб.", entry.getValue(), entry.getKey()))
                .collect(Collectors.joining("\n"));
            App.showAlert("УСПЕХ", "ОПЕРАЦИЯ ВЫПОЛНЕНА.\nВОЗЬМИТЕ:\n" + dispensedMessage);
            refreshAccountsData(currentAccountIndex);
        });
    }

    @FXML
    private void handleDepositAction() {
        if (currentAccountIndex == -1) return;
        BillDepositDialog dialog = new BillDepositDialog(App.loggedInUser.getAcctUUID(currentAccountIndex));
        Optional<Double> result = dialog.showAndWait();
        result.ifPresent(amount -> {
            if (amount <= 0) return;
            App.loggedInUser.addAcctTransaction(currentAccountIndex, amount, "Внесение наличных");
            App.showAlert("УСПЕХ", "ЗАЧИСЛЕНО: " + amount);
            refreshAccountsData(currentAccountIndex);
        });
    }

    @FXML
    private void handleTransferAction() throws IOException {
        if (currentAccountIndex == -1) return;
        App.changeScene("transfer-view.fxml", logoutButton.getScene());
    }

    @FXML
    private void handleHistoryAction() throws IOException {
        if (currentAccountIndex == -1) return;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/atm/history-view.fxml"));
        Parent root = loader.load();
        HistoryController controller = loader.getController();
        controller.initData(App.loggedInUser.getAccount(currentAccountIndex));
        logoutButton.getScene().setRoot(root);
    }
    
    @FXML private void handlePaymentsAction() throws IOException { 
        if (currentAccountIndex == -1) return;
        App.changeScene("payments-view.fxml", logoutButton.getScene()); 
    }
    
    @FXML private void handleChangePinAction() { 
        ChangePinDialog dialog = new ChangePinDialog();
        Optional<Boolean> result = dialog.showAndWait();
        result.ifPresent(success -> {
            if (success) App.showAlert("УСПЕХ", "PIN-КОД УСПЕШНО ИЗМЕНЕН.");
        });
    }
    
    @FXML private void handleLogoutAction() throws IOException { 
        App.loggedInUser = null; 
        App.changeScene("login.fxml", logoutButton.getScene()); 
    }

    private void setStatusText(String text) {
        UIUtils.typewriterAnimation(statusLabel, text, this::setupBlinkingCursor);
    }

    private void setupBlinkingCursor() {
        final Text cursor = new Text("_");
        cursor.getStyleClass().add("cursor");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), evt -> {
            if(statusLabel.getGraphic() == null) statusLabel.setGraphic(cursor);
            else statusLabel.setGraphic(null);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}