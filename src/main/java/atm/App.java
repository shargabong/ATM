package atm;

import atm.model.*;
import atm.util.DataManager;
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
        Scene scene = new Scene(root, 700, 600);
        primaryStage.setTitle("BOMBA TERMINAL");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (bank != null) {
            DataManager.saveData(bank);
        }
    }
    
    public static void changeScene(String fxmlFile, Scene currentScene) throws IOException {
        Parent newRoot = FXMLLoader.load(App.class.getResource("/atm/" + fxmlFile));
        Stage stage = (Stage) currentScene.getWindow();
        stage.getScene().setRoot(newRoot);
    }
    
    public static Bank getBank() { return bank; }

    public static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, content, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.getDialogPane().getStylesheets().add(App.class.getResource("/atm/style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("root");
        alert.getDialogPane().setMinWidth(450);
        Label contentLabel = (Label) alert.getDialogPane().lookup(".content.label");
        if (contentLabel != null) contentLabel.setWrapText(true);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        bank = DataManager.loadData();

        if (bank == null) {
            System.out.println("Сохраненных данных нет. Создаем новый банк.");
            bank = new Bank("BOMBA BANK");
            User aUser = bank.addUser("Иван", "Иванов", "1234");
            Account checkingAccount = new Account("BOMBA INFINITE", "Checking", aUser, bank);
            aUser.addAccount(checkingAccount);
            bank.addAccount(checkingAccount);
            
            String formattedCard = aUser.getUUID().replaceAll("(.{4})", "$1 ").trim();
            System.out.println("НОВАЯ КАРТА: " + formattedCard + " (PIN: 1234)");
        } else {
            System.out.println("Данные успешно загружены.");
        }
        
        System.out.println("======================================================");
        System.out.println("--- ЗАПУСК ТЕРМИНАЛА ---");
        System.out.println("======================================================");
        
        launch(args);
    }
}