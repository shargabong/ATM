package atm;

import java.util.ArrayList;

/**
 * Класс, представляющий банковский счет.
 */
public class Account {

    private String name;
    private String uuid;
    private User holder;
    private ArrayList<Transaction> transactions;

    public Account(String name, User holder, Bank theBank) {
        this.name = name;
        this.holder = holder;
        this.uuid = theBank.getNewAccountUUID();
        this.transactions = new ArrayList<Transaction>();
    }

    public String getUUID() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public String getSummaryLine() {
        double balance = this.getBalance();
        if (balance >= 0) {
            return String.format("%s : %.02f руб. : %s", this.uuid, balance, this.name);
        } else {
            return String.format("%s : (%.02f) руб. : %s", this.uuid, Math.abs(balance), this.name);
        }
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

    public java.util.List<String> getTransactionsSummary() {
        java.util.List<String> summaries = new java.util.ArrayList<>();
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