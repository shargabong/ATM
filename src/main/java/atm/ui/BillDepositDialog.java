package atm.ui;
import atm.model.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;


public class BillDepositDialog extends Dialog<Double> {

    private double totalAmount = 0;
    private Label totalLabel;
    private Label messageLabel;
    private CashValidator validator = new CashValidator();

    public BillDepositDialog(String accountName) {
        setTitle("ВНЕСЕНИЕ НАЛИЧНЫХ");
        setHeaderText(null);

        // Общий стиль
        getDialogPane().getStylesheets().add(getClass().getResource("/atm/style.css").toExternalForm());
        getDialogPane().getStyleClass().add("root");
        getDialogPane().setMinWidth(600);
        getDialogPane().setMinHeight(550);

        // Кнопки диалога
        ButtonType depositButtonType = new ButtonType("ЗАЧИСЛИТЬ", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(depositButtonType, ButtonType.CANCEL);

        // --- ВЕРХНЯЯ ЧАСТЬ (ЭКРАН БАНКОМАТА) ---
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Label header = new Label("КУПЮРОПРИЕМНИК ОТКРЫТ");
        header.setStyle("-fx-font-size: 20px;");
        
        Label subHeader = new Label("СЧЕТ ЗАЧИСЛЕНИЯ: " + accountName);
        subHeader.setStyle("-fx-font-size: 12px; -fx-text-fill: #229922;");

        totalLabel = new Label("ВНЕСЕНО: 0.00 руб.");
        totalLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #33ff33; -fx-border-color: #33ff33; -fx-padding: 10;");

        messageLabel = new Label("> ВСТАВЬТЕ КУПЮРЫ ПО ОДНОЙ");
        messageLabel.setStyle("-fx-font-size: 14px;");
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // --- НИЖНЯЯ ЧАСТЬ (КОШЕЛЕКА) ---
        Label walletLabel = new Label("--- ВАШ КОШЕЛЕК (НАЖМИТЕ НА КУПЮРУ) ---");
        walletLabel.setStyle("-fx-font-size: 12px; -fx-padding: 20 0 0 0;");

        GridPane billsGrid = new GridPane();
        billsGrid.setHgap(15);
        billsGrid.setVgap(15);
        billsGrid.setAlignment(Pos.CENTER);

        int[] denoms = {100, 200, 500, 1000, 2000, 5000};
        int col = 0;
        int row = 0;
        
        for (int denom : denoms) {
            Button btn = createBillButton(denom, true);
            billsGrid.add(btn, col, row);
            col++;
            if (col > 2) { col = 0; row++; }
        }

        Button fakeBtn = createBillButton(5000, false); // false = подделка
        fakeBtn.setText("5000 [ФАЛЬШ]");
        fakeBtn.setStyle("-fx-text-fill: #ff3333; -fx-border-color: #ff3333;");
        billsGrid.add(fakeBtn, 1, row + 1);

        root.getChildren().addAll(header, subHeader, totalLabel, messageLabel, walletLabel, billsGrid);
        getDialogPane().setContent(root);

        setResultConverter(dialogButton -> {
            if (dialogButton == depositButtonType) {
                return totalAmount;
            }
            return null;
        });
    }

    private Button createBillButton(int denomination, boolean isReal) {
        Button btn = new Button(denomination + " ₽");
        btn.setPrefWidth(120);
        btn.setPrefHeight(50);
        
        btn.setOnAction(e -> {
            insertBill(new Bill(denomination, isReal));
        });
        return btn;
    }

    private void insertBill(Bill bill) {
        messageLabel.setText("> ПРОВЕРКА КУПЮРЫ...");
        messageLabel.setStyle("-fx-text-fill: #33ff33; -fx-font-size: 14px;");

        try {
            if (validator.validate(bill)) {
                totalAmount += bill.getDenomination();
                totalLabel.setText(String.format("ВНЕСЕНО: %.2f руб.", totalAmount));
                messageLabel.setText("> КУПЮРА " + bill.getDenomination() + " ПРИНЯТА.");
            }
        } catch (IllegalArgumentException e) {
            messageLabel.setText("> ОШИБКА: " + e.getMessage() + "\n> КУПЮРА ВОЗВРАЩЕНА.");
            messageLabel.setStyle("-fx-text-fill: #ff3333; -fx-font-size: 14px;"); // Красный цвет
        }
    }
}