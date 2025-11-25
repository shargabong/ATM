package atm;
import atm.model.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Bank bank;
    public static User loggedInUser;
    public static CashDispenser dispenser = new CashDispenser();

    @Override
    public void start(Stage primaryStage) throws IOException {
        Font.loadFont(getClass().getResource("/atm/VCR_OSD_MONO_1.001.ttf").toExternalForm(), 10);
        Parent root = FXMLLoader.load(getClass().getResource("/atm/login.fxml"));
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("BOMBA TERMINAL");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    public static void changeScene(String fxmlFile, Scene currentScene) throws IOException {
        Parent newRoot = FXMLLoader.load(App.class.getResource("/atm/" + fxmlFile));
        Stage stage = (Stage) currentScene.getWindow();
        stage.getScene().setRoot(newRoot);
    }
    
    public static Bank getBank() {
        return bank;
    }

    public static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, content, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.getDialogPane().getStylesheets().add(App.class.getResource("/atm/style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("root");
        alert.getDialogPane().setMinWidth(450);

        Label contentLabel = (Label) alert.getDialogPane().lookup(".content.label");
        if (contentLabel != null) {
            contentLabel.setWrapText(true);
        }

        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        bank = new Bank("BOMBA BANK");
        User aUser = bank.addUser("Иван", "Иванов", "1234");
        Account checkingAccount = new Account("AKIM INFINITE", aUser, bank);
        aUser.addAccount(checkingAccount);
        bank.addAccount(checkingAccount);
        
        String rawCard = aUser.getUUID();
        String formattedCard = rawCard.replaceAll("(.{4})", "$1 ").trim();
        
        System.out.println("======================================================");
        System.out.println("--- ЗАПУСК ТЕРМИНАЛА ---");
        System.out.println("Карта выпущена успешно.");
        System.out.println("НОМЕР КАРТЫ: " + formattedCard);
        System.out.println("PIN-КОД:     1234");
        System.out.println("======================================================");
        
        launch(args);
    }
}