package atm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import java.io.IOException;

/**
 * Контроллер для экрана истории транзакций (history-view.fxml).
 */
public class HistoryController {

    @FXML
    private ListView<String> historyListView;
    
    @FXML
    private Button backButton;

    /**
     * Инициализирует контроллер данными о выбранном счете.
     * @param account Счет, чью историю нужно отобразить.
     */
    public void initData(Account account) {
        ObservableList<String> history = FXCollections.observableArrayList();
        history.addAll(account.getTransactionsSummary());
        historyListView.setItems(history);
    }

    @FXML
    private void handleBackAction() throws IOException {
        App.changeScene("main-menu.fxml", backButton.getScene());
    }
}