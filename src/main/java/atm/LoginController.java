package atm;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;

/**
 * Контроллер для экрана входа (login.fxml).
 */
public class LoginController {

    @FXML private TextField userIdField;
    @FXML private PasswordField pinField;
    @FXML private Button loginButton;
    @FXML private Label statusLabel;
    
    @FXML
    private void handleLoginButtonAction() throws IOException {
        String userID = userIdField.getText();
        String pin = pinField.getText();

        if (userID.isEmpty() || pin.isEmpty()) {
            statusLabel.setText("> ОШИБКА: ВВЕДИТЕ ID И PIN");
            return;
        }
        
        Bank bank = App.getBank();
        User authUser = bank.userLogin(userID, pin);

        if (authUser != null) {
            App.loggedInUser = authUser;
            statusLabel.setText("> ДОСТУП РАЗРЕШЕН. ВХОД...");
            App.changeScene("main-menu.fxml", loginButton.getScene());
        } else {
            statusLabel.setText("> ОШИБКА: НЕВЕРНЫЙ ID ИЛИ PIN");
        }
    }
}