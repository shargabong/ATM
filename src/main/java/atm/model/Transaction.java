package atm.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction implements Serializable {

    private double amount;
    private Date timestamp;
    private String memo;
    private Account inAccount;

    public Transaction(double amount, String memo, Account inAccount) {
        this.amount = amount;
        this.memo = memo;
        this.inAccount = inAccount;
        this.timestamp = new Date();
    }

    public double getAmount() {
        return this.amount;
    }

    public String getSummaryLine() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedTimestamp = sdf.format(this.timestamp);
        if (this.amount >= 0) {
            return String.format("%s : %.02f руб. : %s", formattedTimestamp, this.amount, this.memo);
        } else {
            return String.format("%s : (%.02f) руб. : %s", formattedTimestamp, -this.amount, this.memo);
        }
    }
}