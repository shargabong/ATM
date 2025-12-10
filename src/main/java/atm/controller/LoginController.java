package atm.controller;

import atm.App;
import atm.model.Bank;
import atm.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import java.io.IOException;
import java.util.Optional;

public class LoginController {

    @FXML private TextField userIdField;
    @FXML private PasswordField pinField;
    @FXML private Button loginButton;
    @FXML private Label statusLabel;
    @FXML private ChoiceBox<String> userSelector;

    @FXML
    public void initialize() {
        Bank bank = App.getBank();
        if (bank != null && bank.getUsers() != null) {
            ObservableList<String> userList = FXCollections.observableArrayList();
            for (User u : bank.getUsers()) {
                String card = u.getUUID();
                String label = String.format("%s %s - %s", u.getFirstName(), u.getFirstName(), card);
                userList.add(label);
            }
            userSelector.setItems(userList);

            userSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    String[] parts = newVal.split(" - ");
                    if (parts.length > 1) {
                        userIdField.setText(parts[1]);
                    }
                }
            });
        }
    }
    
    @FXML
    private void handleLoginButtonAction() throws IOException {
        String userID = userIdField.getText();
        String pin = pinField.getText();

        if (userID.isEmpty() || pin.isEmpty()) {
            statusLabel.setText("> ОШИБКА: ВВЕДИТЕ НОМЕР КАРТЫ И PIN");
            return;
        }
        
        Bank bank = App.getBank();
        String cleanID = userID.replace(" ", "");
        User authUser = bank.userLogin(cleanID, pin);

        if (authUser != null) {
            App.loggedInUser = authUser;
            statusLabel.setText("> ДОСТУП РАЗРЕШЕН. ВХОД...");
            App.changeScene("main-menu.fxml", loginButton.getScene());
        } else {
            statusLabel.setText("> ОШИБКА: НЕВЕРНАЯ КАРТА ИЛИ PIN");
        }
    }

    @FXML
    private void handleRegisterAction() {
        TextInputDialog dialog = new TextInputDialog("Иван Иванов");
        dialog.setTitle("РЕГИСТРАЦИЯ");
        dialog.setHeaderText("ВЫПУСК НОВОЙ КАРТЫ");
        dialog.setContentText("Введите Имя и Фамилию:");
        
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/atm/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("root");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String[] parts = result.get().split(" ");
            String fName = parts.length > 0 ? parts[0] : "User";
            String lName = parts.length > 1 ? parts[1] : "Name";
            
            Bank bank = App.getBank();
            User newUser = bank.addUser(fName, lName, "1234");
            
            String cardNum = newUser.getUUID();
            statusLabel.setText("ГОТОВО! КАРТА: " + cardNum + " (PIN: 1234)");
            userIdField.setText(cardNum);
            
            initialize();
        }
    }
}