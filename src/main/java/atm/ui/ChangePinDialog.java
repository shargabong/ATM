package atm.ui;
import atm.App;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class ChangePinDialog extends Dialog<Boolean> {

    public ChangePinDialog() {
        setTitle("СМЕНА ПИН-КОДА");
        setHeaderText(null);
        getDialogPane().getStylesheets().add(getClass().getResource("/atm/style.css").toExternalForm());
        getDialogPane().getStyleClass().add("root");
        getDialogPane().setMinWidth(450);

        ButtonType changeButtonType = new ButtonType("СМЕНИТЬ", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 50, 10, 10));

        PasswordField oldPin = new PasswordField();
        oldPin.setPromptText("СТАРЫЙ PIN");
        PasswordField newPin = new PasswordField();
        newPin.setPromptText("НОВЫЙ PIN");
        PasswordField confirmPin = new PasswordField();
        confirmPin.setPromptText("ПОВТОР");

        grid.add(new Label("СТАРЫЙ PIN:"), 0, 0);
        grid.add(oldPin, 1, 0);
        grid.add(new Label("НОВЫЙ PIN:"), 0, 1);
        grid.add(newPin, 1, 1);
        grid.add(new Label("ПОВТОР:"), 0, 2);
        grid.add(confirmPin, 1, 2);
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ff3333; -fx-font-size: 12px; -fx-animation: none;");

        VBox container = new VBox(10, grid, errorLabel);
        getDialogPane().setContent(container);

        final Button btOk = (Button) getDialogPane().lookupButton(changeButtonType);
        // Не даем окну закрыться, если есть ошибки
        btOk.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String oldP = oldPin.getText();
            String newP = newPin.getText();
            String confP = confirmPin.getText();

            if (!newP.equals(confP)) {
                errorLabel.setText("> НОВЫЙ PIN НЕ СОВПАДАЕТ");
                event.consume();
                return;
            }
            if (newP.length() != 4 || !newP.matches("\\d+")) {
                errorLabel.setText("> PIN ДОЛЖЕН БЫТЬ 4 ЦИФРЫ");
                event.consume();
                return;
            }
            
            boolean success = App.loggedInUser.changePin(oldP, newP);
            if (!success) {
                errorLabel.setText("> НЕВЕРНЫЙ СТАРЫЙ PIN");
                event.consume();
            }
        });

        setResultConverter(dialogButton -> {
            if (dialogButton == changeButtonType) {
                return true;
            }
            return null;
        });
    }
}