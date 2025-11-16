package atm;

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

/**
 * Главный класс, точка входа для JavaFX приложения.
 * Инициализирует банк, тестового пользователя и запускает графический интерфейс.
 */
public class App extends Application {

    private static Bank bank;
    public static User loggedInUser;
    public static CashDispenser dispenser = new CashDispenser();

    @Override
    public void start(Stage primaryStage) throws IOException {
        Font.loadFont(getClass().getResource("/atm/VCR_OSD_MONO_1.001.ttf").toExternalForm(), 10);
        Parent root = FXMLLoader.load(getClass().getResource("/atm/login.fxml"));
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("ATM TERMINAL [SEVASTOPOL]");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    /**
     * Утилитарный метод для смены сцен (экранов) в главном окне.
     * @param fxmlFile Имя fxml-файла для новой сцены.
     * @param currentScene Текущая сцена для получения Stage.
     */
    public static void changeScene(String fxmlFile, Scene currentScene) throws IOException {
        Parent newRoot = FXMLLoader.load(App.class.getResource("/atm/" + fxmlFile));
        Stage stage = (Stage) currentScene.getWindow();
        stage.getScene().setRoot(newRoot);
    }
    
    public static Bank getBank() {
        return bank;
    }

    /**
     * Показывает стилизованное информационное или ошибочное сообщение.
     * @param title Заголовок окна.
     * @param content Текст сообщения.
     */
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
        bank = new Bank("WEYLAND-YUTANI CORP.");
        User aUser = bank.addUser("Иван", "Иванов", "1234");
        Account checkingAccount = new Account("Текущий", aUser, bank);
        aUser.addAccount(checkingAccount);
        bank.addAccount(checkingAccount);
        
        System.out.println("======================================================");
        System.out.println("--- ЗАПУСК ТЕРМИНАЛА ---");
        System.out.println("Пользователь для входа: ID=" + aUser.getUUID() + ", PIN=1234");
        System.out.println("======================================================");
        
        launch(args);
    }
}