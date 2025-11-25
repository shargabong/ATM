package atm;

public class Bill {
    private int denomination;
    private boolean isOriginal;

    public Bill(int denomination, boolean isOriginal) {
        this.denomination = denomination;
        this.isOriginal = isOriginal;
    }

    public int getDenomination() {
        return denomination;
    }

    public boolean checkSecurityMarks() {
        return isOriginal;
    }
}