package atm;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Класс, создающий универсальное диалоговое окно для операций с вводом суммы.
 */
public class TransactionDialog extends Dialog<Double> {

    private TextField amountField = new TextField();

    public TransactionDialog(String title, String headerText) {
        setTitle(title);
        setHeaderText(headerText);

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        getDialogPane().getStylesheets().add(getClass().getResource("/atm/style.css").toExternalForm());
        getDialogPane().getStyleClass().add("root");
        getDialogPane().setMinWidth(450);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        amountField.setPromptText("СУММА");
        grid.add(new Label("СУММА:"), 0, 0);
        grid.add(amountField, 1, 0);

        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    return Double.parseDouble(amountField.getText());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });
    }
}