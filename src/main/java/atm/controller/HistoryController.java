package atm.controller;
import atm.App;
import atm.model.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import java.io.IOException;

public class HistoryController {

    @FXML
    private ListView<String> historyListView;
    
    @FXML
    private Button backButton;

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