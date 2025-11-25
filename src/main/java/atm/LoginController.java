package atm;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;

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
}