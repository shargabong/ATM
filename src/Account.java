import java.util.ArrayList;

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

        holder.addAccount(this);
        holder.addAccount(this);
        theBank.addAccount(this);
    }

  public String getUUID() {
        return this.uuid;
  }
}