package atm;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Класс, представляющий пользователя (клиента) банка.
 */
public class User {

    private String firstName;
    private String lastName;
    private String uuid;
    private byte pinHash[];
    private ArrayList<Account> accounts;

    public User(String firstName, String lastName, String pin, Bank theBank) {
        this.firstName = firstName;
        this.lastName = lastName;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            this.pinHash = md.digest(pin.getBytes());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Ошибка, пойман NoSuchAlgorithmException: " + e.getMessage());
            System.exit(1);
        }

        this.uuid = theBank.getNewUserUUID();
        this.accounts = new ArrayList<Account>();
    }

    public void addAccount(Account anAcct) {
        this.accounts.add(anAcct);
    }

    public String getUUID() {
        return this.uuid;
    }

    public boolean validatePin(String aPin) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return MessageDigest.isEqual(md.digest(aPin.getBytes()), this.pinHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean changePin(String oldPin, String newPin) {
        if (!validatePin(oldPin)) {
            return false;
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            this.pinHash = md.digest(newPin.getBytes());
            System.out.println("PIN успешно изменен.");
            return true;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getFirstName() {
        return this.firstName;
    }

    public int numAccounts() {
        return this.accounts.size();
    }
    
    public double getAcctBalance(int acctIdx) {
        return this.accounts.get(acctIdx).getBalance();
    }
    
    public String getAcctUUID(int acctIdx) {
        return this.accounts.get(acctIdx).getUUID();
    }

    public void addAcctTransaction(int acctIdx, double amount, String memo) {
        this.accounts.get(acctIdx).addTransaction(amount, memo);
    }

    public String getAccountSummaryLine(int acctIdx) {
        if (acctIdx >= 0 && acctIdx < this.accounts.size()) {
            return this.accounts.get(acctIdx).getSummaryLine();
        }
        return "ОШИБКА: НЕВЕРНЫЙ ИНДЕКС СЧЕТА";
    }

    public Account getAccount(int acctIdx) {
        if (acctIdx >= 0 && acctIdx < this.accounts.size()) {
            return this.accounts.get(acctIdx);
        }
        return null;
    }
}