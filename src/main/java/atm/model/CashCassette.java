package atm.model;

public class CashCassette {
    private int denomination;
    private int count;

    public CashCassette(int denomination, int count) {
        this.denomination = denomination;
        this.count = count;
    }

    public int getDenomination() {
        return denomination;
    }

    public int getCount() {
        return count;
    }

    public void removeBills(int amount) {
        if (amount <= count) {
            this.count -= amount;
        }
    }
}