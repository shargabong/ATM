package atm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Account implements Serializable {

    private String name;
    private String uuid;
    private String type;
    private User holder;
    private ArrayList<Transaction> transactions;

    public Account(String name, String type, User holder, Bank theBank) {
        this.name = name;
        this.type = type;
        this.holder = holder;
        this.uuid = theBank.getNewAccountUUID();
        this.transactions = new ArrayList<Transaction>();
    }

    public String getUUID() { return this.uuid; }
    public String getName() { return this.name; }
    public String getType() { return this.type; }

    public String getSummaryLine() {
        double balance = this.getBalance();
        return String.format("%s (%s) : %.2f руб.", this.name, this.type, balance);
    }
    
    public String getSelectorLabel() {
        String typeRu = type.equals("Savings") ? "Сберегательный" : "Текущий";
        return String.format("%s (%s)", this.name, typeRu);
    }

    public double getBalance() {
        double balance = 0;
        for (Transaction t : this.transactions) {
            balance += t.getAmount();
        }
        return balance;
    }

    public void addTransaction(double amount, String memo) {
        Transaction newTrans = new Transaction(amount, memo, this);
        this.transactions.add(newTrans);
    }

    public List<String> getTransactionsSummary() {
        List<String> summaries = new ArrayList<>();
        if (this.transactions.isEmpty()) {
            summaries.add("ТРАНЗАКЦИЙ ЕЩЕ НЕТ.");
            return summaries;
        }
        for (int t = this.transactions.size() - 1; t >= 0; t--) {
            summaries.add(this.transactions.get(t).getSummaryLine());
        }
        return summaries;
    }
}